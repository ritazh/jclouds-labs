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
package org.jclouds.azurecomputearm.features;

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecomputearm.domain.*;
import org.jclouds.azurecomputearm.internal.AbstractAzureComputeApiLiveTest;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "VirtualMachineApiLiveTest")
public class VirtualMachineApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String vmName = null;
   private static String LOCATION = "westus";
   final String subscriptionid =  getSubscriptionId();
   final String resourcegroup =  getResourceGroup();
   String subnetID ="";

   @BeforeClass
   public  void Setup(){
      //Subnets belong to a virtual network so that needs to be created first
      VirtualNetwork vn = getOrCreateVirtualNetwork(VIRTUAL_NETWORK_NAME, LOCATION);
      assertNotNull(vn);

      //Subnet needs to be up&running before NIC can be created
      Subnet subnet = getOrCreateSubnet(DEFAULT_SUBNET_NAME, VIRTUAL_NETWORK_NAME);
      assertNotNull(subnet);
      assertNotNull(subnet.id());
      subnetID = subnet.id();
   }

   private String getName() {

      if (vmName == null) {
         vmName = String.format("%3.24s",
           System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase().substring(0,15);
      }

      return vmName;
   }

   private NetworkInterfaceCard createNetworkInterfaceCard() {

      final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);


      //Create properties object
      final NetworkInterfaceCard.NetworkInterfaceCardProperties networkInterfaceCardProperties =
              NetworkInterfaceCard.NetworkInterfaceCardProperties.builder()
                      .ipConfigurations(
                              Arrays.asList(
                                      IpConfiguration.builder()
                                              .name("myipconfig")
                                              .properties(
                                                      IpConfiguration.IpConfigurationProperties.builder()
                                                              .subnet(Subnet.builder().id(subnetID).build())
                                                              .privateIPAllocationMethod("Dynamic")
                                                              .build()
                                              )
                                              .build()
                              ))
                      .build();

      NetworkInterfaceCard nic = nicApi.createOrUpdateNetworkInterfaceCard(NETWORKINTERFACECARD_NAME, LOCATION,  networkInterfaceCardProperties);

      assertEquals(nic.name(), NETWORKINTERFACECARD_NAME);
      assertEquals(nic.location(), LOCATION);
      assertTrue(nic.properties().ipConfigurations().size() > 0 );
      assertEquals(nic.properties().ipConfigurations().get(0).name(), "myipconfig");
      assertEquals(nic.properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
      assertEquals(nic.properties().ipConfigurations().get(0).properties().subnet().id(), subnetID);
      return nic;
   }

   @Test
   public void testCreate() {
      NetworkInterfaceCard nic = createNetworkInterfaceCard();
      StorageAccountApi storageApi = api.getStorageAccountApi(getSubscriptionId(), getResourceGroup());
      final CreateStorageServiceParams params = CreateStorageServiceParams.builder().
              location(LOCATION).
              tags(ImmutableMap.of("property_name", "property_value")).
              properties(ImmutableMap.of("accountType", StorageService.AccountType.Standard_LRS.toString())).
              build();

      CreateStorageServiceParams storage = storageApi.create(getName() + "storage",LOCATION,
              ImmutableMap.of("property_name", "property_value"),
              params.properties());

      while (storage == null) {
         try {
            Thread.sleep(25*1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         storage = storageApi.create(getName() + "storage",LOCATION,
                 ImmutableMap.of("property_name", "property_value"),
                 params.properties());
      }
      StorageService storageAccount = storageApi.get(getName() + "storage");
      String blob = storageAccount.storageServiceProperties().primaryEndpoints().get("blob");
      String id = "/subscriptions/{subscription-id}/resourceGroups/myresourcegroup1/providers/" +
              "Microsoft.Compute/virtualMachines/" + getName();
      VirtualMachine vm = api().create(getName(),id, getName(), LOCATION, getProperties(blob,nic.name()));
      assertTrue(!vm.name().isEmpty());
      String status = "Creating";
      while (status.equals("Creating")){
         try {
            Thread.sleep(120*1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         vm = api().get(getName());
         status = vm.properties().provisioningState();
      }
      status = vm.properties().provisioningState();
      assertTrue(!status.equals("Creating"));
      assertTrue(!status.equals("Failed"));
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      VirtualMachine vm = api().get(getName());
      assertTrue(!vm.name().isEmpty());
   }

   @Test(dependsOnMethods = "testGet")
   public void testList() {
      List<VirtualMachine> list = api().list();
      for (VirtualMachine machine : list) {
         assertTrue(!machine.name().isEmpty());
      }
   }

   @Test(dependsOnMethods = "testList")
   public void testDelete() {
      api().delete(getName());
      //StorageAccountApi storageApi = api.getStorageAccountApi(getSubscriptionId(), getResourceGroup());
      //TODO: delete storage and other resources after VM is destroyed. storageApi.delete(getName() + "storage");

   }

   private VirtualMachineApi api() {
      return api.getVirtualMachineApi(getSubscriptionId(),getResourceGroup());
   }


   private VirtualMachineProperties getProperties(String blob, String nic) {
      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.create("MicrosoftWindowsServerEssentials",
              "WindowsServerEssentials", "WindowsServerEssentials","latest");
      VHD vhd = VHD.create(blob + "vhds/" + getName()+ ".vhd");
      VHD vhd2 = VHD.create(blob + "vhds/" + getName()+ "data.vhd");
      DataDisk dataDisk = DataDisk.create(getName() + "data","100",0, vhd2,"Empty");
      OSDisk osDisk = OSDisk.create(null,getName(), vhd,"ReadWrite","FromImage");
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, null);
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false,null,null,true,null);
      OSProfile osProfile = OSProfile.create(getName(),"azureuser","RFe3&432dg",null,null,windowsConfig);
      NetworkProfile.NetworkInterfaceId networkInterface =
              NetworkProfile.NetworkInterfaceId.create("/subscriptions/" + getSubscriptionId()+
                  "/resourceGroups/" + getResourceGroup() + "/providers/Microsoft.Network/networkInterfaces/" + nic);
      List<NetworkProfile.NetworkInterfaceId> networkInterfaces =
              new ArrayList<NetworkProfile.NetworkInterfaceId>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics =
              DiagnosticsProfile.BootDiagnostics.create(true, blob);
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create(null,
              null, null, hwProf, storageProfile, osProfile,networkProfile, diagnosticsProfile, "Creating");
      return properties;
   }
}
