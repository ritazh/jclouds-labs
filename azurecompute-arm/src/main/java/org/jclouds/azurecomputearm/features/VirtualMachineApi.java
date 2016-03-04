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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecomputearm.binders.CaptureVMImageParamsToXML;
import org.jclouds.azurecomputearm.binders.RoleToXML;
import org.jclouds.azurecomputearm.domain.VirtualMachine;
import org.jclouds.azurecomputearm.domain.CaptureVMImageParams;
import org.jclouds.azurecomputearm.domain.Role;
import org.jclouds.azurecomputearm.domain.VirtualMachineProperties;
import org.jclouds.azurecomputearm.functions.ParseRequestIdHeader;
import org.jclouds.azurecomputearm.oauth.v2.filters.OAuthFilter;
import org.jclouds.azurecomputearm.xml.RoleHandler;
import org.jclouds.rest.annotations.*;
import org.jclouds.rest.binders.BindToJsonPayload;

import java.util.List;

/**
 * The Virtual Machine API includes operations for managing the virtual machines in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163630.aspx">docs</a>
 */
@Path("/subscriptions/{subscriptionId}/resourceGroups/{resourceGroup}/providers/Microsoft.Compute")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@RequestFilters(OAuthFilter.class)
@QueryParams(keys = "api-version", values = "2015-06-15")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface VirtualMachineApi {

   /**
    * The Get Virtual Machine operation
    */
   @Named("GetVirtualMachine")
   @GET
   @Path("/virtualMachines/{name}")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   VirtualMachine get(@PathParam("name") String name);

   /**
    * The Create Virtual Machine operation
    */
   @Named("CreateVirtualMachine")
   @PUT
   @Payload("%7B\"id\":\"{id}\",\"name\":\"{name}\",\"type\":\"Microsoft.Compute/virtualMachines\"," +
           "\"location\":\"{location}\",\"tags\":%7B%7D,\"properties\":{properties}%7D")
   @MapBinder(BindToJsonPayload.class)
   @Path("/virtualMachines/{vmname}")
   @QueryParams(keys = "validating", values = "false")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   VirtualMachine create(@PathParam("vmname") String vmname,@PayloadParam("id") String id,
                         @PayloadParam("name") String name,
                         @PayloadParam("location") String location,
                         @PayloadParam("properties") VirtualMachineProperties properties);

   /**
    * The List Virtual Machines operation
    */
   @Named("ListVirtualMachines")
   @GET
   @Path("/virtualMachines")
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<VirtualMachine> list();

   @Named("RestartRole")
   @POST
   // Warning : the url in the documentation is WRONG ! @see
   // http://social.msdn.microsoft.com/Forums/pl-PL/WAVirtualMachinesforWindows/thread/\
   // 7ba2367b-e450-49e0-89e4-46c240e9d213
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<RestartRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\">"
           + "<OperationType>RestartRoleOperation</OperationType></RestartRoleOperation>")
   String restart(@PathParam("name") String name);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157201
    */
   @Named("CaptureRole")
   @POST
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<CaptureRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\">"
           + "<OperationType>CaptureRoleOperation</OperationType>"
           + "<PostCaptureAction>Delete</PostCaptureAction>"
           + "<TargetImageLabel>{imageLabel}</TargetImageLabel>"
           + "<TargetImageName>{imageName}</TargetImageName></CaptureRoleOperation>")
   String capture(@PathParam("name") String name, @PayloadParam("imageName") String imageName,
           @PayloadParam("imageLabel") String imageLabel);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157195
    */
   @Named("ShutdownRole")
   @POST
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<ShutdownRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\">"
           + "<OperationType>ShutdownRoleOperation</OperationType>" +
           "<PostShutdownAction>{postShutdownAction}</PostShutdownAction></ShutdownRoleOperation>")
   String shutdown(@PathParam("name") String name, @PayloadParam("postShutdownAction") String postShutdownAction);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157195
    */
   @Named("ShutdownRole")
   @POST
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<ShutdownRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\">"
           + "<OperationType>ShutdownRoleOperation</OperationType></ShutdownRoleOperation>")
   String shutdown(@PathParam("name") String name);

   /**
    * http://msdn.microsoft.com/en-us/library/jj157189
    */
   @Named("StartRole")
   @POST
   @Path("/roleinstances/{name}/Operations")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   @Payload(value = "<StartRoleOperation xmlns=\"http://schemas.microsoft.com/windowsazure\">"
           + "<OperationType>StartRoleOperation</OperationType></StartRoleOperation>")
   String start(@PathParam("name") String name);

   /**
    * https://msdn.microsoft.com/en-us/library/azure/jj157193.aspx
    */
   @Named("GetRole")
   @GET
   @Path("/roles/{roleName}")
   @Produces(MediaType.APPLICATION_XML)
   @XMLResponseParser(RoleHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   Role getRole(@PathParam("roleName") String roleName);

   /**
    * https://msdn.microsoft.com/library/azure/jj157187.aspx
    */
   @Named("UpdateRole")
   @PUT
   @Path("/roles/{roleName}")
   @Produces(MediaType.APPLICATION_XML)
   @ResponseParser(ParseRequestIdHeader.class)
   String updateRole(@PathParam("roleName") String roleName, @BinderParam(RoleToXML.class) Role role);

   /**
    * The Capture VM Image operation creates a copy of the operating system virtual hard disk (VHD) and all of the data
    * VHDs that are associated with the Virtual Machine, saves the VHD copies in the same storage location as the original
    * VHDs, and registers the copies as a VM Image in the image repository that is associated with the specified subscription.
    */
   @Named("CaptureVMImage")
   @POST
   @Produces(MediaType.APPLICATION_XML)
   @Path("/roleinstances/{name}/Operations")
   @ResponseParser(ParseRequestIdHeader.class) String capture(@PathParam("name") String name,
         @BinderParam(CaptureVMImageParamsToXML.class) CaptureVMImageParams params);
}
