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
package org.jclouds.azurecomputearm.features;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.google.common.collect.FluentIterable;
import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.azurecomputearm.domain.Subscription;
import org.jclouds.azurecomputearm.json.SubscriptionParser;
import org.jclouds.azurecomputearm.xml.ListRoleSizesHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

/**
 * The Service Management API includes operations for retrieving information about a subscription.
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg715315">docs</a>
 */
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_JSON)
public interface SubscriptionApi {

   /**
    * The List Subscriptions operation lists the subscriptions that are available with token.
    //   @ResponseParser(SubscriptionParser.class)
    */
   @Named("ListSubscriptions")
   @GET
   @Path("/subscriptions")
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   FluentIterable<Subscription> listSubscriptions();

}
