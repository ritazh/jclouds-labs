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
package org.jclouds.azurecompute.arm.features;

import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.DiagnosticsProfile;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "VirtualMachineApiLiveTest")
public class VirtualMachineApiLiveTest extends BaseAzureComputeApiLiveTest {

   private String subscriptionid = getSubscriptionId();
   private String vmName = null;
   private String nicName =  null;

   @BeforeClass
   public  void Setup(){
      NetworkInterfaceCard nic = getOrCreateNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);
      assertNotNull(nic);
      nicName = nic.name();
   }

   private String getName() {
      if (vmName == null) {
         vmName = String.format("%3.24s",
           System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase().substring(0, 15);
      }
      return vmName;
   }

   @Test
   public void testCreate() {
      StorageAccountApi storageApi = api.getStorageAccountApi(getResourceGroupName());
      StorageService storageAccount = storageApi.get(getStorageServiceName());
      String blob = storageAccount.storageServiceProperties().primaryEndpoints().get("blob");

      String id = "/subscriptions/" + subscriptionid + "/resourceGroups/" + getResourceGroupName() +
              "/providers/" + "Microsoft.Compute/virtualMachines/" + getName();
      VirtualMachine vm = api().create(getName(), id, getName(), LOCATION, getProperties(blob, nicName));
      assertTrue(!vm.name().isEmpty());
      String status = "Creating";
      int stopCounter = 0;
      while (status.equals("Creating")){
         try {
            Thread.sleep(120 * 1000); // Creation takes long time. Recheck later
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
         vm = api().get(getName());
         status = vm.properties().provisioningState();
         stopCounter++;
         if (stopCounter == 15)
            break;
      }
      status = vm.properties().provisioningState();

      // Cannot be creating anymore. Should be succeeded or running but not failed.
      assertTrue(!status.equals("Creating"));
      assertTrue(!status.equals("Failed"));
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      VirtualMachine vm = api().get(getName());
      assertTrue(!vm.name().isEmpty());
   }

   @Test(dependsOnMethods = "testGet")
   public void testGetInstanceView() {
      VirtualMachineInstance vmi = api().getInstanceDetails(getName());
      assertTrue(!vmi.statuses().isEmpty());
   }

   @Test(dependsOnMethods = "testStart")
   public void testStop() {
      String status = "";
      api().stop(getName());
      int stopCounter = 0;
      while (!status.equals("VM stopped")) {
         List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(getName()).statuses();
         for (int c = 0; c < statuses.size(); c++) {
            if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
               status = statuses.get(c).displayStatus();
            }
            try {
               Thread.sleep(5 * 1000); // Stopping takes long time. Recheck later
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            stopCounter++;
            if (stopCounter == 40)
               break;
         }
      }
      assertEquals(status, "VM stopped");

   }

   @Test(dependsOnMethods = "testGet")
   public void testStart() {
      api().start(getName());
      String status = "";
      int stopCounter = 0;
      while (!status.equals("VM running")) {
         List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(getName()).statuses();
         for (int c = 0; c < statuses.size(); c++) {
            if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
               status = statuses.get(c).displayStatus();
            }
            try {
               Thread.sleep(5 * 1000); // Starting takes long time. Recheck later
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            stopCounter++;
            if (stopCounter == 40)
               break;
         }
      }
      assertEquals(status, "VM running");

   }

   @Test(dependsOnMethods = "testStop")
   public void testRestart() {
      api().start(getName());
      String status = "";
      int stopCounter = 0;
      while (!status.equals("VM running")) {
         List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(getName()).statuses();
         for (int c = 0; c < statuses.size(); c++) {
            if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
               status = statuses.get(c).displayStatus();
            }
            try {
               Thread.sleep(5 * 1000); // Starting takes long time. Recheck later
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            stopCounter++;
            if (stopCounter == 40)
               break;
         }
      }
      api().restart(getName());
      try {
         Thread.sleep(30 * 1000); // Wait while to get it powered off and then wait until machine is running
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      status = "";
      stopCounter = 0;
      while (!status.equals("VM running")) {
         List<VirtualMachineInstance.VirtualMachineStatus> statuses = api().getInstanceDetails(getName()).statuses();
         for (int c = 0; c < statuses.size(); c++) {
            if (statuses.get(c).code().substring(0, 10).equals("PowerState")) {
               status = statuses.get(c).displayStatus();
            }
            try {
               Thread.sleep(5 * 1000); // Starting takes long time. Recheck later
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
            stopCounter++;
            if (stopCounter == 40)
               break;
         }
      }
      assertEquals(status, "VM running");
   }

   @Test(dependsOnMethods = "testGet")
   public void testList() {
      List<VirtualMachine> list = api().list();
      for (VirtualMachine machine : list) {
         assertTrue(!machine.name().isEmpty());
      }
   }

   @Test(dependsOnMethods = "testRestart")
   public void testDelete() {

      api().delete(getName());

      // Test that there is no machines left that tearDown can delete resource group and storage. Delete takes time so
      // we need to check it multiple time
      List<VirtualMachine> list = api().list();
      while (!list.isEmpty()) {
         list = api().list();
         try {
            Thread.sleep(30 * 1000); // Delete takes long time. Recheck later
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   private VirtualMachineApi api() {
      return api.getVirtualMachineApi(getResourceGroupName());
   }

   private VirtualMachineProperties getProperties(String blob, String nic) {
      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.create("MicrosoftWindowsServerEssentials",
              "WindowsServerEssentials", "WindowsServerEssentials", "latest");
      VHD vhd = VHD.create(blob + "vhds/" + getName() + ".vhd");
      VHD vhd2 = VHD.create(blob + "vhds/" + getName() + "data.vhd");
      DataDisk dataDisk = DataDisk.create(getName() + "data", "100", 0, vhd2, "Empty");
      OSDisk osDisk = OSDisk.create(null, getName(), vhd, "ReadWrite", "FromImage");
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, null);
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false, null, null, true,
              null);
      OSProfile osProfile = OSProfile.create(getName(), "azureuser", "RFe3&432dg", null, null, windowsConfig);
      NetworkProfile.NetworkInterfaceId networkInterface =
              NetworkProfile.NetworkInterfaceId.create("/subscriptions/" + subscriptionid +
                  "/resourceGroups/" + getResourceGroupName() + "/providers/Microsoft.Network/networkInterfaces/"
                  + nic);
      List<NetworkProfile.NetworkInterfaceId> networkInterfaces =
              new ArrayList<NetworkProfile.NetworkInterfaceId>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics =
              DiagnosticsProfile.BootDiagnostics.create(true, blob);
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create(null,
              null, null, hwProf, storageProfile, osProfile, networkProfile, diagnosticsProfile, "Creating");
      return properties;
   }
}
