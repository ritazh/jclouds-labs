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
package org.jclouds.azurecompute.arm.compute.options;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

/**
 * Contains options supported by the {@link org.jclouds.compute.ComputeService#createNodesInGroup(
 * String, int, org.jclouds.compute.options.TemplateOptions)} operation.
 *
 * <h2>Usage</h2> The recommended way to instantiate a {@link AzureComputeArmTemplateOptions} object is to statically
 * import {@code AzureComputeArmTemplateOptions.*} and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p>
 *
 * <pre>
 * import static org.jclouds.compute.options.AzureComputeArmTemplateOptions.Builder.*;
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set&lt;? extends NodeMetadata&gt; set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 * </pre>
 *
 */
public class AzureComputeArmTemplateOptions extends TemplateOptions implements Cloneable {

   protected String virtualNetworkName;
   protected List<String> subnetNames = ImmutableList.of();
   protected String storageAccountName;
   protected String storageAccountType;
   protected String networkSecurityGroupName;
   protected String reservedIPName;

   @Override
   public AzureComputeArmTemplateOptions clone() {
      final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(final TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof AzureComputeArmTemplateOptions) {
         final AzureComputeArmTemplateOptions eTo = AzureComputeArmTemplateOptions.class.cast(to);
         eTo.virtualNetworkName(virtualNetworkName);
         if (!subnetNames.isEmpty()) {
            eTo.subnetNames(subnetNames);
         }
         eTo.storageAccountName(storageAccountName);
         eTo.storageAccountType(storageAccountType);
         eTo.reservedIPName(reservedIPName);
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AzureComputeArmTemplateOptions)) return false;
      if (!super.equals(o)) return false;

      AzureComputeArmTemplateOptions that = (AzureComputeArmTemplateOptions) o;

      if (networkSecurityGroupName != null ? !networkSecurityGroupName.equals(that.networkSecurityGroupName) : that.networkSecurityGroupName != null)
         return false;
      if (reservedIPName != null ? !reservedIPName.equals(that.reservedIPName) : that.reservedIPName != null) return false;
      if (storageAccountName != null ? !storageAccountName.equals(that.storageAccountName) : that.storageAccountName != null) return false;
      if (storageAccountType != null ? !storageAccountType.equals(that.storageAccountType) : that.storageAccountType != null) return false;
      if (subnetNames != null ? !subnetNames.equals(that.subnetNames) : that.subnetNames != null) return false;
      if (virtualNetworkName != null ? !virtualNetworkName.equals(that.virtualNetworkName) : that.virtualNetworkName != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (virtualNetworkName != null ? virtualNetworkName.hashCode() : 0);
      result = 31 * result + (subnetNames != null ? subnetNames.hashCode() : 0);
      result = 31 * result + (storageAccountName != null ? storageAccountName.hashCode() : 0);
      result = 31 * result + (storageAccountType != null ? storageAccountType.hashCode() : 0);
      result = 31 * result + (networkSecurityGroupName != null ? networkSecurityGroupName.hashCode() : 0);
      result = 31 * result + (reservedIPName != null ? reservedIPName.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this)
              .add("virtualNetworkName", virtualNetworkName)
              .add("subnetNames", subnetNames)
              .add("storageAccountName", storageAccountName)
              .add("storageAccountType", storageAccountType)
              .add("networkSecurityGroupName", networkSecurityGroupName)
              .add("reservedIPName", reservedIPName)
              .toString();
   }

   public AzureComputeArmTemplateOptions virtualNetworkName(@Nullable String virtualNetworkName) {
      this.virtualNetworkName = virtualNetworkName;
      return this;
   }

   public AzureComputeArmTemplateOptions subnetNames(Iterable<String> subnetNames) {
      this.subnetNames = ImmutableList.copyOf(checkNotNull(subnetNames, "subnetNames"));
      return this;
   }

   public AzureComputeArmTemplateOptions subnetNames(String...subnetNames) {
      return subnetNames(ImmutableList.copyOf(checkNotNull(subnetNames, "subnetNames")));
   }


   public AzureComputeArmTemplateOptions networkSecurityGroupName(@Nullable String networkSecurityGroupName) {
      this.networkSecurityGroupName = networkSecurityGroupName;
      return this;
   }

   public AzureComputeArmTemplateOptions storageAccountName(@Nullable String storageAccountName) {
      this.storageAccountName = storageAccountName;
      return this;
   }

   public AzureComputeArmTemplateOptions storageAccountType(@Nullable String storageAccountType) {
      this.storageAccountType = storageAccountType;
      return this;
   }

   public AzureComputeArmTemplateOptions reservedIPName(@Nullable String reservedIPName) {
      this.reservedIPName = reservedIPName;
      return this;
   }

   public String getVirtualNetworkName() {
      return virtualNetworkName;
   }

   public List<String> getSubnetNames() {
      return subnetNames;
   }

   public String getStorageAccountName() {
      return storageAccountName;
   }

   public String getStorageAccountType() {
      return storageAccountType;
   }

   public String getNetworkSecurityGroupName() {
      return networkSecurityGroupName;
   }

   public String getReservedIPName() {
      return reservedIPName;
   }

   public static class Builder {

      /**
       * @see #virtualNetworkName
       */
      public static AzureComputeArmTemplateOptions virtualNetworkName(final String virtualNetworkName) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.virtualNetworkName(virtualNetworkName);
      }

      /**
       * @see #subnetNames
       */
      public static AzureComputeArmTemplateOptions subnetNames(String...subnetNames) {
         AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.subnetNames(subnetNames);
      }

      /**
       * @see #subnetNames
       */
      public static AzureComputeArmTemplateOptions subnetNames(Iterable<String> subnetNames) {
         AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.subnetNames(subnetNames);
      }

      /**
       * @see #storageAccountName
       */
      public static AzureComputeArmTemplateOptions storageAccountName(final String storageAccountName) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.storageAccountName(storageAccountName);
      }

      /**
       * @see #storageAccountType
       */
      public static AzureComputeArmTemplateOptions storageAccountType(final String storageAccountType) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.storageAccountType(storageAccountType);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#inboundPorts(int...)
       */
      public static AzureComputeArmTemplateOptions inboundPorts(final int... ports) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.inboundPorts(ports);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#blockOnPort(int, int)
       */
      public static AzureComputeArmTemplateOptions blockOnPort(final int port, final int seconds) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.blockOnPort(port, seconds);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(java.util.Map)
       */
      public static AzureComputeArmTemplateOptions userMetadata(final Map<String, String> userMetadata) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.userMetadata(userMetadata);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#userMetadata(String, String)
       */
      public static AzureComputeArmTemplateOptions userMetadata(final String key, final String value) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.userMetadata(key, value);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#nodeNames(Iterable)
       */
      public static AzureComputeArmTemplateOptions nodeNames(final Iterable<String> nodeNames) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.nodeNames(nodeNames);
      }

      /**
       * @see org.jclouds.compute.options.TemplateOptions#networks(Iterable)
       */
      public static AzureComputeArmTemplateOptions networks(final Iterable<String> networks) {
         final AzureComputeArmTemplateOptions options = new AzureComputeArmTemplateOptions();
         return options.networks(networks);
      }
   }

   // methods that only facilitate returning the correct object type

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions blockOnPort(int port, int seconds) {
      return AzureComputeArmTemplateOptions.class.cast(super.blockOnPort(port, seconds));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions inboundPorts(int... ports) {
      return AzureComputeArmTemplateOptions.class.cast(super.inboundPorts(ports));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions authorizePublicKey(String publicKey) {
      return AzureComputeArmTemplateOptions.class.cast(super.authorizePublicKey(publicKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions installPrivateKey(String privateKey) {
      return AzureComputeArmTemplateOptions.class.cast(super.installPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions blockUntilRunning(boolean blockUntilRunning) {
      return AzureComputeArmTemplateOptions.class.cast(super.blockUntilRunning(blockUntilRunning));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions dontAuthorizePublicKey() {
      return AzureComputeArmTemplateOptions.class.cast(super.dontAuthorizePublicKey());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions nameTask(String name) {
      return AzureComputeArmTemplateOptions.class.cast(super.nameTask(name));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions runAsRoot(boolean runAsRoot) {
      return AzureComputeArmTemplateOptions.class.cast(super.runAsRoot(runAsRoot));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions runScript(Statement script) {
      return AzureComputeArmTemplateOptions.class.cast(super.runScript(script));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions overrideLoginCredentials(LoginCredentials overridingCredentials) {
      return AzureComputeArmTemplateOptions.class.cast(super.overrideLoginCredentials(overridingCredentials));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions overrideLoginPassword(String password) {
      return AzureComputeArmTemplateOptions.class.cast(super.overrideLoginPassword(password));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions overrideLoginPrivateKey(String privateKey) {
      return AzureComputeArmTemplateOptions.class.cast(super.overrideLoginPrivateKey(privateKey));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions overrideLoginUser(String loginUser) {
      return AzureComputeArmTemplateOptions.class.cast(super.overrideLoginUser(loginUser));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions overrideAuthenticateSudo(boolean authenticateSudo) {
      return AzureComputeArmTemplateOptions.class.cast(super.overrideAuthenticateSudo(authenticateSudo));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions userMetadata(Map<String, String> userMetadata) {
      return AzureComputeArmTemplateOptions.class.cast(super.userMetadata(userMetadata));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions userMetadata(String key, String value) {
      return AzureComputeArmTemplateOptions.class.cast(super.userMetadata(key, value));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions nodeNames(Iterable<String> nodeNames) {
      return AzureComputeArmTemplateOptions.class.cast(super.nodeNames(nodeNames));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AzureComputeArmTemplateOptions networks(Iterable<String> networks) {
      return AzureComputeArmTemplateOptions.class.cast(super.networks(networks));
   }

}

