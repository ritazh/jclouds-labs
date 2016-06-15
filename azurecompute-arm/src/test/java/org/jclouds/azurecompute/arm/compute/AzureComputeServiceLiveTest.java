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

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.compute.JettyStatements;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.Location;
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
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceLiveTest")
public class AzureComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   //public String azureGroup;

   public AzureComputeServiceLiveTest() {
      provider = "azurecompute-arm";
      nonBlockDurationSeconds = 300;
      group = "az-u";
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
      //azureGroup = "jc" + System.getProperty("user.name").substring(0, 3);
      Properties properties = super.setupProperties();
      long scriptTimeout = TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES);
      properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_RUNNING, scriptTimeout + "");
      properties.setProperty(TIMEOUT_PORT_OPEN, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_TERMINATED, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_SUSPENDED, scriptTimeout + "");
      properties.put(RESOURCE_GROUP_NAME, "a4");
//      properties.put("jclouds.max-retries", 5);
//      properties.put("jclouds.retries-delay-start", 5000L);

      //properties.put(RESOURCE_GROUP_NAME, azureGroup);

      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");

      return properties;

   }


   @Test(
         enabled = true
   )
   public void testCreateTwoNo() throws Exception {
      this.template = this.buildTemplate(this.client.templateBuilder());
      this.template.getOptions().runScript(Statements.newStatementList(new Statement[]{AdminAccess.standard(), InstallJDK.fromOpenJDK()}));

      try {
         this.nodes = Sets.newTreeSet(this.client.createNodesInGroup(this.group, 2, this.template));
      } catch (RunNodesException var3) {
         this.nodes = Sets.newTreeSet(Iterables.concat(var3.getSuccessfulNodes(), var3.getNodeErrors().keySet()));
         throw var3;
      }

      Assert.assertEquals(this.nodes.size(), 2, "expected two nodes but was " + this.nodes);
      this.checkNodes(this.nodes, this.group, "bootstrap");
      NodeMetadata node1 = (NodeMetadata)this.nodes.first();
      NodeMetadata node2 = (NodeMetadata)this.nodes.last();
//      this.assertLocationSameOrChild((Location) Preconditions.checkNotNull(node1.getLocation(), "location of %s", new Object[]{node1}), this.template.getLocation());
//      this.assertLocationSameOrChild((Location)Preconditions.checkNotNull(node2.getLocation(), "location of %s", new Object[]{node2}), this.template.getLocation());
      this.checkImageIdMatchesTemplate(node1);
      this.checkImageIdMatchesTemplate(node2);
      this.checkOsMatchesTemplate(node1);
      this.checkOsMatchesTemplate(node2);
   }


   @Override
   @Test(
         enabled = false
   )
   public void weCanCancelTasks(NodeMetadata node) throws InterruptedException, ExecutionException {
      return;
   }
}
