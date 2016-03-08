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

import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static org.jclouds.azurecomputearm.domain.options.ListOptions.Builder.top;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.jclouds.azurecomputearm.domain.ResourceGroup;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;

@Test(groups = "unit", testName = "ResourceGroupApiMockTest", singleThreaded = true)
public class ResourceGroupApiMockTest extends BaseAzureComputeApiMockTest {

   public void testListResourceGroups() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroups.json"));

      Iterable<ResourceGroup> resourceGroups = api.getResourceGroupApi().list().concat();

      assertEquals(size(resourceGroups), 2);

      assertSent(server, "GET", "/resourcegroups");
   }

   public void testListResourceGroupsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<ResourceGroup> resourceGroups = api.getResourceGroupApi().list().concat();

      assertTrue(isEmpty(resourceGroups));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/resourcegroups");
   }

   public void testListResourceGroupsWithOptions() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroups.json"));

      Iterable<ResourceGroup> resourceGroups = api.getResourceGroupApi().list(top(2));

      assertEquals(size(resourceGroups), 2);
      assertEquals(server.getRequestCount(), 1);

      assertSent(server, "GET", "/resourcegroups?%24top=2");
   }

   public void testListResourceGroupsWithOptionsReturns404() throws InterruptedException {
      server.enqueue(response404());

      Iterable<ResourceGroup> resourceGroups = api.getResourceGroupApi().list(top(1));

      assertTrue(isEmpty(resourceGroups));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/resourcegroups?%24top=1");
   }

   public void testCreateResourceGroup() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup.json").setStatus("HTTP/1.1 201 Created"));

      HashMap<String, String> tags = new HashMap<String, String>();
      tags.put("tagname1", "tagvalue1");

      ResourceGroup resourceGroup = api.getResourceGroupApi().create("jcloudstest", "West US", tags);

      assertEquals(resourceGroup.name(), "jcloudstest");
      assertEquals(resourceGroup.location(), "westus");
      assertEquals(resourceGroup.tags().size(), 1);
      assertTrue(resourceGroup.id().contains("jcloudstest"));

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PUT", "/resourcegroups/jcloudstest", String.format("{\"location\":\"%s\", \"tags\":{\"tagname1\":\"tagvalue1\"}}", "West US"));
   }

   public void testGetResourceGroup() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroup.json"));

      ResourceGroup resourceGroup = api.getResourceGroupApi().get("jcloudstest");

      assertEquals(resourceGroup.name(), "jcloudstest");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/resourcegroups/jcloudstest");
   }

   public void testGetResourceGroupReturns404() throws InterruptedException {
      server.enqueue(response404());

      ResourceGroup resourceGroup = api.getResourceGroupApi().get("jcloudstest");

      assertNull(resourceGroup);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "GET", "/resourcegroups/jcloudstest");
   }

   public void testUpdateResourceGroupTags() throws InterruptedException {
      server.enqueue(jsonResponse("/resourcegroupupdated.json"));

      HashMap<String, String> tags = new HashMap<String, String>();

      ResourceGroup resourceGroup = api.getResourceGroupApi().update("jcloudstest", tags);


      assertEquals(resourceGroup.tags().size(), 0);

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "PATCH", "/resourcegroups/jcloudstest", "{\"tags\":{}}");
   }

   public void testDeleteResourceGroup() throws InterruptedException {
      server.enqueue(response204());

      api.getResourceGroupApi().delete("jcloudstest");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/resourcegroups/jcloudstest");
   }

   public void testDeleteResourceGroupReturns404() throws InterruptedException {
      server.enqueue(response404());

      api.getResourceGroupApi().delete("jcloudstest");

      assertEquals(server.getRequestCount(), 1);
      assertSent(server, "DELETE", "/resourcegroups/jcloudstest");
   }

   private ResourceGroup ResourceGroupFromResource(String resource) {
      return onlyObjectFromResource(resource, new TypeToken<Map<String, ResourceGroup>>() {
         private static final long serialVersionUID = 1L;
      });
   }
}
