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

package org.apache.jclouds.profitbricks.rest;

import com.google.common.annotations.Beta;
import java.io.Closeable;
import org.apache.jclouds.profitbricks.rest.features.DataCenterApi;
import org.apache.jclouds.profitbricks.rest.features.FirewallApi;
import org.apache.jclouds.profitbricks.rest.features.ImageApi;
import org.apache.jclouds.profitbricks.rest.features.LanApi;
import org.apache.jclouds.profitbricks.rest.features.NicApi;
import org.apache.jclouds.profitbricks.rest.features.ServerApi;
import org.apache.jclouds.profitbricks.rest.features.SnapshotApi;
import org.apache.jclouds.profitbricks.rest.features.VolumeApi;
import org.jclouds.rest.annotations.Delegate;

@Beta
public interface ProfitBricksApi extends Closeable {
   
   @Delegate
   DataCenterApi dataCenterApi();
   
   @Delegate
   ServerApi serverApi();
   
   @Delegate
   VolumeApi volumeApi();

   @Delegate
   ImageApi imageApi();
   
   @Delegate
   SnapshotApi snapshotApi();
   
   @Delegate
   NicApi nicApi();
   
   @Delegate
   LanApi lanApi();
   
   @Delegate
   FirewallApi firewallApi();

}
