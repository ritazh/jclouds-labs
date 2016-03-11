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
package org.jclouds.azurecomputearm.internal;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testng.Assert.assertNotNull;

import com.google.common.collect.ImmutableMap;

import org.jclouds.azurecomputearm.domain.CreateStorageServiceParams;
import org.jclouds.azurecomputearm.domain.IpConfiguration;
import org.jclouds.azurecomputearm.domain.NetworkInterfaceCard;
import org.jclouds.azurecomputearm.domain.ResourceGroup;
import org.jclouds.azurecomputearm.domain.StorageService;
import org.jclouds.azurecomputearm.domain.Subnet;
import org.jclouds.azurecomputearm.domain.VirtualNetwork;
import org.jclouds.azurecomputearm.features.StorageAccountApi;
import org.jclouds.azurecomputearm.features.SubnetApi;
import org.jclouds.azurecomputearm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecomputearm.features.VirtualNetworkApi;

import org.jclouds.azurecomputearm.util.ConflictManagementPredicate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseAzureComputeApiLiveTest extends AbstractAzureComputeApiLiveTest {

   public static final String DEFAULT_ADDRESS_SPACE = "10.0.0.0/20";

   public static final String DEFAULT_SUBNET_ADDRESS_SPACE = "10.2.0.0/23";

   public static final String VIRTUAL_NETWORK_NAME = "jclouds-virtual-network-live-test";

   public static final String DEFAULT_SUBNET_NAME = "jclouds-1";

   public static final String LOCATION = "westeurope";

   public static final String DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX = "10.2.0.0/16";

   public static final String NETWORKINTERFACECARD_NAME = "jcloudsNic";

   public static final String IMAGE_NAME
           = "b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_1-LTS-amd64-server-20150123-en-us-30GB";

   private String resourceGroupName = null;


   protected StorageService storageService;

   //protected NetworkConfiguration.VirtualNetworkSite virtualNetworkSite;

   private String storageServiceName = null;

   protected String getStorageServiceName() {
      if (storageServiceName == null) {
         storageServiceName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase();
      }
      return storageServiceName;
   }

   protected String getSubscriptionId() {
      String subscriptionId = null;
      if (System.getProperties().containsKey("test.azurecompute-arm.subscriptionid"))
         subscriptionId = System.getProperty("test.azurecompute-arm.subscriptionid");
      assertNotNull(subscriptionId);
      return subscriptionId;
   }

   protected String getResourceGroupName() {
      if (resourceGroupName == null) {
         resourceGroupName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + "group");
         createResourceGroup(resourceGroupName);
      }
      return resourceGroupName;
   }

   private void createResourceGroup(String name) {
      HashMap<String, String> tags = new HashMap<String, String>();

      final ResourceGroup resourceGroup = api.getResourceGroupApi(getSubscriptionId()).create(
              name, LOCATION, tags);
   }

   private void deleteResourceGroup(String name) {
      HashMap<String, String> tags = new HashMap<String, String>();

      api.getResourceGroupApi(getSubscriptionId()).delete(name);
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();

      operationSucceeded = new ConflictManagementPredicate(api, 600, 5, 5, SECONDS);

      final CreateStorageServiceParams params = CreateStorageServiceParams.builder().
              location(LOCATION).
              tags(ImmutableMap.of("property_name", "property_value")).
              properties(ImmutableMap.of("accountType", StorageService.AccountType.Standard_LRS.toString())).
              build();
      storageService = getOrCreateStorageService(getStorageServiceName(), params);
   }

   @AfterClass(alwaysRun = true)
   @Override
   protected void tearDown() {
      super.tearDown();

      api.getStorageAccountApi(getSubscriptionId(), getResourceGroupName()).delete(getStorageServiceName());
      deleteResourceGroup(getResourceGroupName());
   }

   protected StorageService getOrCreateStorageService(String storageServiceName, CreateStorageServiceParams params) {
      StorageAccountApi storageApi = api.getStorageAccountApi(getSubscriptionId(), getResourceGroupName());
      StorageService ss = storageApi.get(storageServiceName);
      if (ss != null) {
         return ss;
      }
      CreateStorageServiceParams response = storageApi.create(storageServiceName, params.location(), params.tags(),
              params.properties());
      while (response == null) {
         try {
            Thread.sleep(25 * 1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         response = storageApi.create(storageServiceName, params.location(), params.tags(),
                 params.properties());
      }
      Assert.assertEquals(response.location(), LOCATION);
      ss = storageApi.get(storageServiceName);

      Logger.getAnonymousLogger().log(Level.INFO, "created storageService: {0}", ss);
      return ss;
   }

   protected VirtualNetwork getOrCreateVirtualNetwork(final String virtualNetworkName) {

      VirtualNetworkApi vnApi = api.getVirtualNetworkApi(getSubscriptionId(), getResourceGroupName());
      VirtualNetwork vn = vnApi.getVirtualNetwork(virtualNetworkName);

      if (vn != null) {
         return vn;
      }

      final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties =
              VirtualNetwork.VirtualNetworkProperties.builder()
                      .addressSpace(
                              VirtualNetwork.AddressSpace.builder()
                                      .addressPrefixes(Arrays.asList(DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX)).build()
                      ).build();

      vn = vnApi.createOrUpdateVirtualNetwork(VIRTUAL_NETWORK_NAME, LOCATION, virtualNetworkProperties);
      return vn;
   }

   protected Subnet getOrCreateSubnet(final String subnetName, final String virtualNetworkName){

      SubnetApi subnetApi = api.getSubnetApi(getSubscriptionId(), getResourceGroupName(), virtualNetworkName);
      Subnet subnet = subnetApi.getSubnet(subnetName);

      if (subnet != null){
         return subnet;
      }

      Subnet.SubnetProperties properties = Subnet.SubnetProperties.builder().addressPrefix(DEFAULT_SUBNET_ADDRESS_SPACE).build();
      subnet = subnetApi.createOrUpdateSubnet(subnetName, properties);

      return subnet;
   }


   protected NetworkInterfaceCard getOrCreateNetworkInterfaceCard(final String networkInterfaceCardName){

      NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(getSubscriptionId(), getResourceGroupName());
      NetworkInterfaceCard nic = nicApi.getNetworkInterfaceCard(networkInterfaceCardName);

      if (nic != null){
         return nic;
      }

      VirtualNetwork vn = getOrCreateVirtualNetwork(VIRTUAL_NETWORK_NAME);

      Subnet subnet = getOrCreateSubnet(DEFAULT_SUBNET_NAME, VIRTUAL_NETWORK_NAME);

      //Create properties object
      final NetworkInterfaceCard.NetworkInterfaceCardProperties networkInterfaceCardProperties =
              NetworkInterfaceCard.NetworkInterfaceCardProperties.builder()
                      .ipConfigurations(
                              Arrays.asList(
                                      IpConfiguration.builder()
                                              .name("myipconfig")
                                              .properties(
                                                      IpConfiguration.IpConfigurationProperties.builder()
                                                              .subnet(Subnet.builder().id(subnet.id()).build())
                                                              .privateIPAllocationMethod("Dynamic")
                                                              .build()
                                              )
                                              .build()
                              )
                      )
                      .build();
      final Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
      nic = nicApi.createOrUpdateNetworkInterfaceCard(NETWORKINTERFACECARD_NAME, LOCATION, networkInterfaceCardProperties, tags);
      return  nic;
   }

//
//   protected Deployment getOrCreateDeployment(final String serviceName, final DeploymentParams params) {
//      Deployment deployment = api.getDeploymentApiForService(serviceName).get(params.name());
//      if (deployment != null) {
//         return deployment;
//      }
//
//      assertTrue(new ConflictManagementPredicate(api) {
//
//         @Override
//         protected String operation() {
//            return api.getDeploymentApiForService(serviceName).create(params);
//         }
//      }.apply(getStorageServiceName()));
//
//      deployment = api.getDeploymentApiForService(serviceName).get(params.name());
//
//      Logger.getAnonymousLogger().log(Level.INFO, "created deployment: {0}", deployment);
//      return deployment;
//   }
//
//
//   protected VirtualNetworkSite getOrCreateVirtualNetworkSite(final String virtualNetworkSiteName, String location) {
//      final List<VirtualNetworkSite> current = AzureTestUtils.getVirtualNetworkSite(api);
//
//      final Optional<VirtualNetworkSite> optionalVirtualNetworkSite = tryFind(
//              current,
//              new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));
//
//      if (optionalVirtualNetworkSite.isPresent()) {
//         return optionalVirtualNetworkSite.get();
//      }
//
//      current.add(VirtualNetworkSite.create(UUID.randomUUID().toString(),
//              virtualNetworkSiteName,
//              location,
//              AddressSpace.create(DEFAULT_ADDRESS_SPACE),
//              ImmutableList.of(Subnet.create(DEFAULT_SUBNET_NAME, DEFAULT_SUBNET_ADDRESS_SPACE, null))));
//
//      final NetworkConfiguration networkConfiguration
//              = NetworkConfiguration.create(VirtualNetworkConfiguration.create(null, current));
//
//      VirtualNetworkSite vns;
//      try {
//         vns = find(
//                 api.getVirtualNetworkApi().getNetworkConfiguration().virtualNetworkConfiguration().
//                 virtualNetworkSites(),
//                 new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));
//      } catch (Exception e) {
//         assertTrue(new ConflictManagementPredicate(api) {
//
//            @Override
//            protected String operation() {
//               return api.getVirtualNetworkApi().set(networkConfiguration);
//            }
//         }.apply(virtualNetworkSiteName));
//
//         vns = find(
//                 api.getVirtualNetworkApi().getNetworkConfiguration().virtualNetworkConfiguration().
//                 virtualNetworkSites(),
//                 new SameVirtualNetworkSiteNamePredicate(virtualNetworkSiteName));
//
//         Logger.getAnonymousLogger().log(Level.INFO, "created virtualNetworkSite: {0}", vns);
//      }
//      return vns;
//   }
}
