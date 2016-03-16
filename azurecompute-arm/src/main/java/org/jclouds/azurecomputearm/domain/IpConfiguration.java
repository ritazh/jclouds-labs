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

@AutoValue
public abstract class IpConfiguration{

    @AutoValue
    public abstract static class IpConfigurationProperties{

        public IpConfigurationProperties() {
        }// For AutoValue only!

        @Nullable
        public abstract String provisioningState();

        @Nullable
        public abstract String privateIPAddress();

        @Nullable
        public abstract String privateIPAllocationMethod();

        @Nullable
        public abstract IdReference subnet();

        @Nullable
        public  abstract  IdReference publicIPAddress();

        @SerializedNames({"provisioningState", "privateIPAddress", "privateIPAllocationMethod", "subnet", "publicIPAddress"})
        public static IpConfigurationProperties create(final String provisioningState, final String privateIPAddress, final String privateIPAllocationMethod, final IdReference subnet, final IdReference publicIPAddress) {

            return new AutoValue_IpConfiguration_IpConfigurationProperties(provisioningState, privateIPAddress, privateIPAllocationMethod, subnet, publicIPAddress);
        }

        public Builder toBuilder() {
            return builder().fromIpConfigurationProperties(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private String provisioningState;

            private String privateIPAddress;

            private String privateIPAllocationMethod;

            private IdReference subnet;

            private IdReference publicIPAddress;

            public Builder provisioningState(final String provisioningState) {
                this.provisioningState = provisioningState;
                return this;
            }

            public Builder privateIPAddress(final String privateIPAddress) {
                this.privateIPAddress = privateIPAddress;
                return this;
            }

            public Builder privateIPAllocationMethod(final String privateIPAllocationMethod) {
                this.privateIPAllocationMethod = privateIPAllocationMethod;
                return this;
            }

            public Builder subnet(final IdReference subnet) {
                this.subnet = subnet;
                return this;
            }

            public Builder publicIPAddress(final IdReference publicIPAddress) {
                this.publicIPAddress = publicIPAddress;
                return this;
            }

            public IpConfigurationProperties build() {
                return IpConfigurationProperties.create(provisioningState, privateIPAddress, privateIPAllocationMethod, subnet, publicIPAddress);
            }

            public Builder fromIpConfigurationProperties(final IpConfigurationProperties ipConfigurationProperties) {
                return provisioningState(ipConfigurationProperties.provisioningState()).
                        privateIPAddress(ipConfigurationProperties.privateIPAddress()).
                        privateIPAllocationMethod(ipConfigurationProperties.privateIPAllocationMethod()).
                        subnet(ipConfigurationProperties.subnet()).
                        publicIPAddress(ipConfigurationProperties.publicIPAddress());
            }
        }
    }
    public IpConfiguration() {
    } // For AutoValue only!

    @Nullable
    public abstract String name();

    @Nullable
    public abstract String id();

    @Nullable
    public abstract String etag();

    @Nullable
    public abstract Boolean primary();

    @Nullable
    public abstract IpConfigurationProperties properties();

    @SerializedNames({"name", "id", "etag", "primary", "properties"})
    public static IpConfiguration create(final String name, final String id, final String etag, final Boolean primary, final IpConfigurationProperties properties) {
        return new AutoValue_IpConfiguration(name, id, etag, primary, properties);
    }

    public Builder toBuilder() {
        return builder().fromIpConfiguration(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;

        private String id;

        private String etag;

        private Boolean primary;

        private IpConfigurationProperties properties;

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

        public Builder primary(final Boolean primary) {
            this.primary = primary;
            return this;
        }

        public Builder properties(final IpConfigurationProperties properties) {
            this.properties = properties;
            return this;
        }

        public IpConfiguration build() {
            return IpConfiguration.create(name, id, etag, primary, properties);
        }

        public Builder fromIpConfiguration(final IpConfiguration ipConfiguration) {
            return name(ipConfiguration.name()).
                    id(ipConfiguration.id()).
                    etag(ipConfiguration.etag()).
                    primary(ipConfiguration.primary()).
                    properties(ipConfiguration.properties());
        }
    }
}
