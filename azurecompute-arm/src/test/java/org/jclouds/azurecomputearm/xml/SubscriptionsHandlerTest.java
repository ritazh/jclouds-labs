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

import java.io.InputStream;
import java.util.List;

import org.jclouds.azurecomputearm.domain.Subscription;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import org.jclouds.azurecomputearm.domain.RoleSize;

@Test(groups = "unit", testName = "SubscriptionsHandlerTest")
public class SubscriptionsHandlerTest extends BaseHandlerTest {
/*
   public void test() {
      final InputStream is = getClass().getResourceAsStream("/rolesizes.xml");
      final List<RoleSize> result = factory.create(new ListRoleSizesHandler(new RoleSizeHandler())).parse(is);

      assertEquals(result, expected());
   }
*/
   public static List<Subscription> expected() {
      return ImmutableList.of(
              Subscription.create("/subscriptions/626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec","626f67f6-8fd0-4cc3-bc02-e3ce95f7dfec","Free Trial","Enabled")
      );
   }
}
