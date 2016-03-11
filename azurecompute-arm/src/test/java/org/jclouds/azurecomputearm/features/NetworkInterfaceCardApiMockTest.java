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
import org.jclouds.azurecomputearm.domain.IpConfiguration;
import org.jclouds.azurecomputearm.domain.NetworkInterfaceCard;
import org.jclouds.azurecomputearm.domain.Subnet;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

//mvn -Dtest=NetworkInterfaceCardApiMockTest test
@Test(groups = "unit", testName = "NetworkInterfaceCardApiMockTest", singleThreaded = true)
public class NetworkInterfaceCardApiMockTest extends BaseAzureComputeApiMockTest {

    final String subscriptionid = "12345678-2749-4e68-9dcf-e21cda85a132";
    final String resourcegroup = "myresourcegroup";
    final String apiVersion = "api-version=2015-06-15";
    final String location = "northeurope";
    final String nicName = "myNic";

    //mvn -Dtest=NetworkInterfaceCardApiMockTest#getNetworkInterfaceCard test
    public void getNetworkInterfaceCard() throws InterruptedException {
        server.enqueue(jsonResponse("/getnetworkinterfacecard.json"));

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);
        NetworkInterfaceCard nic = nicApi.getNetworkInterfaceCard(nicName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
        assertSent(server, "GET", path);
        assertNotNull(nic);
        assertEquals(nic.name(), nicName);
        assertEquals(nic.properties().ipConfigurations().get(0).name(), "myip1");
        assertEquals(nic.tags().get("mycustomtag"), "foobar");
    }

    //mvn -Dtest=NetworkInterfaceCardApiMockTest#listNetworkInterfaceCards test
    public void listNetworkInterfaceCards() throws InterruptedException {
        server.enqueue(jsonResponse("/listnetworkinterfaces.json"));

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);
        List<NetworkInterfaceCard> nicList = nicApi.listNetworkInterfaceCards();
        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces?%s", subscriptionid, resourcegroup, apiVersion);

        assertSent(server, "GET", path);
        assertTrue(nicList.size() == 2);
        assertTrue(nicList.get(0).properties().ipConfigurations().size() > 0);
        assertEquals(nicList.get(0).properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
        assertTrue(nicList.get(1).properties().ipConfigurations().size() > 0);
        assertEquals(nicList.get(1).properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Static");
    }

    //mvn -Dtest=NetworkInterfaceCardApiMockTest#createNetworkInterfaceCard test
    public void createNetworkInterfaceCard() throws InterruptedException {

        server.enqueue(jsonResponse("/createnetworkinterfacecard.json").setStatus("HTTP/1.1 201 Created"));

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);


        final String SubnetID = "/subscriptions/" + subscriptionid + "/resourceGroups/azurearmtesting/providers/Microsoft.Network/virtualNetworks/myvirtualnetwork/subnets/mysubnet";
        //Create properties object
        final NetworkInterfaceCard.NetworkInterfaceCardProperties networkInterfaceCardProperties =
                NetworkInterfaceCard.NetworkInterfaceCardProperties.builder()
                        .ipConfigurations(
                                Arrays.asList(
                                IpConfiguration.builder()
                                    .name("myipconfig")
                                    .properties(
                                            IpConfiguration.IpConfigurationProperties.builder()
                                                .subnet(Subnet.builder().id(SubnetID).build())
                                                .privateIPAllocationMethod("Dynamic")
                                                .build()
                                    )
                                    .build()
                                ))
                        .build();

        final Map<String, String> tags = ImmutableMap.of("mycustomtag", "foobar");

        NetworkInterfaceCard nic = nicApi.createOrUpdateNetworkInterfaceCard(nicName, location,  networkInterfaceCardProperties, tags);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
        String json = String.format("{ \"location\":\"%s\", \"tags\": { \"mycustomtag\": \"foobar\" }, \"properties\":{ \"ipConfigurations\":[ { \"name\":\"%s\", \"properties\":{ \"subnet\":{ \"id\": \"%s\" }, \"privateIPAllocationMethod\":\"%s\" } } ] } }", location, "myipconfig", SubnetID, "Dynamic");
        assertSent(server, "PUT", path, json);
        assertEquals(nic.tags().get("mycustomtag"), "foobar");
    }

    //mvn -Dtest=NetworkInterfaceCardApiMockTest#deleteNetworkInterfaceCard test
    public void deleteNetworkInterfaceCard() throws InterruptedException {

        server.enqueue(response202());

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);

        nicApi.deleteNetworkInterfaceCard(nicName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
        assertSent(server, "DELETE", path);

    }

    //mvn -Dtest=NetworkInterfaceCardApiMockTest#deleteNetworkInterfaceCardResourceDoesNotExist test
    public void deleteNetworkInterfaceCardResourceDoesNotExist() throws InterruptedException {

       server.enqueue(response204());

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);

        nicApi.deleteNetworkInterfaceCard(nicName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/networkInterfaces/%s?%s", subscriptionid, resourcegroup, nicName, apiVersion);
        assertSent(server, "DELETE", path);
    }
}
