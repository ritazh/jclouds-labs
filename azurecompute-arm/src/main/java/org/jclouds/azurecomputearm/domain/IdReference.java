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

// Simple helper class to serialize / deserialize id reference.

@AutoValue
public abstract class IdReference {

    public IdReference() {
    } // For AutoValue only!

    @Nullable
    public abstract String id();

    @SerializedNames({"id"})
    public static IdReference create(final String id) {
        return new AutoValue_IdReference(id);
    }

    public Builder toBuilder() {
        return builder().fromIdReference(this);
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
        public IdReference build() {
            return IdReference.create(id);
        }

        public Builder fromIdReference(final IdReference idReference) {
            return id(idReference.id());
        }
    }
}
