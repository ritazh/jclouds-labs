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

import static com.google.common.base.Objects.equal;
import org.jclouds.compute.options.TemplateOptions;
import com.google.common.base.Objects;

/**
 * Azure ARM custom options
 */
public class AzureTemplateOptions extends TemplateOptions implements Cloneable {


   private String customData;
   private String virtualNetworkAddressPrefix;
   private String subnetAddressPrefix;
   private String DNSLabelPrefix;
   private String keyVaultIdAndSecret;


   /**
    * Sets the CIDR block for virtual network
    */
   public  AzureTemplateOptions customData(String customData) {
      this.customData = customData;
      return this;
   }

   /**
    * Sets the CIDR block for virtual network
    */
   public  AzureTemplateOptions virtualNetworkAddressPrefix(String virtualNetworkAddressPrefix) {
      this.virtualNetworkAddressPrefix = virtualNetworkAddressPrefix;
      return this;
   }

   /**
    * Sets the CIDR block for subnet within virtual network
    */
   public  AzureTemplateOptions subnetAddressPrefix(String subnetAddressPrefix) {
      this.subnetAddressPrefix = subnetAddressPrefix;
      return this;
   }

   /**
    * Sets the DNS label prefix for public IP address. label.location.cloudapp.azure.com
    */
   public  AzureTemplateOptions DNSLabelPrefix(String DNSLabelPrefix) {
      this.DNSLabelPrefix = DNSLabelPrefix;
      return this;
   }

   /**
    * Sets the KeyVault id and secret separated with ":"
    */
   public  AzureTemplateOptions keyVaultIdAndSecret(String keyVaultIdAndSecret) {
      this.keyVaultIdAndSecret = keyVaultIdAndSecret;
      return this;
   }

   public String getCustomData() { return customData; }
   public String getVirtualNetworkAddressPrefix() { return virtualNetworkAddressPrefix; }
   public String getSubnetAddressPrefix() { return subnetAddressPrefix; }
   public String getDNSLabelPrefix() { return DNSLabelPrefix; }
   public String getKeyVaultIdAndSecret() { return keyVaultIdAndSecret; }

   @Override
   public AzureTemplateOptions clone() {
      AzureTemplateOptions options = new AzureTemplateOptions();
      copyTo(options);
      return options;
   }

   @Override
   public void copyTo(TemplateOptions to) {
      super.copyTo(to);
      if (to instanceof AzureTemplateOptions) {
         AzureTemplateOptions eTo = AzureTemplateOptions.class.cast(to);
         eTo.customData(customData);
         eTo.virtualNetworkAddressPrefix(virtualNetworkAddressPrefix);
         eTo.subnetAddressPrefix(subnetAddressPrefix);
         eTo.DNSLabelPrefix(DNSLabelPrefix);
         eTo.keyVaultIdAndSecret(keyVaultIdAndSecret);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), virtualNetworkAddressPrefix, subnetAddressPrefix, DNSLabelPrefix, customData, keyVaultIdAndSecret);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      AzureTemplateOptions other = (AzureTemplateOptions) obj;
      return super.equals(other)
            && equal(this.customData, other.customData)
            && equal(this.virtualNetworkAddressPrefix, other.virtualNetworkAddressPrefix)
            && equal(this.subnetAddressPrefix, other.subnetAddressPrefix)
            && equal(this.DNSLabelPrefix, other.DNSLabelPrefix)
            && equal(this.keyVaultIdAndSecret, other.keyVaultIdAndSecret);
   }

   @Override
   public Objects.ToStringHelper string() {
      Objects.ToStringHelper toString = super.string().omitNullValues();
      toString.add("customData", customData);
      toString.add("virtualNetworkAddressPrefix", virtualNetworkAddressPrefix);
      toString.add("subnetAddressPrefix", subnetAddressPrefix);
      toString.add("DNSLabelPrefix", DNSLabelPrefix);
      toString.add("keyVaultIdAndSecret", keyVaultIdAndSecret);
      return toString;
   }

   public static class Builder {

      /**
       * @see AzureTemplateOptions#customData
       */
      public static AzureTemplateOptions customData(String customData) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.customData(customData);
      }

      /**
       * @see AzureTemplateOptions#virtualNetworkAddressPrefix
       */
      public static AzureTemplateOptions virtualNetworkAddressPrefix(String virtualNetworkAddressPrefix) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.virtualNetworkAddressPrefix(virtualNetworkAddressPrefix);
      }

      /**
       * @see AzureTemplateOptions#subnetAddressPrefix
       */
      public static AzureTemplateOptions subnetAddressPrefix(String subnetAddressPrefix) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.subnetAddressPrefix(subnetAddressPrefix);
      }

      /**
       * @see AzureTemplateOptions#DNSLabelPrefix
       */
      public static AzureTemplateOptions DNSLabelPrefix(String DNSLabelPrefix) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.DNSLabelPrefix(DNSLabelPrefix);
      }

      /**
       * @see AzureTemplateOptions#keyVaultIdAndSecret
       */
      public static AzureTemplateOptions keyVaultIdAndSecret(String keyVaultIdAndSecret) {
         AzureTemplateOptions options = new AzureTemplateOptions();
         return options.keyVaultIdAndSecret(keyVaultIdAndSecret);
      }
   }
}
