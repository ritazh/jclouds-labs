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
import com.squareup.okhttp.mockwebserver.RecordedRequest;
import org.jclouds.azurecomputearm.AzureComputeApi;
import org.jclouds.azurecomputearm.domain.ResourceGroup;
import org.jclouds.azurecomputearm.domain.Subnet;
import org.jclouds.azurecomputearm.domain.VirtualNetwork;

import org.testng.annotations.Test;
import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//mvn -Dtest=VirtualNetworkApiMockTest test
@Test(groups = "unit", testName = "VirtualNetworkApiMockTest", singleThreaded = true)
public class VirtualNetworkApiMockTest extends BaseAzureComputeApiMockTest {

    final String subscriptionid = "12345678-2749-4e68-9dcf-e21cda85a132";
    final String resourcegroup = "myresourcegroup";
    final String virtualNetwork = "mockvirtualnetwork";
    final String apiVersion ="api-version=2015-06-15";
    final String location = "northeurope";

    //mvn -Dtest=VirtualNetworkApiMockTest#getVirtualNetwork test
    public void getVirtualNetwork() throws InterruptedException {
        server.enqueue(jsonResponse("/virtualnetwork.json"));

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);
        VirtualNetwork vn = vnApi.getVirtualNetwork(virtualNetwork);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
        assertSent(server, "GET", path);
        assertEquals(vn.name(), "mockvirtualnetwork");
        assertEquals(vn.properties().resourceGuid(),"1568c76a-73a4-4a60-8dfb-53b823197ccb");
        assertEquals(vn.properties().addressSpace().addressPrefixes().get(0),"10.2.0.0/16");
    }

    //mvn -Dtest=VirtualNetworkApiMockTest#listVirtualNetworks test
    public void listVirtualNetworks() throws InterruptedException {
        server.enqueue(jsonResponse("/listvirtualnetworks.json"));

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);
        List<VirtualNetwork> vnList = vnApi.listVirtualNetworks();
        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks?%s", subscriptionid, resourcegroup, apiVersion);

        assertSent(server, "GET", path);
        assertEquals(vnList.size(),3);
    }

    //mvn -Dtest=VirtualNetworkApiMockTest#createVirtualNetwork test
    public void createVirtualNetwork() throws InterruptedException {

        server.enqueue(jsonResponse("/createvirtualnetwork.json").setStatus("HTTP/1.1 201 Created"));

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);


        //Create properties object
        final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties = VirtualNetwork.VirtualNetworkProperties.builder()
                .addressSpace(VirtualNetwork.AddressSpace.builder()
                                .addressPrefixes(Arrays.asList("10.2.0.0/16"))
                                .build()
                )
                .build();

        VirtualNetwork vn = vnApi.createOrUpdateVirtualNetwork(virtualNetwork, location,  virtualNetworkProperties);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
        String json = String.format("{\"location\":\"%s\",\"properties\":{\"addressSpace\":{\"addressPrefixes\":[\"%s\"]}}}", location, "10.2.0.0/16");
        assertSent(server, "PUT", path, json);
    }

    //mvn -Dtest=VirtualNetworkApiMockTest#deleteVirtualNetwork test
    public void deleteVirtualNetwork() throws InterruptedException {

        server.enqueue(response202());

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);

        vnApi.deleteVirtualNetwork(virtualNetwork);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
        assertSent(server, "DELETE", path);

    }

    //mvn -Dtest=VirtualNetworkApiMockTest#deleteVirtualNetworkResourceDoesNotExist test
    public void deleteVirtualNetworkResourceDoesNotExist() throws InterruptedException {

       server.enqueue(response204());

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);

        vnApi.deleteVirtualNetwork(virtualNetwork);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/virtualNetworks/%s?%s", subscriptionid, resourcegroup, virtualNetwork, apiVersion);
        assertSent(server, "DELETE", path);
    }
}
