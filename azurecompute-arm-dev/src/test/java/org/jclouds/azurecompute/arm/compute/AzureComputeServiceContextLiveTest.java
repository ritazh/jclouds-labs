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

import com.google.inject.Module;
import org.jclouds.azurecompute.arm.AzureComputeProviderMetadata;
import org.jclouds.azurecompute.arm.compute.options.AzureComputeArmTemplateOptions;
import org.jclouds.azurecompute.arm.config.AzureComputeProperties;
import org.jclouds.azurecompute.arm.internal.AzureLiveTestUtils;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_RUNNING;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;



@Test(groups = "live", testName = "AzureComputeServiceContextLiveTest", singleThreaded = true)
public class AzureComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override protected Properties setupProperties() {
      Properties properties = super.setupProperties();

      properties.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      properties.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      properties.setProperty(AzureComputeProperties.OPERATION_TIMEOUT, "46000000");
      properties.setProperty(AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD, "5");
      properties.setProperty(AzureComputeProperties.OPERATION_POLL_MAX_PERIOD, "15");
      properties.setProperty(AzureComputeProperties.TCP_RULE_FORMAT, "tcp_%s-%s");
      properties.setProperty(AzureComputeProperties.TCP_RULE_REGEXP, "tcp_\\d{1,5}-\\d{1,5}");
      long scriptTimeout = TimeUnit.MILLISECONDS.convert(20, TimeUnit.MINUTES);
      properties.setProperty(TIMEOUT_SCRIPT_COMPLETE, scriptTimeout + "");
      properties.setProperty(TIMEOUT_NODE_RUNNING, scriptTimeout + "");

      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "jclouds.oauth.resource"), "test.jclouds.oauth.resource");
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");

      return properties;

   }

   public AzureComputeServiceContextLiveTest() {
      provider = "azurecompute-arm";
   }


   @Test
   public void testDefault() throws RunNodesException {
      final String groupName = String.format("def%s", System.getProperty("user.name"));
      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      final Template template = templateBuilder.build();

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
         assertThat(nodes).hasSize(1);
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

//   @Test(dependsOnMethods = "testDefault")
   public void testLinuxNode() throws RunNodesException {
      final String groupName = String.format("ubu%s", System.getProperty("user.name"));
      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.osFamily(OsFamily.UBUNTU);
      templateBuilder.osVersionMatches("14.04");
      templateBuilder.hardwareId("Standard_A0");
      templateBuilder.locationId("westus");
      final Template template = templateBuilder.build();

      final TemplateOptions options = template.getOptions();
      options.inboundPorts(5985);

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
         assertThat(nodes).hasSize(1);
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Test(dependsOnMethods = "testLinuxNode")
   public void testWindowsNode() throws RunNodesException {
      final String groupName = String.format("win%s", System.getProperty("user.name").substring(0, 3));
      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.imageId("WindowsServer2016-Technical-Preview-with-Containers");
      templateBuilder.hardwareId("Standard_A0");
      templateBuilder.locationId("northeurope");
      final Template template = templateBuilder.build();

      final AzureComputeArmTemplateOptions options = template.getOptions().as(AzureComputeArmTemplateOptions.class);
      options.inboundPorts(5985);

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
         assertThat(nodes).hasSize(1);
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AzureComputeProviderMetadata pm = AzureComputeProviderMetadata.builder().build();
      return pm;
   }

   protected String setIfTestSystemPropertyPresent(Properties overrides, String key) {
      if (System.getProperties().containsKey("test." + key)) {
         String val = System.getProperty("test." + key);
         overrides.setProperty(key, val);
         return val;
      } else {
         return null;
      }
   }

}
