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
import org.jclouds.azurecomputearm.domain.Subnet;
import org.jclouds.azurecomputearm.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.binders.BindToJsonPayload;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/subscriptions/{subscriptionid}/resourcegroups/{resourcegroup}/providers/Microsoft.Network/virtualNetworks/{virtualnetwork}")

@QueryParams(keys = "api-version", values = "2015-06-15")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface SubnetApi {

   @Named("subnet:list")
   @Path("subnets")
   @SelectJson("value")
   @GET
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Subnet> listSubnets();

   @Named("subnet:create_or_update")
   @Path("subnets/{subnetname}")
   @MapBinder(BindToJsonPayload.class)
   @PUT
   @Fallback(NullOnNotFoundOr404.class)
   Subnet createOrUpdateSubnet(@PathParam("subnetname") String subnetName,
                               @PayloadParam("properties") Subnet.SubnetProperties properties);

   @Named("subnet:get")
   @Path("subnets/{subnetname}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   Subnet getSubnet(@PathParam("subnetname") String subnetname);

   @Named("subnet:delete")
   @Path("subnets/{subnetname}")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   void deleteSubnet(@PathParam("subnetname") String subnetname);
}
