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
package org.jclouds.azurecompute.arm.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.jclouds.Fallbacks;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.domain.VirtualMachineInstance;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.functions.URIParser;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import java.net.URI;
import java.util.List;

/**
 * The Virtual Machine API includes operations for managing the virtual machines in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163630.aspx">docs</a>
 */
@Path("/resourceGroups/{resourceGroup}/providers/Microsoft.Compute/virtualMachines")
@RequestFilters(OAuthFilter.class)
@QueryParams(keys = "api-version", values = "2015-06-15")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VirtualMachineApi {

   /**
    * The Get Virtual Machine details
    */
   @Named("GetVirtualMachine")
   @GET
   @Path("/{name}")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualMachine get(@PathParam("name") String name);

   /**
    * The Get Virtual Machine details
    */
   @Named("GetVirtualMachineInstance")
   @GET
   @Path("/{name}/instanceView")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualMachineInstance getInstanceDetails(@PathParam("name") String name);

   /**
    * The Create Virtual Machine
    */
   @Named("CreateVirtualMachine")
   @PUT
   @Payload("%7B\"id\":\"{id}\",\"name\":\"{name}\",\"type\":\"Microsoft.Compute/virtualMachines\"," +
           "\"location\":\"{location}\",\"tags\":%7B%7D,\"properties\":{properties}%7D")
   @MapBinder(BindToJsonPayload.class)
   @Path("/{vmname}")
   @QueryParams(keys = "validating", values = "false")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   VirtualMachine create(@PathParam("vmname") String vmname, @PayloadParam("id") String id,
                         @PayloadParam("name") String name,
                         @PayloadParam("location") String location,
                         @PayloadParam("properties") VirtualMachineProperties properties);

   /**
    * The List Virtual Machines operation
    */
   @Named("ListVirtualMachines")
   @GET
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<VirtualMachine> list();

   /**
    * The Delete Virtual Machine operation
    */
   @Named("virtualmachine:delete")
   @DELETE
   @ResponseParser(URIParser.class)
   @Path("/{name}")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   URI delete(@PathParam("name") String name);

   /**
    * The Restart Virtual Machine operation
    */
   @Named("RestartVirtualMachine")
   @POST
   @Path("/{name}/restart")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void restart(@PathParam("name") String name);

   /**
    * The start Virtual Machine operation
    */
   @Named("StartVirtualMachine")
   @POST
   @Path("/{name}/start")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void start(@PathParam("name") String name);

   /**
    * The stop Virtual Machine operation
    */
   @Named("StopVirtualMachine")
   @POST
   @Path("/{name}/powerOff")
   @Fallback(Fallbacks.VoidOnNotFoundOr404.class)
   void stop(@PathParam("name") String name);

}

