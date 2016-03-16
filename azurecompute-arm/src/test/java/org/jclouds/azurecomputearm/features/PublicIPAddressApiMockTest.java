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
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

//mvn -Dtest=PublicIPAddressApiMockTest test
@Test(groups = "unit", testName = "NetworkInterfaceCardApiMockTest", singleThreaded = true)
public class PublicIPAddressApiMockTest extends BaseAzureComputeApiMockTest {

    final String subscriptionid = "fakeb2f5-4710-4e93-bdf4-419edbde2178";
    final String resourcegroup = "myresourcegroup";
    final String apiVersion = "api-version=2015-06-15";
    final String location = "northeurope";
    final String publicIpName = "mypublicaddress";

    //mvn -Dtest=PublicIPAddressApiMockTest#getPublicIPAddressInfo test
    public void getPublicIPAddressInfo() throws InterruptedException {
        server.enqueue(jsonResponse("/PublicIPAddressGetInfo.json"));

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);
        PublicIPAddress ip = ipApi.getPublicIPAddress(publicIpName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
        assertSent(server, "GET", path);

        assertNotNull(ip);
        assertEquals(ip.name(), "mypublicaddress");
        assertEquals(ip.location(), "northeurope");
        assertEquals(ip.id(), "/subscriptions/fakeb2f5-4710-4e93-bdf4-419edbde2178/resourceGroups/myresourcegroup/providers/Microsoft.Network/publicIPAddresses/mypublicaddress");
        assertEquals(ip.tags().get("testkey"), "testvalue");
        assertNotNull(ip.properties());
        assertEquals(ip.properties().provisioningState(), "Succeeded");
        assertEquals(ip.properties().ipAddress(), "12.123.12.123");
        assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
        assertEquals(ip.properties().idleTimeoutInMinutes(), 4);
        assertNotNull(ip.properties().dnsSettings());
        assertEquals(ip.properties().dnsSettings().domainNameLabel(), "foobar");
        assertEquals(ip.properties().dnsSettings().fqdn(), "foobar.northeurope.cloudapp.azure.com");
        assertNotNull(ip.properties().ipConfiguration());
        assertEquals(ip.properties().ipConfiguration().id(), "/subscriptions/fakeb2f5-4710-4e93-bdf4-419edbde2178/resourceGroups/myresourcegroup/providers/Microsoft.Network/networkInterfaces/myNic/ipConfigurations/myip1");
    }

    //mvn -Dtest=PublicIPAddressApiMockTest#listPublicIPAddresses test
    public void listPublicIPAddresses() throws InterruptedException {
        server.enqueue(jsonResponse("/PublicIPAddressList.json"));

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);
        List<PublicIPAddress> ipList = ipApi.listPublicIPAddresses();

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses?%s", subscriptionid, resourcegroup, apiVersion);
        assertSent(server, "GET", path);
        assertEquals(ipList.size(), 4);
    }

    //mvn -Dtest=PublicIPAddressApiMockTest#createPublicIPAddress test
    public void createPublicIPAddress() throws InterruptedException {

        server.enqueue(jsonResponse("/PublicIPAddressCreate.json").setStatus("HTTP/1.1 201 Created"));

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);

        final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");

        PublicIPAddress.PublicIPProperties properties = PublicIPAddress.PublicIPProperties.builder().
                publicIPAllocationMethod("Static").
                idleTimeoutInMinutes(4).
                dnsSettings(PublicIPAddress.DnsSettings.builder().
                                domainNameLabel("foobar").
                                fqdn("foobar.northeurope.cloudapp.azure.com").
                                build())
                .build();

        PublicIPAddress ip = ipApi.createOrUpdatePublicIPAddress(publicIpName, location, tags, properties);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
        String json = String.format("{ \"location\": \"%s\", \"tags\": { \"testkey\": \"testvalue\" }, \"properties\": { \"publicIPAllocationMethod\": \"Static\", \"idleTimeoutInMinutes\": 4, \"dnsSettings\": { \"domainNameLabel\": \"foobar\", \"fqdn\": \"foobar.northeurope.cloudapp.azure.com\" } } }", location );
        assertSent(server, "PUT", path, json);

        assertNotNull(ip);
        assertEquals(ip.name(), "mypublicaddress");
        assertEquals(ip.location(), "northeurope");
        assertEquals(ip.id(), "/subscriptions/fakeb2f5-4710-4e93-bdf4-419edbde2178/resourceGroups/myresourcegroup/providers/Microsoft.Network/publicIPAddresses/mypublicaddress");
        assertEquals(ip.tags().get("testkey"), "testvalue");
        assertNotNull(ip.properties());
        assertEquals(ip.properties().provisioningState(), "Updating");
        assertNull(ip.properties().ipAddress()); // as we don't get IP address until Succeeded state
        assertEquals(ip.properties().publicIPAllocationMethod(), "Static");
        assertEquals(ip.properties().idleTimeoutInMinutes(), 4);
        assertNotNull(ip.properties().dnsSettings());
        assertEquals(ip.properties().dnsSettings().domainNameLabel(), "foobar");
        assertEquals(ip.properties().dnsSettings().fqdn(), "foobar.northeurope.cloudapp.azure.com");
    }

    //mvn -Dtest=PublicIPAddressApiMockTest#createPublicIPAddressDnsRecordInUse test
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void createPublicIPAddressDnsRecordInUse() throws IllegalArgumentException, InterruptedException {

        server.enqueue(jsonResponse("/PublicIPAddressCreateDnsRecordInUse.json").setStatus("HTTP/1.1 400 Bad Request"));

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);

        final Map<String, String> tags = ImmutableMap.of("testkey", "testvalue");

        PublicIPAddress.PublicIPProperties properties = PublicIPAddress.PublicIPProperties.builder().
                publicIPAllocationMethod("Static").
                idleTimeoutInMinutes(4).
                dnsSettings(PublicIPAddress.DnsSettings.builder().
                        domainNameLabel("foobar").
                        fqdn("foobar.northeurope.cloudapp.azure.com").
                        build())
                .build();

        PublicIPAddress ip = ipApi.createOrUpdatePublicIPAddress(publicIpName, location, tags, properties);

    }
    //mvn -Dtest=PublicIPAddressApiMockTest#deletePublicIPAddress test
    public void deletePublicIPAddress() throws InterruptedException {

        server.enqueue(response202());

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);
        String statusCode = ipApi.deletePublicIPAddress(publicIpName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
        assertSent(server, "DELETE", path);

        assertEquals("202", statusCode);
    }
    //mvn -Dtest=PublicIPAddressApiMockTest#deletePublicIPAddressResourceDoesNotExist test
    public void deletePublicIPAddressResourceDoesNotExist() throws InterruptedException {

        server.enqueue(response204());

        final PublicIPAddressApi ipApi = api.getPublicIPAddressApi(subscriptionid, resourcegroup);
        String statusCode = ipApi.deletePublicIPAddress(publicIpName);

        String path = String.format("/subscriptions/%s/resourcegroups/%s/providers/Microsoft.Network/publicIPAddresses/%s?%s", subscriptionid, resourcegroup, publicIpName, apiVersion);
        assertSent(server, "DELETE", path);

        assertEquals("204", statusCode);
    }
}
