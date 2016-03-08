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
import org.jclouds.json.SerializedNames;

import java.util.List;

@AutoValue
public abstract class NetworkProfile {

    NetworkProfile() {
    } // For AutoValue only!

    @AutoValue
    public abstract static class NetworkInterfaceId {

        NetworkInterfaceId() {
        } // For AutoValue only!

        /**
         * id of network interface
         */
        public abstract String id();

        @SerializedNames({"id"})
        public static NetworkInterfaceId create(final String id) {

            return new AutoValue_NetworkProfile_NetworkInterfaceId(
                    id);
        }
    }

    /**
     * List of network interfaces
     */
    public abstract List<NetworkInterfaceId> networkInterfaces();

    @SerializedNames({"networkInterfaces"})
    public static NetworkProfile create(final List<NetworkInterfaceId> networkInterfaces) {

        return new AutoValue_NetworkProfile(networkInterfaces);
    }
}