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

import org.jclouds.azurecompute.arm.domain.Subnet;

import org.jclouds.azurecompute.arm.domain.VirtualNetwork;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiLiveTest;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

// For now live tests expect that Virtual Network "jclouds-virtual-network-live-test" is already created in Azure
// using address space 10.2.0.0/16.
// Make sure you delete the "default" subnet in portal after creating the virtual network.
@Test(groups = "live", singleThreaded = true)
public class SubnetApiLiveTest extends BaseAzureComputeApiLiveTest {

    private final String subscriptionid =  "subscriptionid";
    private String resourcegroup;

    @BeforeClass
    @Override
    public void setup(){
        super.setup();
        resourcegroup = getResourceGroupName();

        // Subnets belong to a virtual network so that needs to be created first
        // VN will be deleted when resource group is deleted
        VirtualNetwork vn = getOrCreateVirtualNetwork(VIRTUAL_NETWORK_NAME);
        assertNotNull(vn);
    }

    @Test(groups = "live")
    public void deleteSubnetResourceDoesNotExist() {

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, VIRTUAL_NETWORK_NAME);

        subnetApi.deleteSubnet(DEFAULT_SUBNET_NAME);

        //TODO: need to check that we get "no content" back
    }

    @Test(groups = "live", dependsOnMethods = "deleteSubnetResourceDoesNotExist")
    public void createSubnet() {

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, VIRTUAL_NETWORK_NAME);

        //Create properties object
        //addressPrefix must match Virtual network address space!
        Subnet.SubnetProperties properties = Subnet.SubnetProperties.create(null, DEFAULT_SUBNET_ADDRESS_SPACE, null);

        Subnet subnet = subnetApi.createOrUpdateSubnet(DEFAULT_SUBNET_NAME, properties);

        assertEquals(subnet.name(), DEFAULT_SUBNET_NAME);
        assertEquals(subnet.properties().addressPrefix(), DEFAULT_SUBNET_ADDRESS_SPACE);
    }

    @Test(groups = "live", dependsOnMethods = "createSubnet")
    public void getSubnet() {

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, VIRTUAL_NETWORK_NAME);
        Subnet subnet = subnetApi.getSubnet(DEFAULT_SUBNET_NAME);

        assertNotNull(subnet.name());
        assertNotNull(subnet.properties().addressPrefix());
    }

    @Test(groups = "live", dependsOnMethods = "getSubnet")
    public void listSubnets() {

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, VIRTUAL_NETWORK_NAME);
        List<Subnet> subnets = subnetApi.listSubnets();

        assertTrue(subnets.size() > 0);
    }

    @Test(groups = "live", dependsOnMethods = "listSubnets", alwaysRun = true)
    public void deleteSubnet() {

        final SubnetApi subnetApi = api.getSubnetApi(resourcegroup, VIRTUAL_NETWORK_NAME);
        subnetApi.deleteSubnet(DEFAULT_SUBNET_NAME);
    }

}
