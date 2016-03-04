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
import com.squareup.okhttp.mockwebserver.MockResponse;
import org.jclouds.azurecomputearm.domain.AvailabilitySet;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "unit", testName = "AvailabilitySetApiMockTest")
public class AvailabilitySetAPIMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      server.enqueue(jsonResponse("/availabilitysets.json"));
      final AvailabilitySetApi availabilitySetApi = api.getAvailabilitySetApi("subscriptionid", "resourcegroup");
      AvailabilitySet.AvailabilitySetProperties properties =
              AvailabilitySet.AvailabilitySetProperties.create(5,3,null);
      assertEquals(availabilitySetApi.list(), ImmutableList.of(
              AvailabilitySet.create("/subscriptions/subscriptionid/locations/eastasia","name",
                      "Microsoft.Compute/availabilitySets","eastasia",
                      null,properties)
      ));
      assertSent(server, "GET",
              "/subscriptions/subscriptionid/resourceGroups/resourcegroup/" + "" +
                      "providers/Microsoft.Compute/availabilitySets?api-version=2015-06-15");
   }

   public void testEmptyList() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(404));

      final AvailabilitySetApi availabilitySetApi = api.getAvailabilitySetApi("subscriptionid", "resourcegroup");

      assertTrue(availabilitySetApi.list().isEmpty());

      assertSent(server, "GET",
              "/subscriptions/subscriptionid/resourceGroups/resourcegroup/" + "" +
                      "providers/Microsoft.Compute/availabilitySets?api-version=2015-06-15");
   }

   public void testCreate() throws Exception {
      server.enqueue(jsonResponse("/availabilityset.json"));

      final AvailabilitySetApi availabilitySetApi = api.getAvailabilitySetApi("subscriptionid", "resourcegroup");

      AvailabilitySet availabilitySet = availabilitySetApi.create("name","name",
              "eastasia", 5, 3);

      assertEquals(availabilitySet.name(),"name");
      assertEquals(availabilitySet.location(),"eastasia");
      assertEquals(availabilitySet.properties().platformFaultDomainCount(),3);
      assertEquals(availabilitySet.properties().platformUpdateDomainCount(),5);

      assertSent(server, "PUT",
              "/subscriptions/subscriptionid/resourceGroups/resourcegroup/" + "" +
                      "providers/Microsoft.Compute/availabilitySets/name?api-version=2015-06-15");
   }

   public void testGet() throws Exception {
      server.enqueue(jsonResponse("/availabilityset.json"));

      final AvailabilitySetApi availabilitySetApi = api.getAvailabilitySetApi("subscriptionid", "resourcegroup");

      AvailabilitySet availabilitySet = availabilitySetApi.get("name");

      assertEquals(availabilitySet.name(),"name");
      assertEquals(availabilitySet.location(),"eastasia");
      assertEquals(availabilitySet.properties().platformFaultDomainCount(),3);
      assertEquals(availabilitySet.properties().platformUpdateDomainCount(),5);

      assertSent(server, "GET",
              "/subscriptions/subscriptionid/resourceGroups/resourcegroup/" + "" +
                      "providers/Microsoft.Compute/availabilitySets/name?api-version=2015-06-15");
   }

   public void testDelete() throws Exception {
      server.enqueue(new MockResponse().setResponseCode(200));

      final AvailabilitySetApi availabilitySetApi = api.getAvailabilitySetApi("subscriptionid", "resourcegroup");

      availabilitySetApi.delete("name");

      assertSent(server, "DELETE",
              "/subscriptions/subscriptionid/resourceGroups/resourcegroup/" + "" +
                      "providers/Microsoft.Compute/availabilitySets/name?api-version=2015-06-15");
   }
}
