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
package org.jclouds.azurecompute.arm.internal;
import static org.testng.Assert.assertNotNull;

import com.google.common.collect.ImmutableMap;

import org.jclouds.azurecompute.arm.domain.AddressSpace;
import org.jclouds.azurecompute.arm.domain.CreateStorageServiceParams;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.SubnetProperties;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.domain.VirtualNetworkProperties;
import org.jclouds.azurecompute.arm.features.StorageAccountApi;
import org.jclouds.azurecompute.arm.features.SubnetApi;
import org.jclouds.azurecompute.arm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkApi;

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

   private String resourceGroupName = null;


   protected StorageService storageService;


   private String storageServiceName = null;

   protected String getStorageServiceName() {
      if (storageServiceName == null) {
         storageServiceName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase();
      }
      return storageServiceName;
   }

   protected String getLocation() {
      return LOCATION;
   }

   protected String getEndpoint() {
      String endpoint = null;
      if (System.getProperty("test.azurecompute-arm.endpoint") != null) {
         endpoint = System.getProperty("test.azurecompute-arm.endpoint");
      }
      assertNotNull(endpoint);
      return endpoint;
   }

   protected String getSubscriptionId() {
      String subscriptionid = null;
      String endpoint = null;
      endpoint = getEndpoint();
      if (endpoint != null) {
         subscriptionid = endpoint.substring(endpoint.lastIndexOf("/") + 1);
      }
      assertNotNull(subscriptionid);
      return subscriptionid;
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

      final ResourceGroup resourceGroup = api.getResourceGroupApi().create(
              name, LOCATION, tags);
   }

   private void deleteResourceGroup(String name) {
      api.getResourceGroupApi().delete(name);
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();

      final CreateStorageServiceParams params = CreateStorageServiceParams.create(
              LOCATION,
              ImmutableMap.of("property_name", "property_value"),
              ImmutableMap.of("accountType", StorageService.AccountType.Standard_LRS.toString()));
      storageService = getOrCreateStorageService(getStorageServiceName(), params);
   }

   @AfterClass(alwaysRun = true)
   @Override
   protected void tearDown() {
      super.tearDown();

      api.getStorageAccountApi(getResourceGroupName()).delete(getStorageServiceName());
      deleteResourceGroup(getResourceGroupName());
   }

   protected StorageService getOrCreateStorageService(String storageServiceName, CreateStorageServiceParams params) {
      StorageAccountApi storageApi = api.getStorageAccountApi(getResourceGroupName());
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

      VirtualNetworkApi vnApi = api.getVirtualNetworkApi(getResourceGroupName());
      VirtualNetwork vn = vnApi.getVirtualNetwork(virtualNetworkName);

      if (vn != null) {
         return vn;
      }

      final VirtualNetworkProperties virtualNetworkProperties =
              VirtualNetworkProperties.create(null, null,
                      AddressSpace.create(Arrays.asList(DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX)), null);


      vn = vnApi.createOrUpdateVirtualNetwork(VIRTUAL_NETWORK_NAME, LOCATION, virtualNetworkProperties);
      return vn;
   }

   protected Subnet getOrCreateSubnet(final String subnetName, final String virtualNetworkName){

      SubnetApi subnetApi = api.getSubnetApi(getResourceGroupName(), virtualNetworkName);
      Subnet subnet = subnetApi.getSubnet(subnetName);

      if (subnet != null){
         return subnet;
      }

      SubnetProperties properties = SubnetProperties.create(null, DEFAULT_SUBNET_ADDRESS_SPACE, null);
      subnet = subnetApi.createOrUpdateSubnet(subnetName, properties);

      return subnet;
   }


   protected NetworkInterfaceCard getOrCreateNetworkInterfaceCard(final String networkInterfaceCardName){

      NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(getResourceGroupName());
      NetworkInterfaceCard nic = nicApi.getNetworkInterfaceCard(networkInterfaceCardName);

      if (nic != null){
         return nic;
      }

      VirtualNetwork vn = getOrCreateVirtualNetwork(VIRTUAL_NETWORK_NAME);

      Subnet subnet = getOrCreateSubnet(DEFAULT_SUBNET_NAME, VIRTUAL_NETWORK_NAME);

      //Create properties object
      final NetworkInterfaceCardProperties networkInterfaceCardProperties =
              NetworkInterfaceCardProperties.create(null, null, null,
                      Arrays.asList(IpConfiguration.create("myipconfig", null, null, null,
                              IpConfigurationProperties.create(null, null, "Dynamic", IdReference.create(subnet.id()), null))
                      )
              );
      final Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
      nic = nicApi.createOrUpdateNetworkInterfaceCard(NETWORKINTERFACECARD_NAME, LOCATION, networkInterfaceCardProperties, tags);
      return  nic;
   }
}
