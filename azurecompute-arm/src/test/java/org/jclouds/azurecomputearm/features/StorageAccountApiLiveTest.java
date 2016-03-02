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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecomputearm.domain.CreateStorageServiceParams;
import org.jclouds.azurecomputearm.domain.StorageService;
import org.jclouds.azurecomputearm.domain.StorageServiceKeys;
import org.jclouds.azurecomputearm.domain.UpdateStorageServiceParams;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = "live", testName = "StorageAccountApiLiveTest")
public class StorageAccountApiLiveTest extends BaseAzureComputeApiLiveTest {

   private static final String NAME = String.format("%3.24s",
           RAND + StorageAccountApiLiveTest.class.getSimpleName().toLowerCase());

   private void check(final StorageService storage) {
      assertNotNull(storage.id());
      assertNotNull(storage.name());
      assertNotNull(storage.storageServiceProperties());
      assertNotNull(storage.storageServiceProperties().accountType());
      assertFalse(storage.storageServiceProperties().primaryEndpoints().isEmpty());
      assertNotNull(storage.storageServiceProperties().creationTime());
   }

   @Test()
   public void testList() {
      List<StorageService> storages = api().list();
      assertTrue(storages.size() > 0);
      for (StorageService storage : storages) {
         check(storage);
      }
   }

   @Test()
   public void testIsAvailable() {
      assertTrue(api().isAvailable(NAME).nameAvailable() == "true");
   }

   @Test(dependsOnMethods = "testIsAvailable")
   public void testCreate() {
      CreateStorageServiceParams storage = api().create(NAME,LOCATION, ImmutableMap.of("property_name", "property_value"),
              ImmutableMap.of("accountType", StorageService.AccountType.Standard_ZRS.toString()));
      while (storage == null) {
         storage = api().create(NAME,LOCATION, ImmutableMap.of("property_name", "property_value"),
                 ImmutableMap.of("accountType", StorageService.AccountType.Standard_ZRS.toString()));
      }
      assertEquals(storage.location(), LOCATION);
      assertTrue(!storage.properties().isEmpty());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      final StorageService service = api().get(NAME);
      assertNotNull(service);
      assertEquals(service.name(), NAME);
      assertEquals(service.storageServiceProperties().primaryLocation(), LOCATION);
      assertEquals(service.storageServiceProperties().accountType(), StorageService.AccountType.Standard_ZRS);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGetKeys() {
      final StorageServiceKeys keys = api().getKeys(NAME);
      assertNotNull(keys);
      assertNotNull(keys.key1());
      assertNotNull(keys.key2());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testRegenerateKeys() {
      api().regenerateKeys(NAME);
      //assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdate() {
      final String requestId = api().update(NAME, null, null,
              ImmutableMap.of("another_property_name", "another_property_value"));
      assertTrue(operationSucceeded.apply(requestId), requestId);
   }

   @AfterClass(alwaysRun = true)
   public void testDelete() {
      api().delete(NAME);
      assertFalse(api().isAvailable(NAME).nameAvailable() == "false");
   }

   private StorageAccountApi api() {
      return api.getStorageAccountApi(getSubscriptionId(), getResourceGroup());
   }
}
