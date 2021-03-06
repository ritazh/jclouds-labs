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
package org.jclouds.azurecompute.domain;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Set;

/**
 * Regions used in Azure.
 *
 * @see <a href="http://azure.microsoft.com/en-us/regions/">this page</a>
 */
public enum Region {

   CENTRAL_US("Central US", "US-IA"),
   EAST_US("East US", "US-VA"),
   EAST_US_2("East US 2", "US-VA"),
   US_GOV_IOWA("US Gov Iowa", "US-IA"),
   US_GOV_VIRGINIA("US Gov Virginia", "US-VA"),
   NORTH_CENTRAL_US("North Central US", "US-IL"),
   SOUTH_CENTRAL_US("South Central US", "US-TX"),
   WEST_US("West US", "US-CA"),
   NORTH_EUROPE("North Europe", "IE"),
   WEST_EUROPE("West Europe", "NL"),
   EAST_ASIA("East Asia", "HK"),
   SOUTH_EAST_ASIA("Southeast Asia", "SG"),
   JAPAN_EAST("Japan East", "JP-11"),
   JAPAN_WEST("Japan West", "JP-27"),
   BRAZIL_SOUTH("Brazil South", "BR"),
   AUSTRALIA_EAST("Australia East", "AU-NSW"),
   AUSTRALIA_SOUTH_EAST("Australia Southeast", "AU-VIC");

   private final String name;

   private final String iso3166Code;

   Region(final String name, final String iso3166Code) {
      this.name = name;
      this.iso3166Code = iso3166Code;
   }

   public String getName() {
      return name;
   }

   public String iso3166Code() {
      return iso3166Code;
   }

   public static Region byName(final String name) {
      Preconditions.checkNotNull(name);

      Region result = null;
      for (Region region : values()) {
         if (name.equals(region.name)) {
            result = region;
         }
      }

      return result;
   }

   public static Set<String> iso3166Codes() {
      return ImmutableSet.copyOf(Iterables.transform(Arrays.asList(values()), new Function<Region, String>() {

         @Override
         public String apply(final Region region) {
            return region.iso3166Code;
         }
      }));
   }
}
