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

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.Map;
import java.util.List;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class VirtualNetwork {

    @AutoValue
    public abstract static class AddressSpace{

        public abstract List<String> addressPrefixes();

        @SerializedNames({"addressPrefixes"})
        public static AddressSpace create(final List<String> addressPrefixes) {
            return new AutoValue_VirtualNetwork_AddressSpace(copyOf(addressPrefixes));
        }
    }

    @AutoValue
    public abstract static class VirtualNetworkProperties{

        @Nullable
        public abstract String provisioningState();

        @Nullable
        public abstract String resourceGuid();

        public abstract AddressSpace addressSpace();

        @Nullable
        public abstract List<Subnet> subnets();

        @SerializedNames({"provisioningState", "resourceGuid", "addressSpace", "subnets"})
        public static VirtualNetworkProperties create(final String provisioningState, final String resourceGuid, final AddressSpace addressSpace, final List<Subnet> subnets) {

            return new AutoValue_VirtualNetwork_VirtualNetworkProperties(provisioningState, resourceGuid, addressSpace, subnets != null ? copyOf(subnets) : null);
        }
    }

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String etag();

    public abstract String location();

    public abstract VirtualNetworkProperties properties();

    @Nullable
    public abstract Map<String, String> tags();

    @SerializedNames({"name", "id", "etag", "location", "properties", "tags"})
    public static VirtualNetwork create(final String name,
                                        final String id,
                                        final String etag,
                                        final String location,
                                        final VirtualNetworkProperties properties,
                                        final Map<String, String> tags) {
        return new AutoValue_VirtualNetwork(name, id, etag, location, properties, tags == null ? ImmutableMap.<String, String>builder().build() : ImmutableMap.copyOf(tags));
    }
}
