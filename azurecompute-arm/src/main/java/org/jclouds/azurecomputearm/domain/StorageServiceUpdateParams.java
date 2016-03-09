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

import java.util.Date;
import java.util.Map;

@AutoValue
public abstract class StorageServiceUpdateParams {

   public enum AccountType {

      Standard_LRS,
      Standard_ZRS,
      Standard_GRS,
      Standard_RAGRS,
      Premium_LRS,
      UNRECOGNIZED;

      public static AccountType fromString(final String text) {
         if (text != null) {
            for (AccountType type : AccountType.values()) {
               if (text.equalsIgnoreCase(type.name())) {
                  return type;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   public enum RegionStatus {

       Available,
       Unavailable,
       available,
       unavailable,
       UNRECOGNIZED;

      public static RegionStatus fromString(final String text) {
         if (text != null) {
            for (RegionStatus status : RegionStatus.values()) {
               if (text.equalsIgnoreCase(status.name())) {
                  return status;
               }
            }
         }
         return UNRECOGNIZED;
      }

   }

   public enum Status {

       Creating,
       Created,
       Deleting,
       Deleted,
       Changing,
       ResolvingDns,
       Succeeded,
       UNRECOGNIZED;

      public static Status fromString(final String text) {
         if (text != null) {
            for (Status status : Status.values()) {
               if (text.equalsIgnoreCase(status.name())) {
                  return status;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   @Nullable
   @AutoValue
   public abstract static class StorageServiceUpdateProperties {

       StorageServiceUpdateProperties() {
      } // For AutoValue only!

       /**
        * Specifies whether the account supports locally-redundant storage, geo-redundant storage, zone-redundant
        * storage, or read access geo-redundant storage.
        */
       @Nullable
       public abstract AccountType accountType();

       /**
        * Specifies the time that the storage account was created.
        */
       @Nullable
       public abstract Date creationTime();

       /**
        * Specifies the endpoints of the storage account.
        */
       @Nullable
       public abstract Map<String, String> primaryEndpoints();

       /**
       * A primaryLocation for the storage account.
       */
      @Nullable
      public abstract String primaryLocation();

      /**
       * provisioningState for the storage group
       */
      @Nullable
      public abstract Status provisioningState();

       /**
        * Specifies the secondary endpoints of the storage account.
        */
       @Nullable
       public abstract Map<String, String> secondaryEndpoints();

      /**
       * Secondary location for the storage group
       */
      @Nullable
      public abstract String secondaryLocation();

      /**
       * The status of primary endpoints
       */
      @Nullable
      public abstract RegionStatus statusOfPrimary();

      /**
       * The secondary status of the storage account.
       */
      @Nullable
      public abstract RegionStatus statusOfSecondary();


      @SerializedNames({"accountType", "creationTime", "primaryEndpoints", "primaryLocation",
              "provisioningState", "secondaryEndpoints", "secondaryLocation", "statusOfPrimary", "statusOfSecondary"})
      public static StorageServiceUpdateProperties create(final AccountType accountType, final Date creationTime,
              final Map<String, String> primaryEndpoints, final String primaryLocation, final Status provisioningState,
              final Map<String, String> secondaryEndpoints, final String secondaryLocation,
              final RegionStatus statusOfPrimary, final RegionStatus statusOfSecondary) {

         return new AutoValue_StorageServiceUpdateParams_StorageServiceUpdateProperties(accountType, creationTime,
                 primaryEndpoints, primaryLocation, provisioningState,
                 secondaryEndpoints, secondaryLocation, statusOfPrimary, statusOfSecondary);
      }
   }

   StorageServiceUpdateParams() {
   } // For AutoValue only!

   /**
    * Specifies the tags of the storage account.
    */
   @Nullable
   public abstract Map<String, String> tags();

   /**
    * Specifies the properties of the storage account.
    */
   public abstract StorageServiceUpdateProperties storageServiceProperties();


   @SerializedNames({"tags", "properties"})
   public static StorageServiceUpdateParams create(final Map<String, String> tags,
                                                   final StorageServiceUpdateProperties storageServiceProperties) {
      return new AutoValue_StorageServiceUpdateParams(tags, storageServiceProperties);
   }
}
