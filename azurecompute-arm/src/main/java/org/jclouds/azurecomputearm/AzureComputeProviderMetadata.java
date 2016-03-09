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
package org.jclouds.azurecomputearm;

import static org.jclouds.azurecomputearm.config.AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD;
import static org.jclouds.azurecomputearm.config.AzureComputeProperties.OPERATION_POLL_MAX_PERIOD;
import static org.jclouds.azurecomputearm.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecomputearm.config.AzureComputeProperties.TCP_RULE_FORMAT;
import static org.jclouds.azurecomputearm.config.AzureComputeProperties.TCP_RULE_REGEXP;


import static org.jclouds.azurecomputearm.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_SECRET;
import static org.jclouds.azurecomputearm.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.azurecomputearm.oauth.v2.config.OAuthProperties.RESOURCE;
import static org.jclouds.azurecomputearm.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.azurecomputearm.oauth.v2.config.OAuthProperties.JWS_ALG;

import java.net.URI;
import java.util.Properties;

import org.jclouds.azurecomputearm.domain.Region;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

import com.google.auto.service.AutoService;

@AutoService(ProviderMetadata.class)
public class AzureComputeProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public AzureComputeProviderMetadata() {
      super(builder());
   }

   public static Properties defaultProperties() {
      final Properties properties = AzureManagementApiMetadata.defaultProperties();
      properties.setProperty(OPERATION_TIMEOUT, "60000");
      properties.setProperty(OPERATION_POLL_INITIAL_PERIOD, "5");
      properties.setProperty(OPERATION_POLL_MAX_PERIOD, "15");
      properties.setProperty(TCP_RULE_FORMAT, "tcp_%s-%s");
      properties.setProperty(TCP_RULE_REGEXP, "tcp_\\d{1,5}-\\d{1,5}");

      properties.put("oauth.endpoint", "https://login.microsoftonline.com/oauth2/token");
      properties.put(JWS_ALG, "RS256");
      properties.put(AUDIENCE, "https://login.microsoftonline.com/oauth2/token");
      properties.put(RESOURCE, "https://management.azure.com");
      properties.put(CREDENTIAL_TYPE, CLIENT_CREDENTIALS_SECRET.toString());

      return properties;
   }

   public AzureComputeProviderMetadata(final Builder builder) {
      super(builder);
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         super();

         id("azurecompute-arm")
                 .name("Azure Resource Management ")
                 .apiMetadata(new AzureManagementApiMetadata())
                 .endpoint("https://management.azure.com/subscriptions/SUBSCRIPTION_ID")
                 .homepage(URI.create("https://www.windowsazure.com/"))
                 .console(URI.create("https://windows.azure.com/default.aspx"))
                 .linkedServices("azureblob", "azurequeue", "azuretable")
                 .iso3166Codes(Region.iso3166Codes())
                 .defaultProperties(AzureComputeProviderMetadata.defaultProperties());
      }

      @Override
      public AzureComputeProviderMetadata build() {
         return new AzureComputeProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(final ProviderMetadata providerMetadata) {
         super.fromProviderMetadata(providerMetadata);
         return this;
      }
   }
}
