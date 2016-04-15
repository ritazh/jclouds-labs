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

import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.DiagnosticsProfile;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

@Test(groups = "unit", testName = "VirtualMachineApiMockTest", singleThreaded = true)
public class VirtualMachineApiMockTest extends BaseAzureComputeApiMockTest {

   public void testGet() throws Exception {
      server.enqueue(jsonResponse("/virtualmachine.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertEquals(vmAPI.get("windowsmachine"), getVM());
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15");
   }

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/virtualmachines.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertEquals(vmAPI.list(), getVMList());
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines?api-version=2015-06-15");
   }

   public void testCreate() throws Exception {
      server.enqueue(jsonResponse("/createvirtualmachineresponse.json"));
      String id = "/subscriptions/SUBSCRIPTIONID/" +
              "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine";
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      VirtualMachine vm = vmAPI.create("windowsmachine", id, "windowsmachine", "westus", getProperties());
      assertEquals(vm, getVM());
      assertSent(server, "PUT", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15&validating=false");
   }

   public void testDelete() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(204));

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.delete("windowsmachine");

      assertSent(server, "DELETE", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15");
   }

   public void testStart() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(204));

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.start("windowsmachine");

      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine/start?api-version=2015-06-15");
   }

   public void testRestart() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(204));

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.restart("windowsmachine");

      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine/restart?api-version=2015-06-15");
   }

   public void testStop() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(204));

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      vmAPI.stop("windowsmachine");

      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine/powerOff?api-version=2015-06-15");
   }

   private VirtualMachineProperties getProperties() {
      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.create("publisher", "offer", "sku", "ver");
      VHD vhd = VHD.create("https://groupname2760.blob.core.windows.net/vhds/windowsmachine201624102936.vhd");
      List<DataDisk> dataDisks = new ArrayList<DataDisk>();
      OSDisk osDisk = OSDisk.create("Windows", "windowsmachine", vhd, "ReadWrite", "FromImage");
      StorageProfile storageProfile = StorageProfile.create(imgRef, osDisk, dataDisks);
      OSProfile.WindowsConfiguration windowsConfig = OSProfile.WindowsConfiguration.create(false, null, null, true,
              null);
      OSProfile osProfile = OSProfile.create("windowsmachine", "azureuser", null, null, null, windowsConfig);
      IdReference networkInterface =
              IdReference.create("/subscriptions/SUBSCRIPTIONID" +
                      "/resourceGroups/groupname/providers/Microsoft.Network/networkInterfaces/" +
                      "windowsmachine167");
      List<IdReference> networkInterfaces = new ArrayList<IdReference>();
      networkInterfaces.add(networkInterface);
      NetworkProfile networkProfile = NetworkProfile.create(networkInterfaces);
      DiagnosticsProfile.BootDiagnostics bootDiagnostics = DiagnosticsProfile.BootDiagnostics.create(true,
                      "https://groupname2760.blob.core.windows.net/");
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(bootDiagnostics);
      VirtualMachineProperties properties = VirtualMachineProperties.create("27ee085b-d707-xxxx-yyyy-2370e2eb1cc1",
              null, null, hwProf, storageProfile, osProfile, networkProfile, diagnosticsProfile, "Creating");
      return properties;
   }

   private VirtualMachine getVM() {
      VirtualMachineProperties properties = getProperties();
      VirtualMachine machine = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + "" +
              "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine", "windowsmachine",
              "Microsoft.Compute/virtualMachines", "westus", null, properties);
      return machine;
   }

   private List<VirtualMachine> getVMList() {
      VirtualMachineProperties properties = getProperties();
      VirtualMachine machine = VirtualMachine.create("/subscriptions/SUBSCRIPTIONID/" + "" +
                      "resourceGroups/groupname/providers/Microsoft.Compute/virtualMachines/windowsmachine",
              "windowsmachine", "Microsoft.Compute/virtualMachines", "westus", null, properties);
      List<VirtualMachine> list = new ArrayList<VirtualMachine>();
      list.add(machine);
      return list;
   }
}
