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
package org.jclouds.azurecomputearm;

import com.google.common.base.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.jclouds.azurecomputearm.domain.NetworkConfiguration;

public class AzureTestUtils {

   @Deprecated //IS virtual network api changed
   public static List<NetworkConfiguration.VirtualNetworkSite> getVirtualNetworkSite(AzureComputeApi api) {
      return new ArrayList<NetworkConfiguration.VirtualNetworkSite>();

      /*
      final VirtualNetworkApi vnapi = api.getVirtualNetworkApi();
      final NetworkConfiguration netConf = vnapi.getNetworkConfiguration();

      return netConf == null
              ? new ArrayList<NetworkConfiguration.VirtualNetworkSite>()
              : new ArrayList<NetworkConfiguration.VirtualNetworkSite>(netConf.virtualNetworkConfiguration().
                      virtualNetworkSites());
      */
   }

   public static class SameVirtualNetworkSiteNamePredicate
           implements Predicate<NetworkConfiguration.VirtualNetworkSite> {

      private final String virtualNetworkSiteName;

      public SameVirtualNetworkSiteNamePredicate(final String virtualNetworkSiteName) {
         this.virtualNetworkSiteName = virtualNetworkSiteName;
      }

      @Override
      public boolean apply(final NetworkConfiguration.VirtualNetworkSite input) {
         return input.name().equals(virtualNetworkSiteName);
      }
   }
}
