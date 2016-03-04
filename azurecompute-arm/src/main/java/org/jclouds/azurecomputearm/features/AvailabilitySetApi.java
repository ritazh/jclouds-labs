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
import org.jclouds.azurecomputearm.domain.AvailabilitySet;
import org.jclouds.azurecomputearm.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.*;

import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * The Service Management API includes operations for listing the available data center locations for a cloud service in
 * your subscription.
 * <p/>
 *
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441299">docs</a>
 */
@Path("/subscriptions/{subscriptionId}/resourceGroups/{resourceGroup}/providers/Microsoft.Compute")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@RequestFilters(OAuthFilter.class)
@QueryParams(keys = "api-version", values = "2015-06-15")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AvailabilitySetApi {

   /**
    * The List Availability sets operation lists all of the availability sets that are valid for your subscription.
    */
   @Named("ListAvailabilitySets")
   @GET
   @Path("/availabilitySets")
   @SelectJson("value")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<AvailabilitySet> list();

   /**
    * Create Availability set operation creates the new availability set
    */
   @Named("CreateAvailabilitySet")
   @PUT
   @Payload("%7B\"name\":\"{name}\",\"type\":\"Microsoft.Compute/availabilitySets\"," +
           "\"location\":\"{location}\",\"tags\":%7B%7D,\"properties\":%7B\"platformUpdateDomainCount\": " +
           "{updateDomainCount},\"platformFaultDomainCount\": {faultDomainCount}%7D%7D")
   @Path("/availabilitySets/{availabilitySet}")
   @Fallback(EmptyListOnNotFoundOr404.class)
   AvailabilitySet create(@PathParam("availabilitySet") String availabilitySet, @PayloadParam("name") String name,
           @PayloadParam("location") String location, @PayloadParam("updateDomainCount") int updateDomainCount,
           @PayloadParam("faultDomainCount") int faultDomainCount);

   /**
    * The Delete Availability set operation deletes the availability set
    */
   @Named("DeleteAvailabilitySet")
   @DELETE
   @Path("/availabilitySets/{name}")
   @Fallback(EmptyListOnNotFoundOr404.class)
   void delete(@PathParam("name") String name);

   /**
    * The Get Availability set operation get one of the availability sets by name
    */
   @Named("GetAvailabilitySet")
   @GET
   @Path("/availabilitySets/{name}")
   @Fallback(EmptyListOnNotFoundOr404.class)
   AvailabilitySet get(@PathParam("name") String name);

}
