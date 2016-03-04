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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jclouds.azurecomputearm.domain.*;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Test(groups = "unit", testName = "StorageAccountApiMockTest", singleThreaded = true)
public class StorageAccountApiMockTest extends BaseAzureComputeApiMockTest {

   private String subsriptionId = "1234";
   private String resourceGroup = "resourceGroup";

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/storageAccounts.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      List<StorageService> list = storageAPI.list();
      assertEquals(list, expected());

      assertSent(server, "GET", "/subscriptions/" + subsriptionId +
              "/providers/Microsoft.Storage/storageAccounts?api-version=2015-06-15");
   }

   public void testEmptyList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertTrue(storageAPI.list().isEmpty());

      assertSent(server, "GET", "/subscriptions/" + subsriptionId +
              "/providers/Microsoft.Storage/storageAccounts?api-version=2015-06-15");
   }

   public void testCreate() throws Exception {
      server.enqueue(jsonResponse("/storageCreateResponse.json"));
      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertEquals(storageAPI.create("name-of-storage-account","westus",
              ImmutableMap.of("property_name", "property_value"),
              ImmutableMap.of("accountType", StorageService.AccountType.Premium_LRS.toString())),
              getCreateResponse());

      assertSent(server, "PUT", "/subscriptions/" + subsriptionId +
              "/resourcegroups/resourceGroup/providers/Microsoft.Storage/" +
              "storageAccounts/name-of-storage-account?api-version=2015-06-15");
   }

   public void testIsAvailable() throws Exception {
      server.enqueue(jsonResponse("/isavailablestorageservice.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertEquals(storageAPI.isAvailable("serviceName"),
              Availability.create("true"));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId +
              "/providers/Microsoft.Storage/checkNameAvailability?api-version=2015-06-15");
   }

   public void testGet() throws Exception {
      server.enqueue(jsonResponse("/storageservices.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertEquals(storageAPI.get("serviceName"), expected().get(0));

      assertSent(server, "GET", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
   }

   public void testNullGet() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertNull(storageAPI.get("serviceName"));

      assertSent(server, "GET", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
   }

   public void testGetKeys() throws Exception {
      server.enqueue(jsonResponse("/storageaccountkeys.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertEquals(storageAPI.getKeys("serviceName"), StorageServiceKeys.create(
              "bndO7lydwDkMo4Y0mFvmfLyi2f9aZY7bwfAVWoJWv4mOVK6E9c/exLnFsSm/NMWgifLCfxC/c6QBTbdEvWUA7w==",
              "/jMLLT3kKqY4K+cUtJTbh7pCBdvG9EMKJxUvaJJAf6W6aUiZe1A1ulXHcibrqRVA2RJE0oUeXQGXLYJ2l85L7A=="));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/serviceName/listKeys?api-version=2015-06-15");
   }

   public void testNullGetKeys() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertNull(storageAPI.getKeys("serviceName"));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/serviceName/listKeys?api-version=2015-06-15");
   }

   public void testRegenerateKeys() throws Exception {
      server.enqueue(jsonResponse("/storageaccountkeys.json"));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      assertEquals(storageAPI.regenerateKeys("serviceName","key1"), StorageServiceKeys.create(
              "bndO7lydwDkMo4Y0mFvmfLyi2f9aZY7bwfAVWoJWv4mOVK6E9c/exLnFsSm/NMWgifLCfxC/c6QBTbdEvWUA7w==",
              "/jMLLT3kKqY4K+cUtJTbh7pCBdvG9EMKJxUvaJJAf6W6aUiZe1A1ulXHcibrqRVA2RJE0oUeXQGXLYJ2l85L7A=="));

      assertSent(server, "POST", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/serviceName/regenerateKey?api-version=2015-06-15");
   }

   public void testUpdate() throws Exception {
      // TODO: org.jclouds.http.HttpResponseException: HTTP method PATCH doesn't support output
      // There is bug in jClouds HTTP implementation which breaks HTTP PATCH mock tests
      /*
      server.enqueue(jsonResponse("/storageaccountupdate.json"));

      final StorageAccountApi storageAPI = api(server.getUrl("/")).getStorageAccountApi(subsriptionId, resourceGroup);

      StorageServiceUpdateParams.StorageServiceUpdateProperties props =
      StorageServiceUpdateParams.StorageServiceUpdateProperties.create(null,null,null,null,null,null,null,null,null);

      final StorageServiceUpdateParams params = storageAPI.update("serviceName", props,
              ImmutableMap.of("another_property_name", "another_property_value"));

      assertTrue(params.tags().containsKey("another_property_name"));

      assertSent(server, "PATCH", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
      "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
      */
   }

   public void testDelete() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(204));

      final StorageAccountApi storageAPI = api.getStorageAccountApi(subsriptionId, resourceGroup);

      storageAPI.delete("serviceName");

      assertSent(server, "DELETE", "/subscriptions/" + subsriptionId + "/resourcegroups/" + resourceGroup +
              "/providers/Microsoft.Storage/storageAccounts/serviceName?api-version=2015-06-15");
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
              endpoints,
              location,
              StorageService.Status.Succeeded,
              secondaryEndpoints,secondaryLocation,
              StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);

      final Map<String, String> tags = ImmutableMap.of(
              "key1", "value1",
              "key2", "value2");

      return StorageService.create(
              "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup" +
                      "/providers/Microsoft.Storage/storageAccounts/jannenstorage",
              "jannenstorage",location, tags, null, props);
   }

   private List<StorageService> expected() throws MalformedURLException {
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
      Map<String, String> endpoints2 = new HashMap<String, String>();
      endpoints2.put("blob","https://jannenstorage2.blob.core.windows.net/");
      endpoints2.put("file","https://jannenstorage2.file.core.windows.net/");
      endpoints2.put("queue","https://jannenstorage2.queue.core.windows.net/");
      endpoints2.put("table","https://jannenstorage2.table.core.windows.net/");
      Map<String, String> secondaryEndpoints2 = new HashMap<String, String>();
      secondaryEndpoints2.put("blob","https://jannenstorage2-secondary.blob.core.windows.net/");
      secondaryEndpoints2.put("queue","https://jannenstorage2-secondary.queue.core.windows.net/");
      secondaryEndpoints2.put("table","https://jannenstorage2-secondary.table.core.windows.net/");
      Map<String, String> endpoints3 = new HashMap<String, String>();
      endpoints3.put("blob","https://jannenstorage3.blob.core.windows.net/");
      endpoints3.put("file","https://jannenstorage3.file.core.windows.net/");
      endpoints3.put("queue","https://jannenstorage3.queue.core.windows.net/");
      endpoints3.put("table","https://jannenstorage3.table.core.windows.net/");
      Map<String, String> secondaryEndpoints3 = new HashMap<String, String>();
      secondaryEndpoints3.put("blob","https://jannenstorage3-secondary.blob.core.windows.net/");
      secondaryEndpoints3.put("queue","https://jannenstorage3-secondary.queue.core.windows.net/");
      secondaryEndpoints3.put("table","https://jannenstorage3-secondary.table.core.windows.net/");


      String location = "westus";
      String secondaryLocation = "eastus";
      final StorageService.StorageServiceProperties props = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T13:04:45.0890883Z"),
              endpoints, location,
              StorageService.Status.Succeeded, secondaryEndpoints,secondaryLocation,
              StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);
      final StorageService.StorageServiceProperties props2 = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T13:11:43.8265672Z"),
              endpoints2, location,
              StorageService.Status.Succeeded, secondaryEndpoints2,secondaryLocation,
              StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);
      final StorageService.StorageServiceProperties props3 = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T14:12:59.5223315Z"),
              endpoints3, location,
              StorageService.Status.Succeeded, secondaryEndpoints3,secondaryLocation,
              StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);

      final Map<String, String> tags = ImmutableMap.of(
              "key1", "value1",
              "key2", "value2");

      return ImmutableList.of(StorageService.create(
              "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup/" +
                      "providers/Microsoft.Storage/storageAccounts/jannenstorage",
              "jannenstorage",location, tags, "Microsoft.Storage/storageAccounts", props),
              StorageService.create(
                      "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup/" +
                              "providers/Microsoft.Storage/storageAccounts/jannenstorage2",
                      "jannenstorage2",location, tags, "Microsoft.Storage/storageAccounts", props2),
              StorageService.create(
                      "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup/" +
                              "providers/Microsoft.Storage/storageAccounts/jannenstorage3",
                      "jannenstorage3",location, tags, "Microsoft.Storage/storageAccounts", props3));
   }

}
