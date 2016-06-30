/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.azurecompute.arm.compute;

import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.scriptbuilder.statements.java.InstallJDK;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;

import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.RESOURCE_GROUP_NAME;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_TERMINATED;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_PORT_OPEN;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import com.google.inject.Module;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.scriptbuilder.ScriptBuilder;


/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceLiveTest")
public class AzureComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   protected int nonBlockDurationSeconds = 30;

   public AzureComputeServiceLiveTest() {
      provider = "azurecompute-arm";
      nonBlockDurationSeconds = 300;
      group = "az-u";
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AzureComputeProviderMetadata pm = AzureComputeProviderMetadata.builder().build();
      return pm;
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      long scriptTimeout = TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES);
      properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_RUNNING, scriptTimeout + "");
      properties.setProperty(TIMEOUT_PORT_OPEN, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_TERMINATED, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_SUSPENDED, scriptTimeout + "");
      properties.put(RESOURCE_GROUP_NAME, "a1");

      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");

      return properties;
   }

   @Override
   protected Template refreshTemplate() {
      return this.template = addRunScriptToTemplate(this.buildTemplate(this.client.templateBuilder()));
   }

   @Override
   protected Template addRunScriptToTemplate(Template template) {
      template.getOptions().runScript(Statements.newStatementList(new Statement[]{AdminAccess.standard(), ScriptBuilder.waitForLockFile("/var/lib/dpkg/lock", 100), InstallJDK.fromOpenJDK()}));
      return template;
   }

   @Override
   @Test( enabled = false)
   protected void weCanCancelTasks(NodeMetadata node) throws InterruptedException, ExecutionException {
      return;
   }

   @Override
   protected Map<? extends NodeMetadata, ExecResponse> runScriptWithCreds(String group, OperatingSystem os, LoginCredentials creds) throws RunScriptOnNodesException {
      return this.client.runScriptOnNodesMatching(NodePredicates.runningInGroup(group), Statements.newStatementList(ScriptBuilder.waitForLockFile("/var/lib/dpkg/lock", 100), InstallJDK.fromOpenJDK()), org.jclouds.compute.options.TemplateOptions.Builder.overrideLoginCredentials(creds).nameTask("runScriptWithCreds"));
   }

   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      String group = this.group + "r";

      try {
         this.client.destroyNodesMatching(NodePredicates.inGroup(group));
      } catch (Exception var8) {
         ;
      }

      this.template = this.buildTemplate(this.client.templateBuilder());
      this.template.getOptions().blockOnPort(22, 120);
      Set nodes = this.client.createNodesInGroup(group, 1, this.template);
      NodeMetadata node = (NodeMetadata) Iterables.get(nodes, 0);
      LoginCredentials good = node.getCredentials();

      assert good.identity != null : nodes;

      Iterator response = this.client.runScriptOnNodesMatching(NodePredicates.runningInGroup(group), "hostname", RunScriptOptions.Builder.wrapInInitScript(false).runAsRoot(false).overrideLoginCredentials(good)).entrySet().iterator();

      while(response.hasNext()) {
         Map.Entry os = (Map.Entry)response.next();
         this.checkResponseEqualsHostname((ExecResponse)os.getValue(), (NodeMetadata)os.getKey());
      }

      ExecResponse response1 = this.client.runScriptOnNode(node.getId(), "hostname", RunScriptOptions.Builder.wrapInInitScript(false).runAsRoot(false));
      this.checkResponseEqualsHostname(response1, node);
      OperatingSystem os1 = node.getOperatingSystem();
      this.tryBadPassword(group, good);
      this.runScriptWithCreds(group, os1, good);
      this.checkNodes(nodes, group, "runScriptWithCreds");
      ListenableFuture future = this.client.submitScriptOnNode(node.getId(), AdminAccess.builder().adminUsername("foo").adminHome("/over/ridden/foo").build(), RunScriptOptions.Builder.nameTask("adminUpdate"));
      response1 = (ExecResponse)future.get(3L, TimeUnit.MINUTES);

      assert response1.getExitStatus() == 0 : node.getId() + ": " + response1;

      node = this.client.getNodeMetadata(node.getId());
      Assert.assertEquals(node.getCredentials().identity, "foo");

      assert node.getCredentials().credential != null : nodes;

      this.weCanCancelTasks(node);

      assert response1.getExitStatus() == 0 : node.getId() + ": " + response1;

      response1 = this.client.runScriptOnNode(node.getId(), "echo $USER", RunScriptOptions.Builder.wrapInInitScript(false).runAsRoot(false));

      assert response1.getOutput().trim().equals("foo") : node.getId() + ": " + response1;

   }
}
