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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;

import org.jclouds.oauth.v2.OAuthFallbacks.AuthorizationExceptionOn4xx;
import org.jclouds.oauth.v2.config.Authorization;
import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;

/**
 * Binds to an OAuth2 <a href="http://tools.ietf.org/html/rfc6749#section-3.1">authorization endpoint</a>.
 */
@Endpoint(Authorization.class)
public interface AzureAuthorizationApi extends Closeable {
   @Named("oauth2:authorize_client_secret")
   @POST
   @FormParams(keys = "grant_type", values = "client_credentials")
   @Consumes(APPLICATION_JSON)
   @Fallback(AuthorizationExceptionOn4xx.class)
   Token authorize_client_secret(
                           @FormParam("client_id") String client_id,
                           @FormParam("client_secret") String client_secret,
                           @FormParam("resource") String resource
   );
}
