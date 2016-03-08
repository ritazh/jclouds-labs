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
import org.jclouds.azurecomputearm.domain.AvailabilitySet;
import org.jclouds.azurecomputearm.domain.CreateStorageServiceParams;
import org.jclouds.azurecomputearm.domain.StorageService;
import org.jclouds.azurecomputearm.features.AvailabilitySetApi;
import org.jclouds.azurecomputearm.internal.AbstractAzureComputeApiLiveTest;
import org.jclouds.azurecomputearm.util.ConflictManagementPredicate;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "AvailabilitySetApiLiveTest")
public class AvailabilitySetAPILiveTest extends AbstractAzureComputeApiLiveTest {
   private String availabilitySetName = null;
   private static String LOCATION = "westus";
   private static int COUNT = 3;

   private String getName() {

      if (availabilitySetName == null) {
         availabilitySetName = String.format("%3.24s",
                 System.getProperty("user.name") + RAND + this.getClass().getSimpleName()).toLowerCase();
      }

      return availabilitySetName;
   }

   @BeforeClass
   @Override
   public void setup() {
      super.setup();
      testCreate();
   }

   @AfterClass(alwaysRun = false)
   @Override
   protected void tearDown() {
      super.tearDown();
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      for (AvailabilitySet availabilitySet : api().list()) {
          assertTrue(!availabilitySet.id().isEmpty());
      }
      assertTrue(!api().list().isEmpty());
   }

   @Test
   public void testCreate() {
      AvailabilitySet availabilitySet = api().create(getName(),getName(),LOCATION, COUNT, COUNT);
      assertTrue(!availabilitySet.id().isEmpty());
      Assert.assertEquals(LOCATION, availabilitySet.location());
      Assert.assertEquals(getName(), availabilitySet.name());
      Assert.assertEquals(COUNT, availabilitySet.properties().platformFaultDomainCount());
      Assert.assertEquals(COUNT, availabilitySet.properties().platformUpdateDomainCount());
   }

   @Test(dependsOnMethods = "testCreate")
   public void testGet() {
      AvailabilitySet availabilitySet = api().get(getName());
      Assert.assertEquals(COUNT,
              availabilitySet.properties().platformFaultDomainCount());
      Assert.assertEquals(COUNT,
              availabilitySet.properties().platformUpdateDomainCount());
      Assert.assertEquals(getName(), availabilitySet.name());
      Assert.assertEquals(LOCATION, availabilitySet.location());
   }

   @Test(dependsOnMethods = "testGet")
   public void testDelete() {
      api().delete(getName());
   }

   private AvailabilitySetApi api() {
      return api.getAvailabilitySetApi(getSubscriptionId(), getResourceGroup());
   }
}
