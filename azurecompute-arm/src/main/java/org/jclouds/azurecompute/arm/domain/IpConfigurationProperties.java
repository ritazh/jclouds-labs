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
package org.jclouds.azurecompute.arm.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class IpConfigurationProperties {

   @Nullable
   public abstract String provisioningState();

   @Nullable
   public abstract String privateIPAddress();

   @Nullable
   public abstract String privateIPAllocationMethod();

   @Nullable
   public abstract IdReference subnet();

   @Nullable
   public abstract IdReference publicIPAddress();

   @SerializedNames({"provisioningState", "privateIPAddress", "privateIPAllocationMethod", "subnet", "publicIPAddress"})
   public static IpConfigurationProperties create(final String provisioningState, final String privateIPAddress, final String privateIPAllocationMethod, final IdReference subnet, final IdReference publicIPAddress) {

      return builder()
              .provisioningState(provisioningState)
              .privateIPAddress(privateIPAddress)
              .privateIPAllocationMethod(privateIPAllocationMethod)
              .subnet(subnet)
              .publicIPAddress(publicIPAddress)
              .build();
   }

   public static Builder builder() {
      return new AutoValue_IpConfigurationProperties.Builder();
   }

   @AutoValue.Builder
   public abstract static class Builder {
      public abstract Builder provisioningState(String provisioningState);

      public abstract Builder privateIPAddress(String privateIPAddress);

      public abstract Builder privateIPAllocationMethod(String privateIPAllocationMethod);

      public abstract Builder subnet(IdReference subnet);

      public abstract Builder publicIPAddress(IdReference publicIPAddress);

      public abstract IpConfigurationProperties build();
   }
}

