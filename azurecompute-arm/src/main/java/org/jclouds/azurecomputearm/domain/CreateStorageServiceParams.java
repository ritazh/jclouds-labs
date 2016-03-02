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
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.jclouds.azurecomputearm.domain.StorageService.AccountType;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class CreateStorageServiceParams {

   CreateStorageServiceParams() {
   } // For AutoValue only!

   /**
    * A location for the storage account
    */
   @Nullable
   public abstract String location();

   /**
    * Represents the name of tags. Each tag property must have both a defined name
    * and value. You can have a maximum of 50 extended property name/value pairs.
    *
    * <p/>
    * The maximum length of the Name element is 64 characters, only alphanumeric characters and underscores are valid in
    * the Name, and the name must start with a letter. Each extended property value has a maximum length of 255
    * characters.
    */
   @Nullable
   public abstract Map<String, String> tags();

   /**
    * Represents the name of an cloud service property. Each property must have both a defined name
    * and value. You can have a maximum of 50 extended property name/value pairs.
    *
    * <p/>
    * The maximum length of the Name element is 64 characters, only alphanumeric characters and underscores are valid in
    * the Name, and the name must start with a letter. Each extended property value has a maximum length of 255
    * characters.
    */
   @Nullable
   public abstract Map<String, String> properties();


   public Builder toBuilder() {
      return builder().fromCreateStorageServiceParams(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String location;

      private Map<String, String> tags;

      private Map<String, String> properties;

      public Builder location(final String location) {
         this.location = location;
         return this;
      }

      public Builder tags(final Map<String, String> tags) {
         this.tags = tags;
         return this;
      }

      public Builder properties(final Map<String, String> properties) {
         this.properties = properties;
         return this;
      }

      public CreateStorageServiceParams build() {
         return CreateStorageServiceParams.create(location, tags, properties);
      }

      public Builder fromCreateStorageServiceParams(final CreateStorageServiceParams storageServiceParams) {
         return location(storageServiceParams.location()).
                  tags(storageServiceParams.tags()).
                  properties(storageServiceParams.properties());
      }
   }

   @SerializedNames({"location","tags","properties"})
   private static CreateStorageServiceParams create(
           final String location, final Map<String, String> tags, final Map<String, String> properties) {

      return new AutoValue_CreateStorageServiceParams(location, tags, properties);
   }
}
