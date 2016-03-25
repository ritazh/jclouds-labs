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
package org.jclouds.azurecomputearm.oauth.v2.config;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Provides;
import org.jclouds.azurecomputearm.oauth.v2.AzureAuthorizationApi;
import org.jclouds.azurecomputearm.oauth.v2.filters.ClientCredentialsSecretFlow;
import org.jclouds.oauth.v2.filters.BearerTokenFromCredentials;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.oauth.v2.config.Authorization;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.URI;

import static org.jclouds.azurecomputearm.oauth.v2.config.AzureCredentialType.CLIENT_CREDENTIALS_SECRET;
import static org.jclouds.azurecomputearm.oauth.v2.config.AzureCredentialType.BEARER_TOKEN_CREDENTIALS;
import static org.jclouds.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

public class AzureOAuthModule extends AbstractModule {
    @Override protected void configure() {
        bindHttpApi(binder(), AzureAuthorizationApi.class);
        bind(AzureCredentialType.class).toProvider(CredentialTypeFromPropertyOrDefault.class);
    }

    @Provides
    @Authorization
    protected Supplier<URI> oauthEndpoint(@javax.inject.Named("oauth.endpoint") String endpoint) {
        return Suppliers.ofInstance(URI.create(endpoint));
    }

    @Singleton
    public static class CredentialTypeFromPropertyOrDefault implements Provider<AzureCredentialType> {
        @Inject(optional = true)
        @Named(CREDENTIAL_TYPE)
        String credentialType = CLIENT_CREDENTIALS_SECRET.toString();

        @Override
        public AzureCredentialType get() {
            return AzureCredentialType.fromValue(credentialType);
        }
    }

    @Provides
    @Singleton
    protected OAuthFilter authenticationFilterForCredentialType(AzureCredentialType credentialType,
                                                                ClientCredentialsSecretFlow clientSecretAuth,
                                                                BearerTokenFromCredentials bearerAuth) {
        switch (credentialType) {
            case CLIENT_CREDENTIALS_SECRET:
                return clientSecretAuth;
            case BEARER_TOKEN_CREDENTIALS:
                return bearerAuth;
            default:
                throw new IllegalArgumentException("Unsupported credential type: " + credentialType);
        }
    }
}
