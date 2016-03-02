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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecomputearm.domain.Availability;
import org.jclouds.azurecomputearm.domain.CreateStorageServiceParams;
import org.jclouds.azurecomputearm.domain.StorageService;
import org.jclouds.azurecomputearm.domain.StorageServiceKeys;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecomputearm.xml.ListStorageServiceHandlerTest;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Test(groups = "unit", testName = "StorageAccountApiMockTest")
public class StorageAccountApiMockTest extends BaseAzureComputeApiMockTest {

   private String subsriptionId = "1234";
   private String resourceGroup = "resourceGroup";

   public void testList() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(jsonResponse("/storageAccounts.json"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         List<StorageService> list = api.list();
         assertEquals(list, ListStorageServiceHandlerTest.expected());

         assertSentJSON(server, "GET", "/subscriptions/" + subsriptionId + "/providers/Microsoft.Storage/storageAccounts?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   public void testEmptyList() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertTrue(api.list().isEmpty());

         assertSentJSON(server, "GET", "/subscriptions/" + subsriptionId + "/providers/Microsoft.Storage/storageAccounts?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   public void testCreate() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(jsonResponse("/storageCreateResponse.json"));
      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertEquals(api.create("name-of-storage-account","westus",
                 ImmutableMap.of("property_name", "property_value"),
                 ImmutableMap.of("accountType", StorageService.AccountType.Premium_LRS.toString())), getCreateResponse());

         assertSentJSON(server, "PUT", "/subscriptions/" + subsriptionId + "/resourcegroups/resourceGroup/providers/Microsoft.Storage/storageAccounts/name-of-storage-account?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   public void testIsAvailable() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(jsonResponse("/isavailablestorageservice.json"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertEquals(api.isAvailable("serviceName"),
                 Availability.create("true"));

         assertSentJSON(server, "POST", "/subscriptions/" + subsriptionId + "/providers/Microsoft.Storage/checkNameAvailability?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   public void testGet() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(jsonResponse("/storageservices.json"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertEquals(api.get("serviceName"), ListStorageServiceHandlerTest.expected().get(0));

         assertSentJSON(server, "GET", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup + "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   public void testNullGet() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertNull(api.get("serviceName"));

         assertSentJSON(server, "GET", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup + "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   public void testGetKeys() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/storageaccountkeys.xml"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertEquals(api.getKeys("serviceName"), StorageServiceKeys.create(
                 "bndO7lydwDkMo4Y0mFvmfLyi2f9aZY7bwfAVWoJWv4mOVK6E9c/exLnFsSm/NMWgifLCfxC/c6QBTbdEvWUA7w==",
                 "/jMLLT3kKqY4K+cUtJTbh7pCBdvG9EMKJxUvaJJAf6W6aUiZe1A1ulXHcibrqRVA2RJE0oUeXQGXLYJ2l85L7A=="));

         assertSentJSON(server, "GET", "/services/storageservices/serviceName/keys");
      } finally {
         server.shutdown();
      }
   }

   public void testNullGetKeys() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(404));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertNull(api.getKeys("serviceName"));

         assertSentJSON(server, "GET", "/services/storageservices/serviceName/keys");
      } finally {
         server.shutdown();
      }
   }

   public void testRegenerateKeys() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertEquals(api.regenerateKeys("serviceName"), "request-1");

         assertSentJSON(server, "POST", "/services/storageservices/serviceName/keys?action=regenerate");
      } finally {
         server.shutdown();
      }
   }

   public void testUpdate() throws Exception {
      final MockWebServer server = mockAzureManagementServer();
      server.enqueue(requestIdResponse("request-1"));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         assertEquals(api.update("serviceName", null,
                 ImmutableMap.of("property_name", "property_value"), null), "request-1");

         assertSentJSON(server, "PATCH", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup + "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   public void testDelete() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(new MockResponse().setResponseCode(204));

      try {
         final StorageAccountApi api = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

         api.delete("serviceName");

         assertSentJSON(server, "DELETE", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup + "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
      } finally {
         server.shutdown();
      }
   }

   private CreateStorageServiceParams getCreateResponse() {
      CreateStorageServiceParams.Builder paramsBuilder = CreateStorageServiceParams.builder();
      paramsBuilder.location("westus");
      paramsBuilder.tags(ImmutableMap.of("property_name", "property_value"));
      paramsBuilder.properties(ImmutableMap.of("accountType", StorageService.AccountType.Premium_LRS.toString()));
      return paramsBuilder.build();
   }
   private StorageService getStrorageAccount() {
      DateService DATE_SERVICE = new SimpleDateFormatDateService();
      Map<String, String> endpoints = new HashMap<String, String>();
      endpoints.put("blob","https://jannenstorage.blob.core.windows.net/");
      endpoints.put("file","https://jannenstorage.file.core.windows.net/");
      endpoints.put("queue","https://jannenstorage.queue.core.windows.net/");
      endpoints.put("table","https://jannenstorage.table.core.windows.net/");
      Map<String, String> secondaryEndpoints = new HashMap<String, String>();
      secondaryEndpoints.put("blob","https://jannenstorage-secondary.blob.core.windows.net/");
      secondaryEndpoints.put("queue","https://jannenstorage-secondary.queue.core.windows.net/");
      secondaryEndpoints.put("table","https://jannenstorage-secondary.table.core.windows.net/");


      String location = "westus";
      String secondaryLocation = "eastus";
      final StorageService.StorageServiceProperties props = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T13:04:45.0890883Z"),
              endpoints, location,
              StorageService.Status.Succeeded, secondaryEndpoints,secondaryLocation, StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);

      final Map<String, String> tags = ImmutableMap.of(
              "key1", "value1",
              "key2", "value2");

      return StorageService.create(
              "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup/providers/Microsoft.Storage/storageAccounts/jannenstorage",
              "jannenstorage",location, tags, null, props);
   }
}
