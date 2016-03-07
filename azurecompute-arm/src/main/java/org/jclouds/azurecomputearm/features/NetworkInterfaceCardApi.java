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

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.azurecomputearm.domain.NetworkInterfaceCard;
import org.jclouds.azurecomputearm.domain.VirtualNetwork;
import org.jclouds.azurecomputearm.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/subscriptions/{subscriptionid}/resourcegroups/{resourcegroup}/providers/Microsoft.Network/")
@QueryParams(keys = "api-version", values = "2015-06-15")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)

public interface NetworkInterfaceCardApi {

   @Named("networkinterfacecard:list")
   @Path("networkInterfaces")
   @SelectJson("value")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<NetworkInterfaceCard> listNetworkInterfaceCards();

   @Named("networkinterfacecard:create_or_update")
   @Path("networkInterfaces/{networkinterfacecardname}")
   @MapBinder(BindToJsonPayload.class)
   @PUT
   @Fallback(NullOnNotFoundOr404.class)
   NetworkInterfaceCard createOrUpdateNetworkInterfaceCard(@PathParam("networkinterfacecardname") String networkinterfacecardname,
                                                           @PayloadParam("location") String location,
                                                           @PayloadParam("properties") NetworkInterfaceCard.NetworkInterfaceCardProperties properties);


   @Named("networkinterfacecard:get")
   @Path("networkInterfaces/{networkinterfacecardname}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   NetworkInterfaceCard getNetworkInterfaceCard(@PathParam("networkinterfacecardname") String networkinterfacecardname);

   @Named("networkinterfacecard:delete")
   @Path("networkInterfaces/{networkinterfacecardname}")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteNetworkInterfaceCard(@PathParam("networkinterfacecardname") String networkinterfacecardname);
}
