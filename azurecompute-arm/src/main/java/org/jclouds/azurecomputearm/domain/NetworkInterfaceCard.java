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

import com.google.auto.value.AutoValue;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.HashMap;
import java.util.List;
import static com.google.common.collect.ImmutableList.copyOf;

@AutoValue
public abstract class NetworkInterfaceCard {


    @AutoValue
    public abstract static class NetworkInterfaceCardProperties{

        public NetworkInterfaceCardProperties() {
        }// For AutoValue only!

        @Nullable
        public abstract String provisioningState();

        @Nullable
        public abstract String resourceGuid();

        @Nullable
        public abstract Boolean enableIPForwarding();

        @Nullable
        public abstract List<IpConfiguration> ipConfigurations();

        @SerializedNames({"provisioningState", "resourceGuid", "enableIPForwarding", "ipConfigurations"/*, "dnsSettings"*/})
        public static NetworkInterfaceCardProperties create(final String provisioningState, final String resourceGuid, final Boolean enableIPForwarding, final List<IpConfiguration> ipConfigurations) {

            return new AutoValue_NetworkInterfaceCard_NetworkInterfaceCardProperties(provisioningState, resourceGuid, enableIPForwarding, ipConfigurations != null ? copyOf(ipConfigurations) : null );
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

            private Boolean enableIPForwarding;

            private List<IpConfiguration> ipConfigurations;

            public Builder provisioningState(final String provisioningState) {
                this.provisioningState = provisioningState;
                return this;
            }

            public Builder resourceGuid(final String resourceGuid) {
                this.resourceGuid = resourceGuid;
                return this;
            }

            public Builder enableIPForwarding(final Boolean enableIPForwarding) {
                this.enableIPForwarding = enableIPForwarding;
                return this;
            }

            public Builder ipConfigurations(final List<IpConfiguration> ipConfigurations) {
                this.ipConfigurations = ipConfigurations;
                return this;
            }

            public NetworkInterfaceCardProperties build() {
                return NetworkInterfaceCardProperties.create(provisioningState, resourceGuid, enableIPForwarding, ipConfigurations);
            }

            public Builder fromVirtualNetworkProperties(final NetworkInterfaceCardProperties networkInterfaceCardProperties) {
                return provisioningState(networkInterfaceCardProperties.provisioningState()).
                        resourceGuid(networkInterfaceCardProperties.resourceGuid()).
                        enableIPForwarding(networkInterfaceCardProperties.enableIPForwarding()).
                        ipConfigurations(networkInterfaceCardProperties.ipConfigurations());
            }
        }
    }

    public NetworkInterfaceCard() {
    } // For AutoValue only!

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String etag();

    @Nullable
    public abstract String location();

    @Nullable
    public abstract NetworkInterfaceCardProperties properties();

    @Nullable
    public abstract HashMap<String, String> tags();

    @SerializedNames({"name", "id", "etag", "location", "properties", "tags"})
    public static NetworkInterfaceCard create(final String name,
                                              final String id,
                                              final String etag,
                                              final String location,
                                              final NetworkInterfaceCardProperties properties,
                                              final HashMap<String, String> tags) {
        return new AutoValue_NetworkInterfaceCard(name, id, etag, location, properties, tags);
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

        private NetworkInterfaceCardProperties properties;

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

        public Builder properties(final NetworkInterfaceCardProperties properties) {
            this.properties = properties;
            return this;
        }

        public Builder tags(final HashMap<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public NetworkInterfaceCard build() {
            return NetworkInterfaceCard.create(name, id, etag, location, properties, tags);
        }

        public Builder fromVirtualNetwork(final NetworkInterfaceCard networkInterfaceCard) {
            return name(networkInterfaceCard.name()).
                    id(networkInterfaceCard.id()).
                    etag(networkInterfaceCard.etag()).
                    location(networkInterfaceCard.location()).
                    properties(networkInterfaceCard.properties()).
                    tags(networkInterfaceCard.tags());
        }
    }
}
