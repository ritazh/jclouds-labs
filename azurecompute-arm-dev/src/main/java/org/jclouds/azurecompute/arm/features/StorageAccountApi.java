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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.arm.domain.Availability;
import org.jclouds.azurecompute.arm.domain.CreateStorageServiceParams;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.StorageServiceKeys;
import org.jclouds.azurecompute.arm.domain.StorageServiceUpdateParams;
import org.jclouds.azurecompute.arm.functions.StatusCodeParser;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.binders.BindToJsonPayload;

import java.util.List;
import java.util.Map;

/**
 * The Azure Resource Management API includes operations for managing the storage accounts in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/mt163683.aspx">docs</a>
 */
@Path("/")
@RequestFilters(OAuthFilter.class)
@QueryParams(keys = "api-version", values = "2015-06-15")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface StorageAccountApi {

   /**
    * The List Storage Accounts operation lists the storage accounts that are available in the specified subscription
    * and resource group.
    * https://msdn.microsoft.com/en-us/library/mt163559.aspx
    */
   @Named("ListStorageAccounts")
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts")
   @GET
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<StorageService> list();

   /**
    * The List Storage Accounts operation lists the storage accounts that are available in the specified subscription.
    * https://msdn.microsoft.com/en-us/library/mt163559.aspx
    */
   @Named("ListAllStorageAccounts")
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts")
   @GET
   @SelectJson("value")
   @Fallback(Fallbacks.EmptyListOnNotFoundOr404.class)
   List<StorageService> listAll();

   /**
    * The Create Storage Account asynchronous operation creates a new storage account in Microsoft Azure.
    * https://msdn.microsoft.com/en-us/library/mt163564.aspx
    * PUT
    */
   @Named("CreateStorageAccount")
   @Payload("%7B\"location\":\"{location}\",\"tags\":{tags},\"properties\":{properties}%7D")
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   @MapBinder(BindToJsonPayload.class)
   @PUT
   CreateStorageServiceParams create(@PathParam("storageAccountName") String storageAccountName,
                                     @PayloadParam("location") String location,
                                     @PayloadParam("tags") Map<String, String> tags,
                                     @PayloadParam("properties") Map<String, String> properties);

   /**
    * The Check Storage Account Name Availability operation checks to see if the specified storage account name is
    * available, or if it has already been taken. https://msdn.microsoft.com/en-us/library/mt163642.aspx
    * POST
    */
   @Named("CheckStorageAccountNameAvailability")
   @POST
   @Payload("%7B\"name\":\"{name}\",\"type\":\"Microsoft.Storage/storageAccounts\"%7D")
   @Path("/providers/Microsoft.Storage/checkNameAvailability")
   Availability isAvailable(@PayloadParam("name") String storageAccountName);

   /**
    * The Get Storage Account Properties operation returns system properties for the specified storage account.
    * https://msdn.microsoft.com/en-us/library/mt163553.aspx
    */
   @Named("GetStorageAccountProperties")
   @GET
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   @Fallback(NullOnNotFoundOr404.class)
   StorageService get(@PathParam("storageAccountName") String storageAccountName);

   /**
    * The Get Storage Keys operation returns the primary and secondary access keys for the specified storage account.
    * https://msdn.microsoft.com/en-us/library/mt163589.aspx
    * POST
    */
   @Named("GetStorageAccountKeys")
   @POST
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}/listKeys")
   @Fallback(NullOnNotFoundOr404.class)
   StorageServiceKeys getKeys(@PathParam("storageAccountName") String storageAccountName);

   /**
    * https://msdn.microsoft.com/en-us/library/mt163567.aspx
    * POST
    */
   @Named("RegenerateStorageAccountKeys")
   @POST
   @Payload("%7B\"keyName\":\"{keyName}\"%7D")
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccount}/regenerateKey")
   StorageServiceKeys regenerateKeys(@PathParam("storageAccount") String storageAccount,
                                     @PayloadParam("keyName") String keyName);

   /**
    * The Update Storage Account asynchronous operation updates the label, the description, and enables or disables the
    * geo-replication status for the specified storage account. https://msdn.microsoft.com/en-us/library/mt163639.aspx
    * PATCH
    */
   @Named("UpdateStorageAccount")
   @PATCH
   @Payload("%7B\"tags\":{tags},\"properties\":{properties}%7D")
   @MapBinder(BindToJsonPayload.class)
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   StorageServiceUpdateParams update(
           @PathParam("storageAccountName") String storageAccountName,
           @PayloadParam("properties") StorageServiceUpdateParams.StorageServiceUpdateProperties properties,
           @PayloadParam("tags") Map<String, String> tags);

   /**
    * https://msdn.microsoft.com/en-us/library/mt163652.aspx
    * DELETE
    */
   @Named("DeleteStorageAccount")
   @DELETE
   @ResponseParser(StatusCodeParser.class)
   @Path("/resourcegroups/{resourceGroup}/providers/Microsoft.Storage/storageAccounts/{storageAccountName}")
   String delete(@PathParam("storageAccountName") String storageAccountName);

}
