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

import com.google.common.collect.ImmutableList;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecomputearm.domain.*;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "VirtualMachineApiMockTest")
public class VirtualMachineAPIMockTest extends BaseAzureComputeApiMockTest {

   public void testGet() throws Exception {
      server.enqueue(jsonResponse("/virtualmachine.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("subscriptionid","groupname");
      assertEquals(vmAPI.get("windowsmachine"), getVM());
      assertSent(server, "GET", "/subscriptions/subscriptionid/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15");
   }

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/virtualmachines.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("subscriptionid","groupname");
      assertEquals(vmAPI.list(), getVMList());
      assertSent(server, "GET", "/subscriptions/subscriptionid/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines?api-version=2015-06-15");
   }

   public void testCreate() throws Exception {
      server.enqueue(jsonResponse("/createvirtualmachineresponse.json"));
      String id = "/subscriptions/626f67f6-8fd0-xxxx-yyyy-e3ce95f7dfec/" +
              "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine";
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("subscriptionid","groupname");
      VirtualMachine vm = vmAPI.create("windowsmachine",id, "windowsmachine", "westus", getProperties());
      assertEquals(vm, getVM());
      assertSent(server, "PUT", "/subscriptions/subscriptionid/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15&validating=false");
   }

   private VirtualMachineProperties getProperties() {
      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.create("publisher", "offer", "sku","ver");
      VHD vhd = VHD.create("https://groupname2760.blob.core.windows.net/vhds/windowsmachine201624102936.vhd");
      List<DataDisk> dataDisks = new ArrayList<DataDisk>();
      OSDisk osDisk = OSDisk.create("Windows","windowsmachine", vhd,"ReadWrite","FromImage");
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, dataDisks);
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false,null,null,true,null);
      OSProfile osProfile = OSProfile.create("windowsmachine","azureuser",null,null,null,windowsConfig);
      NetworkProfile.NetworkInterfaceId networkInterface =
              NetworkProfile.NetworkInterfaceId.create("/subscriptions/626f67f6-8fd0-xxxx-" +
                      "yyyy-e3ce95f7dfec/resourceGroups/groupname/providers/Microsoft.Network/networkInterfaces/" +
                      "windowsmachine167");
      List<NetworkProfile.NetworkInterfaceId> networkInterfaces = new ArrayList<NetworkProfile.NetworkInterfaceId>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics = DiagnosticsProfile.BootDiagnostics.create(true,
                      "https://groupname2760.blob.core.windows.net/");
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create("27ee085b-d707-xxxx-yyyy-2370e2eb1cc1",
              null, null, hwProf, storageProfile, osProfile,networkProfile, diagnosticsProfile, "Creating");
      return properties;
   }

   private VirtualMachine getVM() {
      VirtualMachineProperties properties = getProperties();
      VirtualMachine machine = VirtualMachine.create("/subscriptions/626f67f6-8fd0-xxxx-yyyy-e3ce95f7dfec/" + "" +
              "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine","windowsmachine",
              "Microsoft.Compute/virtualMachines", "westus",null, properties);
      return machine;
   }

   private List<VirtualMachine> getVMList() {
      VirtualMachineProperties properties = getProperties();
      VirtualMachine machine = VirtualMachine.create("/subscriptions/626f67f6-8fd0-xxxx-yyyy-e3ce95f7dfec/" + "" +
                      "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine",
              "windowsmachine", "Microsoft.Compute/virtualMachines", "westus",null, properties);
      List<VirtualMachine> list = new ArrayList<VirtualMachine>();
      list.add(machine);
      return list;
   }
}
