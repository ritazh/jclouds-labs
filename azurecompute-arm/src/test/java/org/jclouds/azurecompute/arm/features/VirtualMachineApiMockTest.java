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

import com.google.common.collect.ImmutableList;
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
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

import static com.google.common.collect.Iterables.isEmpty;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "VirtualMachineApiMockTest", singleThreaded = true)
public class VirtualMachineApiMockTest extends BaseAzureComputeApiMockTest {

   public void testGet() throws Exception {
      server.enqueue(jsonResponse("/virtualmachine.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertEquals(vmAPI.get("windowsmachine"), getVM());
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15");
   }
   public void testGetEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertNull(vmAPI.get("windowsmachine"));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15");
   }

   public void testGetInstanceDetails() throws Exception {
      server.enqueue(jsonResponse("/virtualmachineInstance.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      VirtualMachineInstance actual = vmAPI.getInstanceDetails("windowsmachine");
      VirtualMachineInstance expected = getVMInstance();

      assertEquals(actual.statuses().get(0).code(), expected.statuses().get(0).code());
      assertEquals(actual.statuses().get(0).displayStatus(), expected.statuses().get(0).displayStatus());
      assertEquals(actual.statuses().get(0).level(), expected.statuses().get(0).level());
      //assertEquals(actual.statuses().get(0).time().toString(), expected.statuses().get(0).time().toString());
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine/instanceView?api-version=2015-06-15");
   }

   public void testGetInstanceDetailsEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertNull(vmAPI.getInstanceDetails("windowsmachine"));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine/instanceView?api-version=2015-06-15");
   }

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/virtualmachines.json"));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertEquals(vmAPI.list(), getVMList());
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines?api-version=2015-06-15");
   }
   public void testListEmpty() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      assertTrue(isEmpty(vmAPI.list()));
      assertSent(server, "GET", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines?api-version=2015-06-15");
   }

   public void testCreate() throws Exception {
      server.enqueue(jsonResponse("/createvirtualmachineresponse.json"));

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      VirtualMachine vm = vmAPI.create("windowsmachine", "westus", getProperties());
      assertEquals(vm, getVM());
      assertSent(server, "PUT", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15&validating=false",
              "{\"location\":\"westus\",\"properties\":" +
                      "{\"vmId\":\"27ee085b-d707-xxxx-yyyy-2370e2eb1cc1\"," +
                      "\"hardwareProfile\":{\"vmSize\":\"Standard_D1\"}," +
                      "\"storageProfile\":{\"imageReference\":{\"publisher\":\"publisher\",\"offer\":\"offer\",\"sku\":\"sku\",\"version\":\"ver\"}," +
                      "\"osDisk\":{\"osType\":\"Windows\",\"name\":\"windowsmachine\"," +
                      "\"vhd\":{\"uri\":\"https://groupname2760.blob.core.windows.net/vhds/windowsmachine201624102936.vhd\"},\"caching\":\"ReadWrite\",\"createOption\":\"FromImage\"},\"dataDisks\":[]}," +
                      "\"osProfile\":{\"computerName\":\"windowsmachine\",\"adminUsername\":\"azureuser\",\"windowsConfiguration\":{\"provisionVMAgent\":false,\"enableAutomaticUpdates\":true}}," +
                      "\"networkProfile\":{\"networkInterfaces\":[{\"id\":\"/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Network/networkInterfaces/windowsmachine167\"}]}," +
                      "\"diagnosticsProfile\":{\"bootDiagnostics\":{\"enabled\":true,\"storageUri\":\"https://groupname2760.blob.core.windows.net/\"}},\"provisioningState\":\"Creating\"}}");

   }

   public void testDeleteReturns404() throws Exception {
      server.enqueue(response404());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      URI uri = vmAPI.delete("windowsmachine");

      assertEquals(server.getRequestCount(), 1);
      assertNull(uri);

      assertSent(server, "DELETE", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/windowsmachine?api-version=2015-06-15");
   }
   public void testDelete() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");

      URI uri = vmAPI.delete("windowsmachine");

      assertEquals(server.getRequestCount(), 1);
      assertNotNull(uri);

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

   public void testGeneralize() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200));
      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      vmAPI.generalize("vm"); // IllegalStateException if failed
      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/vm/generalize?api-version=2015-06-15");
   }

   public void testCapture() throws Exception {
      server.enqueue(response202WithHeader());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      URI uri = vmAPI.capture("vm", "prefix", "container");
      assertNotNull(uri);
      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/vm/capture?api-version=2015-06-15");
   }

   public void testCapture404() throws Exception {
      server.enqueue(response404());

      final VirtualMachineApi vmAPI = api.getVirtualMachineApi("groupname");
      URI uri = vmAPI.capture("vm", "prefix", "container");
      assertNull(uri);
      assertSent(server, "POST", "/subscriptions/SUBSCRIPTIONID/resourceGroups/groupname/providers/Microsoft.Compute" +
              "/virtualMachines/vm/capture?api-version=2015-06-15");
   }

   private VirtualMachineProperties getProperties() {
      HardwareProfile hwProf = HardwareProfile.create("Standard_D1");
      ImageReference imgRef = ImageReference.create("publisher", "offer", "sku", "ver");
      VHD vhd = VHD.create("https://groupname2760.blob.core.windows.net/vhds/windowsmachine201624102936.vhd");
      List<DataDisk> dataDisks = new ArrayList<DataDisk>();
      OSDisk osDisk = OSDisk.create("Windows", "windowsmachine", vhd, "ReadWrite", "FromImage", null);
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

   private VirtualMachineInstance getVMInstance() {
      List<VirtualMachineInstance.VirtualMachineStatus> statuses = new ArrayList<VirtualMachineInstance.VirtualMachineStatus>();
      String testDate = "Wed May 04 01:38:52 PDT 2016";
      DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
      Date date = null;
      try {
         date = formatter.parse(testDate);
      } catch (Exception e) {
         e.printStackTrace();
      }
      VirtualMachineInstance.VirtualMachineStatus vmStatus =
              VirtualMachineInstance.VirtualMachineStatus.create("ProvisioningState/succeeded", "Info", "Provisioning succeeded",  date);
      statuses.add(vmStatus);
      VirtualMachineInstance.VirtualMachineStatus vmStatus1 =
              VirtualMachineInstance.VirtualMachineStatus.create("PowerState/running", "Info", "VM running",  null);
      statuses.add(vmStatus1);

      VirtualMachineInstance machineInstance =
              VirtualMachineInstance.create(null, null, ImmutableList.copyOf(statuses));
      return machineInstance;
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
