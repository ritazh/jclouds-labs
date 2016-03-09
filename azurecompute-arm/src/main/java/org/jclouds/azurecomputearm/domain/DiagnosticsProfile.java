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
public abstract class DiagnosticsProfile {

   DiagnosticsProfile() {
   } // For AutoValue only!

   @AutoValue
   public abstract static class BootDiagnostics {

      BootDiagnostics() {
      } // For AutoValue only!

      public abstract boolean enabled();

      @Nullable
      public abstract String storageUri();

      @SerializedNames({"enabled", "storageUri"})
      public static BootDiagnostics create(final boolean enabled,
                                           final String storageUri) {

         return new AutoValue_DiagnosticsProfile_BootDiagnostics(enabled, storageUri);
      }
   }

   public abstract BootDiagnostics bootDiagnostics();

   @SerializedNames({"bootDiagnostics"})
   public static DiagnosticsProfile create(final BootDiagnostics bootDiagnostics) {
      return new AutoValue_DiagnosticsProfile(bootDiagnostics);
   }
}
