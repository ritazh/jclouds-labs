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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.Deployment.ProvisioningState;
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecompute.arm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = "unit", testName = "DeploymentApiMockTest", singleThreaded = true)
public class DeploymentApiMockTest extends BaseAzureComputeApiMockTest {

    private String subscriptionId = "SUBSCRIPTIONID";
    private String resourceGroup = "resourceGroup";
    private String deploymentName = "jcdep1458344383064";
    private String resourceName = "jcres1458344383064";

    private String getPutBody(String template, String mode, String parameters) {
        String body = "{ " +
                "\"properties\" : " +
                "  { " +
                "    \"template\" : " + template + ", " +
                "    \"mode\" : \"" + mode + "\", " +
                "    \"parameters\" : " + parameters + " " +
                "  } " +
                "}";
        return body;
    }

    @Test
    public void testCreateDeployment() throws Exception
    {
        final DeploymentApi deploymentApi = api.getDeploymentApi(resourceGroup);

        // check if deployment accepted
        server.enqueue(jsonResponse("/createdeploymentaccepted.json"));
        String template = "{\"$schema\":\"https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#\",\"contentVersion\":\"1.0.0.0\",\"parameters\":{\"newStorageAccountName\":{\"type\":\"string\",\"metadata\":{\"description\":\"Name of the Storage Account\"}},\"storageAccountType\":{\"type\":\"string\",\"defaultValue\":\"Standard_LRS\",\"allowedValues\":[\"Standard_LRS\",\"Standard_GRS\",\"Standard_ZRS\"],\"metadata\":{\"description\":\"Storage Account type\"}},\"location\":{\"type\":\"string\",\"allowedValues\":[\"East US\",\"West US\",\"West Europe\",\"East Asia\",\"Southeast Asia\"],\"metadata\":{\"description\":\"Location of storage account\"}}},\"resources\":[{\"type\":\"Microsoft.Storage/storageAccounts\",\"name\":\"[parameters('newStorageAccountName')]\",\"apiVersion\":\"2015-05-01-preview\",\"location\":\"[parameters('location')]\",\"properties\":{\"accountType\":\"[parameters('storageAccountType')]\"}}]}";
        String parameters = "{\"newStorageAccountName\":{\"value\":\"" + resourceName + "\"},\"storageAccountType\":{\"value\":\"Standard_LRS\"},\"location\":{\"value\":\"West US\"}}";
        String properties = getPutBody(template, "Incremental", parameters);
        Deployment deployment = deploymentApi.createDeployment(deploymentName, properties);
        assertTrue(deployment != null);
        assertEquals(ProvisioningState.fromString(deployment.properties().provisioningState()), ProvisioningState.ACCEPTED);

        // check if deployment succeeded
        server.enqueue(jsonResponse("/createdeploymentsucceeded.json"));
        deployment = deploymentApi.createDeployment(deploymentName, properties);
        assertTrue(deployment != null);
        assertEquals(ProvisioningState.fromString(deployment.properties().provisioningState()), ProvisioningState.SUCCEEDED);
    }

    @Test
    public void testGetDeployment() throws Exception
    {
        final DeploymentApi deploymentApi = api.getDeploymentApi(resourceGroup);

        // check if deployment succeeded
        server.enqueue(jsonResponse("/createdeploymentsucceeded.json"));
        Deployment deployment = deploymentApi.getDeployment(deploymentName);
        assertTrue(deployment != null);
        assertEquals(ProvisioningState.fromString(deployment.properties().provisioningState()), ProvisioningState.SUCCEEDED);
    }

    @Test
    public void testListDeployment() throws Exception
    {
        final DeploymentApi deploymentApi = api.getDeploymentApi(resourceGroup);

        // check if deployment succeeded
        server.enqueue(jsonResponse("/listdeployments.json"));
        List<Deployment> deployments = deploymentApi.listDeployments();
        assertTrue(deployments.size() > 0);
    }

    @Test
    public void testNullGetDeployment() throws Exception {
        final DeploymentApi deploymentApi = api.getDeploymentApi(resourceGroup);

        server.enqueue(new MockResponse().setResponseCode(404));
        assertNull(deploymentApi.getDeployment("randomName"));

        assertSent(server, "GET", "/subscriptions/" + subscriptionId + "/resourcegroups/" + resourceGroup +
                "/providers/microsoft.resources/deployments/randomName?api-version=2016-02-01");
    }
}
