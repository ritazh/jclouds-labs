package org.jclouds.azurecomputearm.oauth.v2.filters;

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

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jclouds.azurecomputearm.oauth.v2.AuthorizationApi;
import org.jclouds.azurecomputearm.oauth.v2.config.OAuthScopes;
import org.jclouds.azurecomputearm.oauth.v2.domain.ClientSecret;
import org.jclouds.azurecomputearm.oauth.v2.domain.Token;
import org.jclouds.domain.Credentials;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.location.Provider;

import javax.inject.Inject;
import javax.inject.Named;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.azurecomputearm.oauth.v2.config.OAuthProperties.RESOURCE;

/**
 * Authorizes new Bearer Tokens at runtime by sending up for the http request.
 *
 * <h3>Cache</h3>
 * This maintains a time-based Bearer Token cache. By default expires after 59 minutes
 * (the maximum time a token is valid is 60 minutes).
 * This cache and expiry period is system-wide and does not attend to per-instance expiry time
 * (e.g. "expires_in" from Google Compute -- which is set to the standard 3600 seconds).
 */
public class ClientCredentialsSecretFlow implements OAuthFilter {
    private static final Joiner ON_COMMA = Joiner.on(",");

    private final Supplier<Credentials> credentialsSupplier;
    private final OAuthScopes scopes;
    private final long tokenDuration;
    private final String resource;
    private final LoadingCache<ClientSecret, Token> tokenCache;

    public static class TestClientCredentialsSecretFlow extends ClientCredentialsSecretFlow {

        @Inject
        TestClientCredentialsSecretFlow(AuthorizeToken loader, @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration,
                                        @Named(RESOURCE) String resource,
                               @Provider Supplier<Credentials> credentialsSupplier, OAuthScopes scopes) {
            super(loader, tokenDuration, resource, credentialsSupplier, scopes);
        }

        /** Constant time for testing. */
        long currentTimeSeconds() {
            return 0;
        }
    }

    @Inject ClientCredentialsSecretFlow(AuthorizeToken loader, @Named(PROPERTY_SESSION_INTERVAL) long tokenDuration,
                               @Named(RESOURCE) String resource,
                               @Provider Supplier<Credentials> credentialsSupplier, OAuthScopes scopes) {
        this.credentialsSupplier = credentialsSupplier;
        this.scopes = scopes;
        this.resource = resource;
        this.tokenDuration = tokenDuration;
        // since the session interval is also the token expiration time requested to the server make the token expire a
        // bit before the deadline to make sure there aren't session expiration exceptions
        long cacheExpirationSeconds = tokenDuration > 30 ? tokenDuration - 30 : tokenDuration;
        this.tokenCache = CacheBuilder.newBuilder().expireAfterWrite(cacheExpirationSeconds, SECONDS).build(loader);
    }

    static final class AuthorizeToken extends CacheLoader<ClientSecret, Token> {
        private final AuthorizationApi api;

        @Inject AuthorizeToken(AuthorizationApi api) {
            this.api = api;
        }

        @Override public Token load(ClientSecret key) throws Exception {
            return api.authorize_client_secret(key.clientId(), key.clientSecret(), key.resource());
        }
    }

    @Override public HttpRequest filter(HttpRequest request) throws HttpException {
        long now = currentTimeSeconds();
        ClientSecret client = ClientSecret.create(
                credentialsSupplier.get().identity,
                credentialsSupplier.get().credential,
                resource,
                ON_COMMA.join(scopes.forRequest(request)),
                now + tokenDuration
        );
        Token token = tokenCache.getUnchecked(client);
        String authorization = String.format("%s %s", token.tokenType(), token.accessToken());
        return request.toBuilder().addHeader("Authorization", authorization).build();
    }

    long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }
}
