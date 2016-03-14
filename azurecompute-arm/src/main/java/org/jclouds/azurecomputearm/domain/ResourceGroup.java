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
import java.util.HashMap;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;


@AutoValue
public abstract class ResourceGroup {

   @AutoValue
   public abstract static class ResourceGroupProperties{

      public ResourceGroupProperties() {
      }// For AutoValue only!

      @Nullable
      public abstract String provisioningState();

      @SerializedNames({"provisioningState"})
      public static ResourceGroupProperties create(final String provisioningState) {
         return new AutoValue_ResourceGroup_ResourceGroupProperties(provisioningState);
      }

      public Builder toBuilder() {
         return builder().fromResourceGroupProperties(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private String provisioningState;

         public Builder provisioningState(final String provisioningState) {
            this.provisioningState = provisioningState;
            return this;
         }

         public ResourceGroupProperties build() {
            return ResourceGroupProperties.create(provisioningState);
         }

         public Builder fromResourceGroupProperties(final ResourceGroupProperties resourceGroupProperties) {
            return provisioningState(resourceGroupProperties.provisioningState());
         }
      }
   }

   public ResourceGroup() {}

   public abstract String id();
   public abstract String name();
   public abstract String location();
   @Nullable
   public abstract HashMap<String, String> tags();
   public abstract ResourceGroupProperties properties();

   @SerializedNames({"id", "name", "location", "tags", "properties"})
   public static ResourceGroup create(String id, String name, String location, HashMap<String, String> tags, ResourceGroupProperties properties) {
      return new AutoValue_ResourceGroup(id, name, location, tags, properties);
   }

   public Builder toBuilder() {
      return builder().fromResourceGroup(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String id;

      private String name;

      private String location;

      private HashMap<String, String> tags;

      private ResourceGroupProperties resourceGroupProperties;

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder id(final String id) {
         this.id = id;
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

      public Builder resourceGroupProperties(final ResourceGroupProperties resourceGroupProperties) {
         this.resourceGroupProperties = resourceGroupProperties;
         return this;
      }

      public ResourceGroup build() {
         return ResourceGroup.create(name, id, location, tags, resourceGroupProperties);
      }

      public Builder fromResourceGroup(final ResourceGroup resourceGroup) {
         return name(resourceGroup.name()).
                 id(resourceGroup.id()).
                 location(resourceGroup.location()).
                 tags(resourceGroup.tags()).
                 resourceGroupProperties(resourceGroup.properties());
      }
   }
}
