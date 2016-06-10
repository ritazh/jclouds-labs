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
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.predicates.NodePredicates;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import com.google.inject.Module;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Live tests for the {@link org.jclouds.compute.ComputeService} integration.
 */
@Test(groups = "live", singleThreaded = true, testName = "AzureComputeServiceLiveTest")
public class AzureComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   //public String azureGroup;

   public AzureComputeServiceLiveTest() {
      provider = "azurecompute-arm";
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

   @Override protected Properties setupProperties() {
      //azureGroup = "jc" + System.getProperty("user.name").substring(0, 3);
      Properties properties = super.setupProperties();
      long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
      properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_RUNNING, scriptTimeout + "");
      //properties.put(RESOURCE_GROUP_NAME, azureGroup);

      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");

      return properties;

   }

   @Test(
         enabled = true
   )
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      String group = this.group + "r";
/*
      try {
         this.client.destroyNodesMatching(NodePredicates.inGroup(group));
      } catch (Exception var11) {
         ;
      }
*/
      this.template = this.buildTemplate(this.client.templateBuilder());
      this.template.getOptions().blockOnPort(22, 120);

      try {
         Set nodes = this.client.createNodesInGroup(group, 1, this.template);
         NodeMetadata node = /*this.client.getNodeMetadata("azurecompute-armr-270");*/
               (NodeMetadata) Iterables.get(nodes, 0);
         LoginCredentials good = node.getCredentials();
//         Credentials credentials = new Credentials("jclouds", "Password1!");
//         good = LoginCredentials.fromCredentials(credentials);

         Iterator response = this.client.runScriptOnNodesMatching(NodePredicates.runningInGroup(group), "hostname", RunScriptOptions.Builder.wrapInInitScript(false).runAsRoot(false).overrideLoginCredentials(good)).entrySet().iterator();

         while (response.hasNext()) {
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
      } catch (Exception e) {
         System.out.println(e.getMessage());
      }

   }

}
