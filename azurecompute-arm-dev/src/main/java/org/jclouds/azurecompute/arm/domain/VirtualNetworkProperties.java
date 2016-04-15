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
import com.google.common.collect.ImmutableList;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;
import java.util.List;

@AutoValue
public abstract class VirtualNetworkProperties{

    @Nullable
    public abstract String provisioningState();

    @Nullable
    public abstract String resourceGuid();

    @Nullable
    public abstract AddressSpace addressSpace();

    @Nullable
    public abstract ImmutableList<Subnet>  subnets();

    @SerializedNames({"provisioningState", "resourceGuid", "addressSpace", "subnets"})
    public static VirtualNetworkProperties create(final String provisioningState, String resourceGuid, final AddressSpace addressSpace, final List<Subnet> subnets) {

        VirtualNetworkProperties.Builder builder = VirtualNetworkProperties.builder()
                .provisioningState(provisioningState)
                .resourceGuid(resourceGuid)
                .addressSpace(addressSpace);

        if (subnets != null)   builder.subnets(subnets);

        return builder.build();
    }

    public static Builder builder() {
        return new AutoValue_VirtualNetworkProperties.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder provisioningState(String provisioningState);
        public abstract Builder resourceGuid(String resourceGuid);
        public abstract Builder addressSpace(AddressSpace addressSpace);
        public abstract Builder subnets(List<Subnet> subnets);
        public abstract VirtualNetworkProperties build();
        }
    }
