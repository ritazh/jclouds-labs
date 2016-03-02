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
package org.jclouds.azurecomputearm.xml;

import static org.testng.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jclouds.azurecomputearm.domain.StorageService;
import org.jclouds.date.DateService;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "ListStorageServiceHandlerTest")
public class ListStorageServiceHandlerTest extends BaseHandlerTest {

   private static final DateService DATE_SERVICE = new SimpleDateFormatDateService();

   public void list() throws MalformedURLException {
      final InputStream input = getClass().getResourceAsStream("/storageservices.json");
      final List<StorageService> result = factory.create(
              new ListStorageServicesHandler(new StorageServiceHandler(DATE_SERVICE))).
              parse(input);
      assertEquals(result, expected());
   }

   public static List<StorageService> expected() throws MalformedURLException {
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
              StorageService.Status.Succeeded, secondaryEndpoints,secondaryLocation, StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);
      final StorageService.StorageServiceProperties props2 = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T13:11:43.8265672Z"),
              endpoints2, location,
              StorageService.Status.Succeeded, secondaryEndpoints2,secondaryLocation, StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);
      final StorageService.StorageServiceProperties props3 = StorageService.StorageServiceProperties.create(
              StorageService.AccountType.Standard_RAGRS,
              DATE_SERVICE.iso8601DateOrSecondsDateParse("2016-02-24T14:12:59.5223315Z"),
              endpoints3, location,
              StorageService.Status.Succeeded, secondaryEndpoints3,secondaryLocation, StorageService.RegionStatus.available,
              StorageService.RegionStatus.available);

      final Map<String, String> tags = ImmutableMap.of(
              "key1", "value1",
              "key2", "value2");

      return ImmutableList.of(StorageService.create(
              "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup/providers/Microsoft.Storage/storageAccounts/jannenstorage",
              "jannenstorage",location, tags, "Microsoft.Storage/storageAccounts", props),
              StorageService.create(
                      "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup/providers/Microsoft.Storage/storageAccounts/jannenstorage2",
                      "jannenstorage2",location, tags, "Microsoft.Storage/storageAccounts", props2),
              StorageService.create(
                      "/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec/resourceGroups/jannegroup/providers/Microsoft.Storage/storageAccounts/jannenstorage3",
                      "jannenstorage3",location, tags, "Microsoft.Storage/storageAccounts", props3));
   }
}
