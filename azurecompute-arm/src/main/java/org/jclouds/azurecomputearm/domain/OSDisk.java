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
public abstract class OSDisk {

    OSDisk() {
    } // For AutoValue only!


    /**
     * The OS type of the os disk
     */
    @Nullable
    public abstract String osType();

    /**
     * The name of the os disk
     */
    @Nullable
    public abstract String name();

    /**
     * The vhd of the os disk
     */
    @Nullable
    public abstract VHD vhd();

    /**
     * The caching mode of the os disk
     */
    @Nullable
    public abstract String caching();

    /**
     * The create options of the os disk
     */
    @Nullable
    public abstract String createOption();

    @SerializedNames({"osType", "name", "vhd", "caching", "createOption"})
    public static OSDisk create(final String osType, final String name, final VHD vhd,
                                final String caching, final String createOption) {

        return new AutoValue_OSDisk(osType, name, vhd, caching, createOption);
    }
}