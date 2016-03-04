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

import java.util.List;
import java.util.Map;

/**
 * A virtual machine  that is valid for your subscription.
 */
@AutoValue
public abstract class VirtualMachineProperties {

   VirtualMachineProperties() {
   } // For AutoValue only!





   /**
    * The id of the virtual machine.
    */
   @Nullable
   public abstract String vmId();

   /**
    * The license type of the virtual machine.
    */
   @Nullable
   public abstract String licenseType();

   /**
    * The availability set  of the virtual machine
    */
   @Nullable
   public abstract AvailabilitySet availabilitySet();

   /**
    * The hardware Profile of the virtual machine .
    */
   @Nullable
   public abstract HardwareProfile hardwareProfile();

   /**
    * The Storage Profile of the virtual machine .
    */
   @Nullable
   public abstract StorageProfile storageProfile();

   /**
    * The OS Profile of the virtual machine .
    */
   @Nullable
   public abstract OSProfile osProfile();

   /**
    * The network profile of the VM
    */
   @Nullable
   public abstract NetworkProfile networkProfile();

   /**
    * The diagnostics profile of the VM
    */
   @Nullable
   public abstract DiagnosticsProfile diagnosticsProfile();

   /**
    * The provisioning state of the VM
    */
   @Nullable
   public abstract String provisioningState();

   @SerializedNames({"vmId","licenseType", "availabilitySet", "hardwareProfile", "storageProfile", "osProfile",
      "networkProfile", "diagnosticsProfile", "provisioningState"})
   public static VirtualMachineProperties create(final String vmId, final String licenseType,
                                                 final AvailabilitySet availabilitySet,
                                                 final HardwareProfile hardwareProfile,
                                                 final StorageProfile storageProfile,
                                                 final OSProfile osProfile,
                                                 final NetworkProfile networkProfile,
                                                 final DiagnosticsProfile diagnosticsProfile,
                                                 final String provisioningState) {

      return new AutoValue_VirtualMachineProperties(vmId, licenseType, availabilitySet, hardwareProfile,
              storageProfile, osProfile, networkProfile, diagnosticsProfile, provisioningState);
   }
}
