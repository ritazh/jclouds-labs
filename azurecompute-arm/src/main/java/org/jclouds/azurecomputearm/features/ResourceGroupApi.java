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

import java.beans.ConstructorProperties;
import java.io.Closeable;
import java.net.URI;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.azurecomputearm.AzureComputeApi;
import org.jclouds.azurecomputearm.domain.PaginatedCollection;
import org.jclouds.azurecomputearm.domain.ResourceGroup;
import org.jclouds.azurecomputearm.domain.options.ListOptions;
import org.jclouds.azurecomputearm.functions.BaseToPagedIterable;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.Json;
//import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.azurecomputearm.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;

import org.jclouds.rest.annotations.PATCH;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.TypeLiteral;

/**
 * The Azure Resource Manager API includes operations for managing resource groups in your subscription.
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790546.aspx">docs</a>
 */
@Path("/resourcegroups")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface ResourceGroupApi extends Closeable{

   @Named("resourcegroup:list")
   @GET
   @ResponseParser(ParseResourceGroups.class)
   @Transform(ParseResourceGroups.ToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<ResourceGroup> list();

   @Named("resourcegroup:list")
   @GET
   @ResponseParser(ParseResourceGroups.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   IterableWithMarker<ResourceGroup> list(ListOptions options);

   static final class ParseResourceGroups extends ParseJson<ParseResourceGroups.ResourceGroups> {
      @Inject
      ParseResourceGroups(Json json) {
         super(json, TypeLiteral.get(ResourceGroups.class));
      }

      private static class ResourceGroups extends PaginatedCollection<ResourceGroup> {
         @ConstructorProperties({"value"})
         public ResourceGroups(List<ResourceGroup> items) {
            super(items);
         }
      }

     private static class ToPagedIterable extends BaseToPagedIterable<ResourceGroup, ListOptions> {
         @Inject
         ToPagedIterable(AzureComputeApi api, Function<URI, ListOptions> linkToOptions) {
            super(api, linkToOptions);
         }

         @Override
         protected IterableWithMarker<ResourceGroup> fetchPageUsingOptions(ListOptions options, Optional<Object> arg0) {
            return api.getResourceGroupApi().list(options);
         }
      }
   }

   @Named("resourcegroup:create")
   @PUT
   @Path("/{name}")
   @Produces(MediaType.APPLICATION_JSON)
   @MapBinder(BindToJsonPayload.class)
   ResourceGroup create(@PathParam("name") String name, @PayloadParam("location") String location, @PayloadParam("tags")Map<String, String> tags);

   @Named("resourcegroup:get")
   @GET
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   ResourceGroup get(@PathParam("name") String name);

   @Named("resourcegroup:update")
   @PATCH
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/{name}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   @MapBinder(BindToJsonPayload.class)
   ResourceGroup update(@PathParam("name") String name, @PayloadParam("tags")Map<String, String> tags);

   @Named("resourcegroup:delete")
   @DELETE
   @Path("/{name}")
   @Fallback(VoidOnNotFoundOr404.class)
   void delete(@PathParam("name") String name);
}

