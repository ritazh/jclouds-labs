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
package org.jclouds.azurecompute.arm.binders;


import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.json.Json;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.reflect.Invocation;

import static com.google.common.base.Preconditions.checkArgument;

@Singleton
public class BindSubscriptionIdAndResourceName extends BindToJsonPayload {
   private final String subscriptionId;

   @Inject
   public BindSubscriptionIdAndResourceName(Json jsonBinder,
                                                    @Named("subscriptionId") String subscriptionId) {
      super(jsonBinder);
      this.subscriptionId = subscriptionId;
   }

   @Override public <R extends HttpRequest> R bindToRequest(R request, Map<String, Object> postParams) {
      checkArgument(request instanceof GeneratedHttpRequest, "this binder only supports GeneratedHttpRequests");
      checkArgument(GeneratedHttpRequest.class.cast(request).getCaller().isPresent(), "this binder is only for delegate apis");

      // Get the value for the resource group name from the caller API
      Invocation caller = GeneratedHttpRequest.class.cast(request).getCaller().get();
      String resourceGroupName = (String) caller.getArgs().get(0);

      // Populate the computed values to the payload param map so they are bound to the payload
      postParams.put("resourceGroup", resourceGroupName);
      postParams.put("subscriptionId", subscriptionId);

      return super.bindToRequest(request, postParams);
   }
}
