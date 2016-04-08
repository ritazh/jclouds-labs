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

import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;
import java.util.List;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "SubnetApiMockTest", singleThreaded = true)
public class SubnetApiMockTest extends BaseAzureComputeApiMockTest {

    private final String subscriptionid = "SUBSCRIPTIONID";
    private final String resourcegroup = "myresourcegroup";
    private final String virtualNetwork = "myvirtualnetwork";
    private final String subnetName = "mysubnet";
    private final String apiVersion = "api-version=2015-06-15";

    public void createSubnet() throws InterruptedException {

        server.enqueue(jsonResponse("/createsubnetresponse.json").setResponseCode(200));

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, virtualNetwork);

        Subnet.SubnetProperties properties = Subnet.SubnetProperties.create(null, "10.2.0.0/24", null);

        Subnet subnet = subnetApi.createOrUpdateSubnet(subnetName, properties);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s/subnets/%s?%s", subscriptionid, resourcegroup, virtualNetwork, subnetName, apiVersion);
        String json = String.format("{ \"properties\":{\"addressPrefix\":\"%s\"}}", "10.2.0.0/24");
        assertSent(server, "PUT", path, json);

        assertEquals(subnet.name(), subnetName);
        assertEquals(subnet.properties().addressPrefix(), "10.2.0.0/24");
    }

    public void getSubnet() throws InterruptedException {

        server.enqueue(jsonResponse("/getonesubnet.json").setResponseCode(200));

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, virtualNetwork);

        Subnet subnet = subnetApi.getSubnet(subnetName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s/subnets/%s?%s", subscriptionid, resourcegroup, virtualNetwork, subnetName, apiVersion);
        assertSent(server, "GET", path);

        assertEquals(subnet.name(), subnetName);
        assertEquals(subnet.properties().addressPrefix(), "10.2.0.0/24");
    }

    public void listSubnets() throws InterruptedException {

        server.enqueue(jsonResponse("/listsubnetswithinvirtualnetwork.json").setResponseCode(200));

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, virtualNetwork);

        List<Subnet> subnets = subnetApi.listSubnets();

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s/subnets?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
        assertSent(server, "GET", path);

        assertTrue(subnets.size() > 0);
    }

    public void deleteSubnet() throws InterruptedException {

        server.enqueue(response202());

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, virtualNetwork);

        subnetApi.deleteSubnet(subnetName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s/subnets/%s?%s", subscriptionid, resourcegroup, virtualNetwork, subnetName, apiVersion);
        assertSent(server, "DELETE", path);
    }

    public void deleteSubnetResourceDoesNotExist() throws InterruptedException {

        server.enqueue(response204());

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, virtualNetwork);

        subnetApi.deleteSubnet(subnetName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s/subnets/%s?%s", subscriptionid, resourcegroup, virtualNetwork, subnetName, apiVersion);
        RecordedRequest rr = assertSent(server, "DELETE", path);
    }
}
