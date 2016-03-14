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
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.List;

import org.jclouds.azurecomputearm.domain.ResourceGroup;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiLiveTest;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "live", testName = "ResourceGroupApiLiveTest")
public class ResourceGroupApiLiveTest extends BaseAzureComputeApiLiveTest {

   final String subscriptionid =  getSubscriptionId();
   String resourcegroup;

   @BeforeClass
   @Override
   public void setup(){
      super.setup();
      resourcegroup = getResourceGroupName();
   }

   private ResourceGroupApi api() {
      return api.getResourceGroupApi(subscriptionid);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testList() {
      final List<ResourceGroup> resourceGroups = api().list();

      assertTrue(resourceGroups.size() > 0);

      final ResourceGroup matching = Iterables.find(resourceGroups, new Predicate<ResourceGroup>() {

         @Override
         public boolean apply(final ResourceGroup group) {
            return resourcegroup.equals(group.name());
         }
      });
      assertNotNull(matching);
   }

   @Test(dependsOnMethods = "testCreate")
   public void testRead() {
      final ResourceGroup group = api().get(resourcegroup);
      assertNotNull(group);
      assertEquals(group.name(), resourcegroup);
      assertEquals(group.location(), LOCATION);
   }

   public void testCreate() {
      HashMap<String, String> tags = new HashMap<String, String>();
      tags.put("tagname1", "tagvalue1");

      final ResourceGroup resourceGroup = api().create("jcloudstest", LOCATION, tags);
      assertEquals(resourceGroup.name(), "jcloudstest");
      assertEquals(resourceGroup.location(), LOCATION);
      assertEquals(resourceGroup.tags().size(), 1);
      assertTrue(resourceGroup.id().contains("jcloudstest"));
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @Test(dependsOnMethods = "testCreate")
   public void testUpdate() {
      HashMap<String, String> tags = new HashMap<String, String>();

      final ResourceGroup resourceGroup = api().update("jcloudstest", tags);

      assertEquals(resourceGroup.tags().size(), 0);
      assertEquals(resourceGroup.properties().provisioningState(), "Succeeded");
   }

   @AfterClass(alwaysRun = true)
   public void testDelete() throws Exception {
      String statusCode =  api().delete(resourcegroup);
      assertTrue(statusCode.equals("202"));
      statusCode =  api().delete("jcloudstest");
      assertTrue(statusCode.equals("202"));
   }
}
