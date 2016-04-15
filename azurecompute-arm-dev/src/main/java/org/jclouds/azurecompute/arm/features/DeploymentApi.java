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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;

//https://management.azure.com/subscriptions/{subscription-id}/resourcegroups/{resource-group-name}/providers/microsoft.resources/deployments/{deployment-name}?api-version={api-version}

/**
 * - create deployment
 * - delete deployment
 * - get information about deployment
 */
@Path("/resourcegroups/{resourcegroup}/providers/microsoft.resources/deployments")
@QueryParams(keys = "api-version", values = "2016-02-01")
@RequestFilters(OAuthFilter.class)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface DeploymentApi {

   /**
    * The Create Template Deployment operation starts the process of an ARM Template deployment.
    * It then returns a Deployment object.
    */
   @Named("deployment:create")
   @Path("/{deploymentname}")
   @Payload("{properties}")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Deployment createDeployment(@PathParam("deploymentname") String deploymentname,
                               @PayloadParam("properties") String properties);

   /**
    * Get Deployment Information returns information about the specified deployment.
    */
   @Named("deployment:get")
   @Path("/{deploymentname}")
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   Deployment getDeployment(@PathParam("deploymentname") String deploymentname);

   /**
    * Validate Deployment validates deployment template before deployment
    */
   @Named("deployment:validate")
   @Path("/{deploymentname}/validate")
   @Payload("{properties}")
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   Deployment validateDeployment(@PathParam("deploymentname") String deploymentname,
                                 @PayloadParam("properties") String properties);
}
