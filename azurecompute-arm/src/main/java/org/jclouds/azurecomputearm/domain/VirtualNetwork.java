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
package org.jclouds.azurecomputearm.domain;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.HashMap;
import java.util.List;

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class VirtualNetwork {

    @AutoValue
    public abstract static class AddressSpace{

        public AddressSpace() {
        } // For AutoValue only!

        public abstract List<String> addressPrefixes();

        @SerializedNames({"addressPrefixes"})
        public static AddressSpace create(final List<String> addressPrefixes) {
            return new AutoValue_VirtualNetwork_AddressSpace(copyOf(addressPrefixes));
        }

        public Builder toBuilder() {
            return builder().fromAddressSpace(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private List<String> addressPrefixes;

            public Builder addressPrefixes(final List<String> addressPrefixes) {
                this.addressPrefixes = addressPrefixes;
                return this;
            }

            public AddressSpace build() {
                return AddressSpace.create(addressPrefixes);
            }

            public Builder fromAddressSpace(final AddressSpace addressSpace) {
                return addressPrefixes(addressSpace.addressPrefixes());
            }
        }
    }

    @AutoValue
    public abstract static class VirtualNetworkProperties{

        public VirtualNetworkProperties() {
        }// For AutoValue only!

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

        public Builder toBuilder() {
            return builder().fromVirtualNetworkProperties(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private String provisioningState;

            private String resourceGuid;

            private AddressSpace addressSpace;

            private List<Subnet> subnets;

            public Builder provisioningState(final String provisioningState) {
                this.provisioningState = provisioningState;
                return this;
            }

            public Builder resourceGuid(final String resourceGuid) {
                this.resourceGuid = resourceGuid;
                return this;
            }

            public Builder addressSpace(final AddressSpace addressSpace) {
                this.addressSpace = addressSpace;
                return this;
            }

            public Builder subnets(final List<Subnet> subnets) {
                this.subnets = subnets;
                return this;
            }

            public VirtualNetworkProperties build() {
                return VirtualNetworkProperties.create(provisioningState, resourceGuid, addressSpace, subnets);
            }

            public Builder fromVirtualNetworkProperties(final VirtualNetworkProperties virtualNetworkProperties) {
                return provisioningState(virtualNetworkProperties.provisioningState()).
                        resourceGuid(virtualNetworkProperties.resourceGuid()).
                        addressSpace(virtualNetworkProperties.addressSpace()).
                        subnets(virtualNetworkProperties.subnets());
            }
        }
    }

    public VirtualNetwork() {
    } // For AutoValue only!

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String etag();

    public abstract String location();

    public abstract VirtualNetworkProperties properties();

    @Nullable
    public abstract HashMap<String, String> tags();

    @SerializedNames({"name", "id", "etag", "location", "properties", "tags"})
    public static VirtualNetwork create(final String name,
                                        final String id,
                                        final String etag,
                                        final String location,
                                        final VirtualNetworkProperties properties,
                                        final HashMap<String, String> tags) {
        return new AutoValue_VirtualNetwork(name, id, etag, location, properties, tags);
    }

    public Builder toBuilder() {
        return builder().fromVirtualNetwork(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;

        private String id;

        private String etag;

        private String location;

        private VirtualNetworkProperties virtualNetworkProperties;

        private HashMap<String, String> tags;

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder etag(final String etag) {
            this.etag = etag;
            return this;
        }

        public Builder location(final String location) {
            this.location = location;
            return this;
        }

        public Builder virtualNetworkProperties(final VirtualNetworkProperties virtualNetworkProperties) {
            this.virtualNetworkProperties = virtualNetworkProperties;
            return this;
        }

        public Builder tags(final HashMap<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public VirtualNetwork build() {
            return VirtualNetwork.create(name, id, etag, location, virtualNetworkProperties, tags);
        }

        public Builder fromVirtualNetwork(final VirtualNetwork virtualNetwork) {
            return name(virtualNetwork.name()).
                    id(virtualNetwork.id()).
                    etag(virtualNetwork.etag()).
                    location(virtualNetwork.location()).
                    virtualNetworkProperties(virtualNetwork.properties()).
                    tags(virtualNetwork.tags());
        }
    }
}
