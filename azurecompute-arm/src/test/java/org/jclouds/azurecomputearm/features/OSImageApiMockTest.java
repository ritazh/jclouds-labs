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

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import java.util.List;
import org.jclouds.azurecomputearm.domain.Offer;
import org.jclouds.azurecomputearm.domain.Publisher;
import org.jclouds.azurecomputearm.domain.SKU;
import org.jclouds.azurecomputearm.domain.Version;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

@Test(groups = "unit", testName = "OSImageApiMockTest", singleThreaded = true)
public class OSImageApiMockTest extends BaseAzureComputeApiMockTest {

   final String subscriptionid = "12345678-1234-1234-1234-123456789012";
   final String apiversion = "?api-version=2015-06-15";
   final String location = "eastus";
   final String publisher = "MicrosoftWindowsServer";
   final String offer = "WindowsServer";
   final String sku = "2008-R2-SP1";

   final String requestUrl = "/subscriptions/" + subscriptionid + "/providers/Microsoft.Compute/locations/" + location + "/publishers";

   public void testPublishers() throws InterruptedException {
      server.enqueue(jsonResponse("/publishers.json"));

      List<Publisher> publishers = api.getOSImageApi(subscriptionid, location).listPublishers();

      assertEquals(size(publishers), 2);

      assertSent(server, "GET", requestUrl + apiversion);
   }
   public void testOffers() throws InterruptedException {
      server.enqueue(jsonResponse("/offers.json"));

      List<Offer> offers = api.getOSImageApi(subscriptionid, location).listOffers(publisher);

      assertEquals(size(offers), 1);

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers" + apiversion);
   }
   public void testSkus() throws InterruptedException {
      server.enqueue(jsonResponse("/skus.json"));

      List<SKU> skus = api.getOSImageApi(subscriptionid, location).listSKUs(publisher, offer);

      assertEquals(size(skus), 2);

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers/" + offer + "/skus" + apiversion);
   }

   public void testVersions() throws InterruptedException {
      server.enqueue(jsonResponse("/versions.json"));

      List<Version> versions = api.getOSImageApi(subscriptionid, location).listVersions(publisher, offer, sku);

      assertEquals(size(versions), 2);

      assertSent(server, "GET", requestUrl + "/" + publisher + "/artifacttypes/vmimage/offers/" + offer + "/skus/" + sku + "/versions" + apiversion);
   }


}
