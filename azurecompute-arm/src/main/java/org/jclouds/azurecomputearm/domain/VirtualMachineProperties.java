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

   @AutoValue
   public abstract static class HardwareProfile {

      HardwareProfile() {
      } // For AutoValue only!

      /**
       * The vm size of the virtual machine.
       */
      public abstract String vmSize();

      @SerializedNames({"vmSize"})
      public static HardwareProfile create(final String vmSize) {

         return new AutoValue_VirtualMachineProperties_HardwareProfile(vmSize);
      }
   }

   @AutoValue
   public abstract static class ImageReference {

      ImageReference() {
      } // For AutoValue only!

      /**
       * The publisher of the image reference.
       */
      @Nullable
      public abstract String publisher();

      /**
       * The offer of the image reference.
       */
      @Nullable
      public abstract String offer();

      /**
       * The sku of the image reference.
       */
      @Nullable
      public abstract String sku();

      /**
       * The version of the image reference.
       */
      @Nullable
      public abstract String version();

      @SerializedNames({"publisher", "offer", "sku", "version"})
      public static ImageReference create(final String publisher,
                                          final String offer,
                                          final String sku,
                                          final String version) {

         return new AutoValue_VirtualMachineProperties_ImageReference(publisher,
                 offer, sku, version);
      }
   }

   @AutoValue
   public abstract static class VHD {

      VHD() {
      } // For AutoValue only!

      /**
       * The uri of the vhd.
       */
      public abstract String uri();

      @SerializedNames({"uri"})
      public static VHD create(final String uri) {

         return new AutoValue_VirtualMachineProperties_VHD(uri);
      }
   }

   @AutoValue
   public abstract static class DataDisk {

      DataDisk() {
      } // For AutoValue only!

      public abstract String name();

      public abstract String diskSizeGB();

      public abstract int lun();

      public abstract VHD vhd();

      public abstract String createOption();

      @SerializedNames({"name", "diskSizeGB", "lun", "vhd", "createOption"})
      public static DataDisk create(final String name, final String diskSizeGB, final int lun,
                                    final VHD vhd, final String createOption) {

         return new AutoValue_VirtualMachineProperties_DataDisk(name, diskSizeGB, lun, vhd, createOption);
      }
   }

   @AutoValue
   public abstract static class OSDisk {

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

         return new AutoValue_VirtualMachineProperties_OSDisk(osType, name, vhd, caching, createOption);
      }
   }

   @AutoValue
   public abstract static class StorageProfile {

      StorageProfile() {
      } // For AutoValue only!

      /**
       * The image reference of the storage profile
       */
      public abstract ImageReference imageReference();

      /**
       * The image reference of the storage profile
       */
      public abstract OSDisk osDisk();

      /**
       * The list of the data disks of the storage profile
       */
      @Nullable
      public abstract List<DataDisk> dataDisks();

      @SerializedNames({"imageReference","osDisk", "dataDisks"})
      public static StorageProfile create(final ImageReference imageReference,
                                          final OSDisk osDisk, final List<DataDisk> dataDisks) {

         return new AutoValue_VirtualMachineProperties_StorageProfile(imageReference, osDisk, dataDisks);
      }
   }

   @AutoValue
   public abstract static class OSProfile {

      OSProfile() {
      } // For AutoValue only!

      @AutoValue
      public abstract static class LinuxConfiguration {

         LinuxConfiguration() {
         } // For AutoValue only!

         @AutoValue
         public abstract static class SSH {

            SSH() {
            } // For AutoValue only!

            @AutoValue
            public abstract static class SSHPublicKey {

               SSHPublicKey() {
               } // For AutoValue only!

               @Nullable
               public abstract String path();
               @Nullable
               public abstract String keyData();

               @SerializedNames({"path","keyData"})
               public static SSHPublicKey create(final String path, final String keyData) {

                  return new AutoValue_VirtualMachineProperties_OSProfile_LinuxConfiguration_SSH_SSHPublicKey(
                          path, keyData);
               }
            }
            /**
             * The list of public keys and paths
             */
            @Nullable
            public abstract List<SSHPublicKey> publicKeys();

            @SerializedNames({"publicKeys"})
            public static SSH create(final List<SSHPublicKey> publicKeys) {

               return new AutoValue_VirtualMachineProperties_OSProfile_LinuxConfiguration_SSH(
                       publicKeys);
            }
         }
         /**
          * The authentication method password or ssh
          */
         public abstract String disablePasswordAuthentication();

         /**
          * ssh keys
          */
         @Nullable
         public abstract SSH ssh();

         @SerializedNames({"disablePasswordAuthentication", "ssh"})
         public static LinuxConfiguration create(final String disablePasswordAuthentication,
                                                 final SSH ssh) {

            return new AutoValue_VirtualMachineProperties_OSProfile_LinuxConfiguration(disablePasswordAuthentication,
                    ssh);
         }
      }

      @AutoValue
      public abstract static class WindowsConfiguration {

         WindowsConfiguration() {
         } // For AutoValue only!

         @AutoValue
         public abstract static class WinRM {

            WinRM() {
            } // For AutoValue only!

            /**
             * Map of different settings
             */
            public abstract Map<String,String> listeners();

            @SerializedNames({"listeners"})
            public static WinRM create(final Map<String,String> listeners) {

               return new AutoValue_VirtualMachineProperties_OSProfile_WindowsConfiguration_WinRM(listeners);
            }
         }

         @AutoValue
         public abstract static class AdditionalUnattendContent {

            AdditionalUnattendContent() {
            } // For AutoValue only!

            public abstract String pass();
            public abstract String component();
            public abstract String settingName();
            public abstract String content();

            @SerializedNames({"pass", "component", "settingName", "content"})
            public static AdditionalUnattendContent create(final String pass,final String component,
                                                           final String settingName,
                                                           final String content) {

               return new AutoValue_VirtualMachineProperties_OSProfile_WindowsConfiguration_AdditionalUnattendContent(
                       pass,component,settingName,content);
            }
         }

         /**
          * The provision VM Agent true of false.
          */
         @Nullable
         public abstract boolean provisionVMAgent();

         /**
          * winRM
          */
         @Nullable
         public abstract WinRM winRM();

         /**
          * unattend content
          */
         @Nullable
         public abstract AdditionalUnattendContent additionalUnattendContent();

         /**
          * is automatic updates enabled
          */
         @Nullable
         public abstract boolean enableAutomaticUpdates();

         /**
          * list of certificates
          */
         @Nullable
         public abstract List<String> secrets();

         @SerializedNames({"provisionVMAgent", "winRM", "additionalUnattendContent", "enableAutomaticUpdates",
                 "secrets"})
         public static WindowsConfiguration create(final boolean provisionVMAgent, final WinRM winRM,
                                                   final AdditionalUnattendContent additionalUnattendContent,
                                                   final boolean enableAutomaticUpdates, final List<String> secrets) {

            return new AutoValue_VirtualMachineProperties_OSProfile_WindowsConfiguration(provisionVMAgent, winRM,
                    additionalUnattendContent, enableAutomaticUpdates, secrets);
         }
      }

      /**
       * The computer name of the VM
       */
      @Nullable
      public abstract String computerName();

      /**
       * The admin username of the VM
       */
      @Nullable
      public abstract String adminUsername();

      /**
       * The admin password of the VM
       */
      @Nullable
      public abstract String adminPassword();

      /**
       * The custom data of the VM
       */
      @Nullable
      public abstract String customData();

      /**
       * The linux configuration of the VM
       */
      @Nullable
      public abstract LinuxConfiguration linuxConfiguration();

      /**
       * The windows configuration of the VM
       */
      @Nullable
      public abstract WindowsConfiguration windowsConfiguration();


      @SerializedNames({"computerName", "adminUsername", "adminPassword", "customData", "linuxConfiguration",
              "windowsConfiguration"})
      public static OSProfile create(final String computerName, final String adminUsername, final String adminPassword,
                                     final String customData, final LinuxConfiguration linuxConfiguration,
                                     final WindowsConfiguration windowsConfiguration) {

         return new AutoValue_VirtualMachineProperties_OSProfile(computerName, adminUsername, adminPassword, customData,
                 linuxConfiguration, windowsConfiguration);
      }
   }

   @AutoValue
   public abstract static class NetworkProfile {

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

            return new AutoValue_VirtualMachineProperties_NetworkProfile_NetworkInterfaceId(
                    id);
         }
      }

      /**
       * List of network interfaces
       */
      public abstract List<NetworkInterfaceId> networkInterfaces();

      @SerializedNames({"networkInterfaces"})
      public static NetworkProfile create(final List<NetworkInterfaceId> networkInterfaces) {

         return new AutoValue_VirtualMachineProperties_NetworkProfile(networkInterfaces);
      }
   }

   @AutoValue
   public abstract static class DiagnosticsProfile {

      DiagnosticsProfile() {
      } // For AutoValue only!

      @AutoValue
      public abstract static class BootDiagnostics {

         BootDiagnostics() {
         } // For AutoValue only!

         @Nullable
         public abstract boolean enabled();

         @Nullable
         public abstract String storageUri();

         @SerializedNames({"enabled", "storageUri"})
         public static BootDiagnostics create(final boolean enabled,
                                             final String storageUri) {

            return new AutoValue_VirtualMachineProperties_DiagnosticsProfile_BootDiagnostics(enabled,storageUri);
         }
      }

      public abstract BootDiagnostics bootDiagnostics();

      @SerializedNames({"bootDiagnostics"})
      public static DiagnosticsProfile create(final BootDiagnostics bootDiagnostics) {

         return new AutoValue_VirtualMachineProperties_DiagnosticsProfile(bootDiagnostics);
      }
   }

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
