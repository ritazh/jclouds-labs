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

import org.jclouds.azurecomputearm.domain.VirtualNetwork;
import org.jclouds.azurecomputearm.internal.AbstractAzureComputeApiLiveTest;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiLiveTest;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


//mvn -Dtest=VirtualNetworkApiLiveTest -Dtest.azurecompute-arm.identity="f....a" -Dtest.azurecompute-arm.subscriptionid="626f67f6-8fd0-xxxx-yyyy-e3ce95f7dfec" -Dtest.azurecompute-arm.resourcegroup="hardcodedgroup" -Dtest.azurecompute-arm.credential="yorupass" -Dtest.azurecompute-arm.endpoint="https://management.azure.com/" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/youraccount.onmicrosoft.com/oauth2/token" test

@Test(groups = "live", singleThreaded = true)
public class VirtualNetworkApiLiveTest extends BaseAzureComputeApiLiveTest {

    final String subscriptionid =  getSubscriptionId();
    final String resourcegroup =  getResourceGroup();

    @Test(groups = "live")
    public void deleteVirtualNetworkResourceDoesNotExist() {

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);

        vnApi.deleteVirtualNetwork(VIRTUAL_NETWORK_NAME);

        //TODO: need to check that we get "no content" back

    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#createVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "deleteVirtualNetworkResourceDoesNotExist")
    public void createVirtualNetwork() {

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);

        //Create properties object
        //Create properties object
        final VirtualNetwork.VirtualNetworkProperties virtualNetworkProperties = VirtualNetwork.VirtualNetworkProperties.builder()
                .addressSpace(VirtualNetwork.AddressSpace.builder()
                        .addressPrefixes(Arrays.asList(DEFAULT_VIRTUALNETWORK_ADDRESS_PREFIX))
                        .build()
                )
                .build();

        VirtualNetwork vn = vnApi.createOrUpdateVirtualNetwork(VIRTUAL_NETWORK_NAME, LOCATION, virtualNetworkProperties);

        assertEquals(VIRTUAL_NETWORK_NAME, vn.name());
        assertEquals(LOCATION, vn.location());
    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#getVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "createVirtualNetwork")
    public void getVirtualNetwork() {

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);
        VirtualNetwork vn = vnApi.getVirtualNetwork(VIRTUAL_NETWORK_NAME);

        assertNotNull(vn.name());
        assertNotNull(vn.location());
        assertNotNull(vn.properties().addressSpace().addressPrefixes());
    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#listVirtualNetworks test
    @Test(groups = "live", dependsOnMethods = "getVirtualNetwork")
    public void listVirtualNetworks() {

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);
        List<VirtualNetwork> vnList = vnApi.listVirtualNetworks();

        assertTrue(vnList.size()>0);
    }



    //mvn -Dtest=VirtualNetworkApiLiveTest#deleteVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "listVirtualNetworks", alwaysRun = true)
    public void deleteVirtualNetwork() {

        final VirtualNetworkApi vnApi = api.getVirtualNetworkApi(subscriptionid, resourcegroup);

        vnApi.deleteVirtualNetwork(VIRTUAL_NETWORK_NAME);

    }

}
