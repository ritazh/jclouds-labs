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

@AutoValue
public abstract class PublicIPAddress {

    @AutoValue
    public abstract static class DnsSettings{

        public DnsSettings(){
        }// For AutoValue only!

        public abstract String domainNameLabel();

        public abstract String fqdn();

        @Nullable
        public abstract String reverseFqdn();

        @SerializedNames({"domainNameLabel", "fqdn", "reverseFqdn"})
        public static DnsSettings create(final String domainNameLabel, final String fqdn, final String reverseFqdn){
            return new AutoValue_PublicIPAddress_DnsSettings(domainNameLabel, fqdn, reverseFqdn);
        }

        public Builder toBuilder() {
            return builder().fromDnsSettings(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private String domainNameLabel;

            private String fqdn;

            private String reverseFqdn;

            public Builder domainNameLabel(final String domainNameLabel) {
                this.domainNameLabel = domainNameLabel;
                return this;
            }

            public Builder fqdn(final String fqdn) {
                this.fqdn = fqdn;
                return this;
            }

            public Builder reverseFqdn(final String reverseFqdn) {
                this.reverseFqdn = reverseFqdn;
                return this;
            }

            public DnsSettings build() {
                return DnsSettings.create(domainNameLabel, fqdn, reverseFqdn);
            }

            public Builder fromDnsSettings(final DnsSettings settings) {
                return domainNameLabel(settings.domainNameLabel()).
                        fqdn(settings.fqdn()).
                        reverseFqdn(settings.reverseFqdn());
            }
        }
    }

    @AutoValue
    public abstract static class PublicIPProperties{

        public PublicIPProperties() {
        }// For AutoValue only!

        @Nullable // needs to be nullable to create the payload for create request
        public abstract String provisioningState();

        @Nullable // only set in succeeded provisioningState for Static IP and for Dynamic when attached to a NIC
        public abstract String ipAddress();

        public abstract String publicIPAllocationMethod();

        public abstract int idleTimeoutInMinutes();

        @Nullable // only if attached to NIC
        public abstract IdReference ipConfiguration();

        @Nullable // only if DNS name is set
        public abstract DnsSettings dnsSettings();

        @SerializedNames({"provisioningState", "ipAddress", "publicIPAllocationMethod", "idleTimeoutInMinutes", "ipConfiguration", "dnsSettings"})
        public static PublicIPProperties create(final String provisioningState,
                                                final String ipAddress,
                                                final String publicIPAllocationMethod,
                                                final int idleTimeoutInMinutes,
                                                final IdReference ipConfiguration ,
                                                final DnsSettings dnsSettings) {

            return new AutoValue_PublicIPAddress_PublicIPProperties(provisioningState,
                                                                    ipAddress,
                                                                    publicIPAllocationMethod,
                                                                    idleTimeoutInMinutes,
                                                                    ipConfiguration,
                                                                    dnsSettings);
        }

        public Builder toBuilder() {
            return builder().fromPublicIPProperties(this);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {

            private String provisioningState;

            private String ipAddress;

            private String publicIPAllocationMethod;

            private int idleTimeoutInMinutes;

            private IdReference ipConfiguration;

            private DnsSettings dnsSettings;

            public Builder provisioningState(final String provisioningState) {
                this.provisioningState = provisioningState;
                return this;
            }

            public Builder ipAddress(final String ipAddress) {
                this.ipAddress = ipAddress;
                return this;
            }

            public Builder publicIPAllocationMethod(final String publicIPAllocationMethod) {
                this.publicIPAllocationMethod = publicIPAllocationMethod;
                return this;
            }
            public Builder idleTimeoutInMinutes(final int idleTimeoutInMinutes) {
                this.idleTimeoutInMinutes = idleTimeoutInMinutes;
                return this;
            }

            public Builder ipConfiguration(final IdReference ipConfiguration) {
                this.ipConfiguration = ipConfiguration;
                return this;
            }

            public Builder dnsSettings(final DnsSettings dnsSettings) {
                this.dnsSettings = dnsSettings;
                return this;
            }

            public PublicIPProperties build() {
                return PublicIPProperties.create(provisioningState,
                                                 ipAddress,
                                                 publicIPAllocationMethod,
                                                 idleTimeoutInMinutes,
                                                 ipConfiguration ,
                                                 dnsSettings);
            }

            public Builder fromPublicIPProperties(final PublicIPProperties properties) {
                return provisioningState(properties.provisioningState()).
                        ipAddress(properties.ipAddress()).
                        publicIPAllocationMethod(properties.publicIPAllocationMethod()).
                        idleTimeoutInMinutes(properties.idleTimeoutInMinutes()).
                        ipConfiguration(properties.ipConfiguration()).
                        dnsSettings(properties.dnsSettings());
            }
        }
    }
    public PublicIPAddress() {
    } // For AutoValue only!

    public abstract String name();

    public abstract String id();

    public abstract String etag();

    public abstract String location();

    public abstract HashMap<String, String> tags();

    public abstract PublicIPProperties properties();

    @SerializedNames({"name", "id", "etag", "location", "tags", "properties"})
    public static PublicIPAddress create(final String name,
                                         final String id,
                                         final String etag,
                                         final String location,
                                         final HashMap<String, String> tags,
                                         final PublicIPProperties properties) {
        return new AutoValue_PublicIPAddress(name, id, etag, location, tags, properties);
    }

    public Builder toBuilder() {
        return builder().fromPublicIPAddress(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String name;

        private String id;

        private String etag;

        private String location;

        private HashMap<String, String> tags;

        private PublicIPProperties properties;

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

        public Builder tags(final HashMap<String, String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder properties(final PublicIPProperties properties) {
            this.properties = properties;
            return this;
        }

        public PublicIPAddress build() {
            return PublicIPAddress.create(name, id, etag, location, tags, properties);
        }

        public Builder fromPublicIPAddress(final PublicIPAddress ipConfiguration) {
            return name(ipConfiguration.name()).
                    id(ipConfiguration.id()).
                    etag(ipConfiguration.etag()).
                    location(ipConfiguration.location()).
                    tags(ipConfiguration.tags()).
                    properties(ipConfiguration.properties());
        }
    }
}
