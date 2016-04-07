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
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import java.util.Map;

@AutoValue
public abstract class PublicIPAddress {

    @AutoValue
    public abstract static class DnsSettings{

        public abstract String domainNameLabel();

        public abstract String fqdn();

        @Nullable
        public abstract String reverseFqdn();

        @SerializedNames({"domainNameLabel", "fqdn", "reverseFqdn"})
        public static DnsSettings create(final String domainNameLabel, final String fqdn, final String reverseFqdn){
            return new AutoValue_PublicIPAddress_DnsSettings(domainNameLabel, fqdn, reverseFqdn);
        }
    }

    @AutoValue
    public abstract static class PublicIPProperties{

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
    }

    public abstract String name();

    public abstract String id();

    public abstract String etag();

    public abstract String location();

    @Nullable
    public abstract Map<String, String> tags();

    public abstract PublicIPProperties properties();

    @SerializedNames({"name", "id", "etag", "location", "tags", "properties"})
    public static PublicIPAddress create(final String name,
                                         final String id,
                                         final String etag,
                                         final String location,
                                         @Nullable final Map<String, String> tags,
                                         final PublicIPProperties properties) {
        return new AutoValue_PublicIPAddress(name, id, etag, location, tags == null ? ImmutableMap.<String, String>builder().build() : ImmutableMap.copyOf(tags), properties);
    }
}
