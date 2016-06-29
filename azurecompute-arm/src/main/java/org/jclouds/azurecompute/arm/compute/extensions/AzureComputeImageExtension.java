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
package org.jclouds.azurecompute.arm.compute.extensions;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.internal.LinkedTreeMap;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.arm.compute.functions.VMImageToImage;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.VMImage;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;

import static java.lang.String.format;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_NODE_SUSPENDED;

import com.google.common.util.concurrent.UncheckedTimeoutException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class AzureComputeImageExtension implements ImageExtension {
   private final AzureComputeApi api;
   private final Predicate<URI> imageAvailablePredicate;
   private final Predicate<String> nodeSuspendedPredicate;
   private final AzureComputeConstants azureComputeConstants;
   private final ListeningExecutorService userExecutor;
   private final String group;
   private final VMImageToImage imageReferenceToImage;
   public static final String CONTAINER_NAME = "vhdsnew";
   public static final String CUSTOM_IMAGE_PREFIX = "#";

   @Inject
   AzureComputeImageExtension(AzureComputeApi api,
                              @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<URI> imageAvailablePredicate,
                              @Named(TIMEOUT_NODE_SUSPENDED) Predicate<String> nodeSuspendedPredicate,
                              final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants,
                              @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                              VMImageToImage imageReferenceToImage) {
      this.userExecutor = userExecutor;
      this.group = azureComputeConstants.azureResourceGroup();
      this.imageReferenceToImage = imageReferenceToImage;
      this.api = api;
      this.imageAvailablePredicate = imageAvailablePredicate;
      this.nodeSuspendedPredicate = nodeSuspendedPredicate;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      String imageName = name.toLowerCase();
      return new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(imageName).build();
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      final CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      final String id = cloneTemplate.getSourceNodeId();
      final String storageAccountName = id.replaceAll("[^A-Za-z0-9 ]", "") + "stor";

      // VM needs to be stopped before it can be generalized
      String status = "";
      api.getVirtualMachineApi(group).stop(id);
      //Poll until resource is ready to be used
      if (nodeSuspendedPredicate.apply(id)) {
         return userExecutor.submit(new Callable<Image>() {
            @Override
            public Image call() throws Exception {
               api.getVirtualMachineApi(group).generalize(id);

               final String[] disks = new String[2];
               URI uri = api.getVirtualMachineApi(group).capture(id, cloneTemplate.getName(), CONTAINER_NAME);
               if (uri != null) {
                  if (imageAvailablePredicate.apply(uri)) {
                     List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
                     if (definitions != null) {
                        for (ResourceDefinition definition : definitions) {
                           LinkedTreeMap<String, String> properties = (LinkedTreeMap<String, String>) definition.properties();
                           Object storageObject = properties.get("storageProfile");
                           LinkedTreeMap<String, String> properties2 = (LinkedTreeMap<String, String>) storageObject;
                           Object osDiskObject = properties2.get("osDisk");
                           LinkedTreeMap<String, String> osProperties = (LinkedTreeMap<String, String>) osDiskObject;
                           Object dataDisksObject = properties2.get("dataDisks");
                           ArrayList<Object> dataProperties = (ArrayList<Object>) dataDisksObject;
                           LinkedTreeMap<String, String> datadiskObject = (LinkedTreeMap<String, String>) dataProperties.get(0);

                           disks[0] = osProperties.get("name");
                           disks[1] = datadiskObject.get("name");

                           VirtualMachine vm = api.getVirtualMachineApi(group).get(id);
                           String location = vm.location();
                           final VMImage ref = VMImage.create(CUSTOM_IMAGE_PREFIX + group, CUSTOM_IMAGE_PREFIX + storageAccountName, disks[0], disks[1], location, false);
                           return imageReferenceToImage.apply(ref);
                        }
                     }
                  }
               }
               throw new UncheckedTimeoutException("Image was not created within the time limit: "
                       + cloneTemplate.getName());
            }
         });
      } else {
         final String illegalStateExceptionMessage = format("Node %s was not suspended within %sms.",
                 id, azureComputeConstants.operationTimeout());
         throw new IllegalStateException(illegalStateExceptionMessage);
      }
   }

   @Override
   public boolean deleteImage(String id) {
      return false;
   }
}
