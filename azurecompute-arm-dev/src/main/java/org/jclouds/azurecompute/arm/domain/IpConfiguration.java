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
public abstract class IpConfiguration{

    @AutoValue
    public abstract static class IpConfigurationProperties{

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
    }

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
}
