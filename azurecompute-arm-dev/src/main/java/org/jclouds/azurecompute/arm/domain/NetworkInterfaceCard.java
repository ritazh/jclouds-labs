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
import java.util.List;
import static com.google.common.collect.ImmutableList.copyOf;

@AutoValue
public abstract class NetworkInterfaceCard {


    @AutoValue
    public abstract static class NetworkInterfaceCardProperties{

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
    }

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
    public abstract Map<String, String> tags();

    @SerializedNames({"name", "id", "etag", "location", "properties", "tags"})
    public static NetworkInterfaceCard create(final String name,
                                              final String id,
                                              final String etag,
                                              final String location,
                                              final NetworkInterfaceCardProperties properties,
                                              @Nullable final Map<String, String> tags) {
        return new AutoValue_NetworkInterfaceCard(name, id, etag, location, properties, tags == null ? ImmutableMap.<String, String>builder().build() : ImmutableMap.copyOf(tags));
    }
}
