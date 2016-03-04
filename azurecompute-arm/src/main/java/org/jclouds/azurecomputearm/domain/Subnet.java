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
import static com.google.common.collect.ImmutableMap.copyOf;

import java.util.List;

import java.util.Date;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;


@AutoValue
public abstract class Subnet {

    @AutoValue
    public abstract static class IpConfiguration{

        public IpConfiguration() {
        }// For AutoValue only!

        public abstract String id();

        @SerializedNames({"id"})
        public static IpConfiguration create(final String id) {
            return new AutoValue_Subnet_IpConfiguration(id);
        }

        public Builder toBuilder() {
            return builder().fromIpConfiguration(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private String id;

            public Builder id(final String id) {
                this.id = id;
                return this;
            }

            public IpConfiguration build() {
                return IpConfiguration.create(id);
            }

            public Builder fromIpConfiguration(final IpConfiguration ipConfiguration) {
                return id(ipConfiguration.id());
            }
        }
    }

    @AutoValue
    public abstract static class SubnetProperties{

        public SubnetProperties() {
        }// For AutoValue only!

        @Nullable
        public abstract String provisioningState();

        @Nullable
        public abstract String addressPrefix();

        @Nullable
        public abstract List<IpConfiguration> ipConfigurations();

        @SerializedNames({"provisioningState", "addressPrefix", "ipConfigurations"})
        public static SubnetProperties create(final String provisioningState, final String addressPrefix, final List<IpConfiguration> ipConfigurations) {
            return new AutoValue_Subnet_SubnetProperties(provisioningState, addressPrefix, (ipConfigurations != null ? copyOf(ipConfigurations) : null));
        }

        public Builder toBuilder() {
            return builder().fromSubnetProperties(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private String provisioningState;
            private String addressPrefix;
            private List<IpConfiguration> ipConfigurations;

            public Builder provisioningState(final String provisioningState) {
                this.provisioningState = provisioningState;
                return this;
            }

            public Builder addressPrefix(final String addressPrefix) {
                this.addressPrefix = addressPrefix;
                return this;
            }

            public Builder ipConfigurations(final List<IpConfiguration> ipConfigurations) {
                this.ipConfigurations = ipConfigurations;
                return this;
            }

            public SubnetProperties build() {
                return SubnetProperties.create(provisioningState, addressPrefix, ipConfigurations);
            }

            public Builder fromSubnetProperties(final SubnetProperties subnetProperties) {
                return provisioningState(subnetProperties.provisioningState()).
                        addressPrefix(subnetProperties.addressPrefix()).
                        ipConfigurations(subnetProperties.ipConfigurations());
            }
        }
    }

    public Subnet() {
    } // For AutoValue only!

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String etag();

    @Nullable
    public abstract SubnetProperties properties();

    @SerializedNames({"name", "id", "etag", "properties"})
    public static Subnet create(final String name,
                                final String id,
                                final String etag,
                                final SubnetProperties properties) {
        return new AutoValue_Subnet(name, id, etag, properties);
    }

    public Builder toBuilder() {
        return builder().fromSubnet(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;

        private String id;

        private String etag;

        private SubnetProperties subnetProperties;

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

        public Builder subnetProperties(final SubnetProperties subnetProperties) {
            this.subnetProperties = subnetProperties;
            return this;
        }

        public Subnet build() {
            return Subnet.create(name, id, etag, subnetProperties);
        }

        public Builder fromSubnet(final Subnet subnet) {
            return name(subnet.name()).
                    id(subnet.id()).
                    etag(subnet.etag()).
                    subnetProperties(subnet.properties());
        }
    }
}
