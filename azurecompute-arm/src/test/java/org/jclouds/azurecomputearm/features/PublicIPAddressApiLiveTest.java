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
import org.jclouds.azurecomputearm.domain.PublicIPAddress;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

//mvn -Dtest=PublicIPAddressApiLiveTest -Dtest.azurecompute-arm.identity="f....a" -Dtest.azurecompute-arm.subscriptionid="626f67f6-8fd0-xxxx-yyyy-e3ce95f7dfec" -Dtest.azurecompute-arm.resourcegroup="hardcodedgroup" -Dtest.azurecompute-arm.credential="yorupass" -Dtest.azurecompute-arm.endpoint="https://management.azure.com/" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/youraccount.onmicrosoft.com/oauth2/token" test

@Test(groups = "live", singleThreaded = true)
public class PublicIPAddressApiLiveTest extends BaseAzureComputeApiLiveTest {

    final String publicIpAddressName = "myipaddress";
    final String subscriptionid =  getSubscriptionId();
    String resourcegroup;

    @BeforeClass
    @Override
    public void setup(){
        super.setup();
        resourcegroup = getResourceGroupName();
    }

    @Test(groups = "live")
    public void deletePublicIPAddressResourceDoesNotExist() {
        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);
        String statusCode = ipApi.deletePublicIPAddress(publicIpAddressName);
        assertTrue(statusCode.equals("204"));
    }
    
    @Test(groups = "live", dependsOnMethods = "deletePublicIPAddressResourceDoesNotExist")
    public void createPublicIPAddress() {

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);

        final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");

        PublicIPAddress.PublicIPProperties properties = PublicIPAddress.PublicIPProperties.builder().
                publicIPAllocationMethod("Static").
                idleTimeoutInMinutes(4).
                build();

        PublicIPAddress ip = ipApi.createOrUpdatePublicIPAddress(publicIpAddressName, LOCATION, tags, properties);

        assertNotNull(ip);
        assertEquals(ip.name(), publicIpAddressName);
        assertEquals(ip.location(), LOCATION);
        assertEquals(ip.id(), String.format("/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/publicIPAddresses/%s", subscriptionid, resourcegroup, publicIpAddressName));
        assertEquals(ip.tags().get("testkey"), "testvalue");
        assertNotNull(ip.properties());
        assertEquals(ip.properties().provisioningState(), "Updating");
        assertNull(ip.properties().ipAddress()); // as we don't get IP address until Succeeded state
        assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
        assertEquals(ip.properties().idleTimeoutInMinutes(), 4);
    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#getVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "createPublicIPAddress")
    public void getPublicIPAddress() {

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);

        PublicIPAddress ip = ipApi.getPublicIPAddress(publicIpAddressName);

        //Poll until resource is ready to be used
        while (ip.properties().provisioningState().equals("Updating")){
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ip = ipApi.getPublicIPAddress(publicIpAddressName);
        }

        assertNotNull(ip);
        assertEquals(ip.name(), publicIpAddressName);
        assertEquals(ip.location(), LOCATION);
        assertEquals(ip.id(), String.format("/subscriptions/%s/resourceGroups/%s/providers/Microsoft.Network/publicIPAddresses/%s", subscriptionid, resourcegroup, publicIpAddressName));
        assertEquals(ip.tags().get("testkey"), "testvalue");
        assertNotNull(ip.properties());
        assertEquals(ip.properties().provisioningState(), "Succeeded");
        assertNotNull(ip.properties().ipAddress()); // by this time we should have IP address already
        assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
        assertEquals(ip.properties().idleTimeoutInMinutes(), 4);
    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#listVirtualNetworks test
    @Test(groups = "live", dependsOnMethods = "getPublicIPAddress")
    public void listPublicIPAddresses() {

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);

        List<PublicIPAddress> ipList = ipApi.listPublicIPAddresses();

        assertTrue(ipList.size() > 0);
    }


    //mvn -Dtest=VirtualNetworkApiLiveTest#deleteVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "listPublicIPAddresses", alwaysRun = true)
    public void deletePublicIPAddress() {
        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);
        String statusCode = ipApi.deletePublicIPAddress(publicIpAddressName);
        assertTrue(statusCode.equals("202"));
    }

}
