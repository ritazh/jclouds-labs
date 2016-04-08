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

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCard;
import org.jclouds.azurecompute.arm.domain.Subnet;
import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

@Test(groups = "live", singleThreaded = true)
public class NetworkInterfaceCardApiLiveTest extends BaseAzureComputeApiLiveTest {

    private String resourcegroup;
    private String subnetID;

    @BeforeClass
    @Override
    public void setup(){
        super.setup();

        resourcegroup = getResourceGroupName();

        //Subnets belong to a virtual network so that needs to be created first
        VirtualNetwork vn = getOrCreateVirtualNetwork(VIRTUAL_NETWORK_NAME);
        assertNotNull(vn);

        //Subnet needs to be up & running before NIC can be created
        Subnet subnet = getOrCreateSubnet(DEFAULT_SUBNET_NAME, VIRTUAL_NETWORK_NAME);
        assertNotNull(subnet);
        assertNotNull(subnet.id());
        subnetID = subnet.id();
    }


    @Test(groups = "live")
    public void deleteNetworkInterfaceCardResourceDoesNotExist() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);
        String statusCode = nicApi.deleteNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);
        assertTrue(statusCode.equals("204"));
    }

    @Test(groups = "live", dependsOnMethods = "deleteNetworkInterfaceCardResourceDoesNotExist")
    public void createNetworkInterfaceCard() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);


        //Create properties object
        final NetworkInterfaceCard.NetworkInterfaceCardProperties networkInterfaceCardProperties =
                NetworkInterfaceCard.NetworkInterfaceCardProperties.create(null, null, null,
                        Arrays.asList(IpConfiguration.create("myipconfig", null, null, null,
                                IpConfiguration.IpConfigurationProperties.create(null, null, "Dynamic", IdReference.create(subnetID), null))
                        )
                );

        final Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
        NetworkInterfaceCard nic = nicApi.createOrUpdateNetworkInterfaceCard(NETWORKINTERFACECARD_NAME, LOCATION,  networkInterfaceCardProperties, tags);

        assertEquals(nic.name(), NETWORKINTERFACECARD_NAME);
        assertEquals(nic.location(), LOCATION);
        assertTrue(nic.properties().ipConfigurations().size() > 0 );
        assertEquals(nic.properties().ipConfigurations().get(0).name(), "myipconfig");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().subnet().id(), subnetID);
        assertEquals(nic.tags().get("jclouds"), "livetest");

    }

    @Test(groups = "live", dependsOnMethods = "createNetworkInterfaceCard")
    public void getNetworkInterfaceCard() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);

        NetworkInterfaceCard nic = nicApi.getNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);

        assertEquals(nic.name(), NETWORKINTERFACECARD_NAME);
        assertEquals(nic.location(), LOCATION);
        assertTrue(nic.properties().ipConfigurations().size() > 0 );
        assertEquals(nic.properties().ipConfigurations().get(0).name(), "myipconfig");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().subnet().id(), subnetID);
    }

    @Test(groups = "live", dependsOnMethods = "getNetworkInterfaceCard")
    public void listNetworkInterfaceCards() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);

        List<NetworkInterfaceCard> nicList = nicApi.listNetworkInterfaceCards();

        assertTrue(nicList.size() > 0);
    }


    @Test(groups = "live", dependsOnMethods = "listNetworkInterfaceCards", alwaysRun = true)
    public void deleteNetworkInterfaceCard() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(resourcegroup);

        String statusCode = nicApi.deleteNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);
        assertTrue(statusCode.equals("202"));
    }

}
