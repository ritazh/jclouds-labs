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
package org.jclouds.azurecomputearm.oauth.v2;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.providers.AnonymousProviderMetadata.forApiOnEndpoint;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.azurecomputearm.oauth.v2.config.AzureOAuthModule;
import org.jclouds.azurecomputearm.oauth.v2.config.AzureOAuthProperties;
import org.jclouds.oauth.v2.config.OAuthScopes;
import org.jclouds.oauth.v2.config.OAuthScopes.SingleScope;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.config.OAuthProperties;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

@Test(groups = "live", singleThreaded = true)
public class AuthorizationApiLiveTest extends BaseApiLiveTest<AzureAuthorizationApi> {

   private String scope;
   private String resource;
   private String audience; // XXX -- to shut up the compilers

   public AuthorizationApiLiveTest() {
      provider = "oauth";
   }

   public void authenticateCredentialAndPassword() throws Exception {
      Token token = api.authorize_client_secret(identity, credential, resource);

      assertNotNull(token, "no token when authorizing password based credential");
   }

   /** OAuth isn't registered as a provider intentionally, so we fake one. */
   @Override protected ProviderMetadata createProviderMetadata() {
      return forApiOnEndpoint(AzureAuthorizationApi.class, endpoint).toBuilder().id("oauth").build();
   }

   @Override protected Properties setupProperties() {
      Properties props = super.setupProperties();
      resource = checkNotNull(setIfTestSystemPropertyPresent(props, AzureOAuthProperties.RESOURCE), "test.jclouds.oauth.resource");
      audience = checkNotNull(setIfTestSystemPropertyPresent(props, OAuthProperties.AUDIENCE), "test.jclouds.oauth.audience");
      scope = resource;
      return props;
   }

   @Override protected Iterable<Module> setupModules() {
      return ImmutableList.<Module>builder() //
            .add(new AzureOAuthModule()) //
            .add(new Module() {
               @Override public void configure(Binder binder) {
                  // ContextBuilder erases oauth.endpoint, as that's the same name as the provider key.
                  binder.bindConstant().annotatedWith(Names.named("oauth.endpoint")).to(endpoint);
                  binder.bind(OAuthScopes.class).toInstance(SingleScope.create(scope));
               }
            }).addAll(super.setupModules()).build();
   }
}
