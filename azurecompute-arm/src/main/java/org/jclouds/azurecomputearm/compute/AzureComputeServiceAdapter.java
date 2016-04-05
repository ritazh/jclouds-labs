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
package org.jclouds.azurecomputearm.compute;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecomputearm.AzureComputeApi;
import org.jclouds.azurecomputearm.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecomputearm.domain.Deployment;
import org.jclouds.azurecomputearm.domain.VMSize;
import org.jclouds.azurecomputearm.domain.ImageReference;
import org.jclouds.azurecomputearm.domain.Location;

import org.jclouds.azurecomputearm.domain.Publisher;
import org.jclouds.azurecomputearm.domain.Offer;
import org.jclouds.azurecomputearm.domain.SKU;
import org.jclouds.azurecomputearm.domain.Version;

import org.jclouds.azurecomputearm.features.OSImageApi;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Defines the connection between the {@link AzureComputeApi} implementation and the jclouds
 * {@link org.jclouds.compute.ComputeService}.
 */
@Singleton
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<Deployment, VMSize, ImageReference, Location> {

   private static final String DEFAULT_LOGIN_USER = "jclouds";

   private static final String DEFAULT_LOGIN_PASSWORD = "password";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   private final AzureComputeApi api;

   private final AzureComputeConstants azureComputeConstants;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api, final AzureComputeConstants azureComputeConstants) {

      this.api = api;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public NodeAndInitialCredentials<Deployment> createNodeWithGroupEncodedIntoName(
           final String group, final String name, final Template template) {

      // azure-specific options
//      final AzureComputeTemplateOptions templateOptions = template.getOptions().as(AzureComputeTemplateOptions.class);
//
//      final String loginUser = firstNonNull(templateOptions.getLoginUser(), DEFAULT_LOGIN_USER);
//      final String loginPassword = firstNonNull(templateOptions.getLoginPassword(), DEFAULT_LOGIN_PASSWORD);
//      final String location = template.getLocation().getId();

      final String loginUser = DEFAULT_LOGIN_USER;
      final String loginPassword = DEFAULT_LOGIN_PASSWORD;

      logger.info("Deployment created with name: %s", name);

      final Set<Deployment> deployments = Sets.newHashSet();
      if (!retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            final Deployment deployment = null;
            if (deployment != null) {
               deployments.add(deployment);
            }
            return !deployments.isEmpty();
         }
      }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(name)) {
         final String illegalStateExceptionMessage = format("Deployment %s was not created within %sms so it will be destroyed.",
                 name, azureComputeConstants.operationTimeout());
         logger.warn(illegalStateExceptionMessage);

         throw new IllegalStateException(illegalStateExceptionMessage);
      }

      final Deployment deployment = deployments.iterator().next();


      return new NodeAndInitialCredentials<Deployment>(deployment, name,
              LoginCredentials.builder().user(loginUser).password(loginPassword).authenticateSudo(true).build());
   }

   @Override
   public Iterable<VMSize> listHardwareProfiles() {
      return api.getVMSizeApi(getSubscriptionId(), getLocation()).list();
   }

   @Override
   public Iterable<ImageReference> listImages() {
      final List<ImageReference> osImages = Lists.newArrayList();

      OSImageApi osImageApi = api.getOSImageApi(getSubscriptionId(), getLocation());

      Iterable<Publisher> list = osImageApi.listPublishers();
      for (Publisher publisher : list) {
         Iterable<Offer> offerList = osImageApi.listOffers(publisher.name());
         for (Offer offer : offerList) {
            Iterable<SKU> skuList = osImageApi.listSKUs(publisher.name(), offer.name());
            for (SKU sku : skuList) {
               Iterable<Version> versions = osImageApi.listVersions(publisher.name(), offer.name(), sku.name());
               for (Version version : versions) {
                  osImages.add(ImageReference.create(publisher.name(), offer.name(), sku.name(), version.name()));
               }
            }
         }
      }
      return osImages;
   }

   @Override
   public ImageReference getImage(final String id) {
      ImageReference imageReference = null;
      OSImageApi osImageApi = api.getOSImageApi(getSubscriptionId(), getLocation());

      Iterable<Publisher> list = osImageApi.listPublishers();
      for (Publisher publisher : list) {
         if (id.contains(publisher.name())) {
            Iterable<Offer> offerList = osImageApi.listOffers(publisher.name());
            for (Offer offer : offerList) {
               if (id.contains(offer.name())) {
                  Iterable<SKU> skuList = osImageApi.listSKUs(publisher.name(), offer.name());
                  for (SKU sku : skuList) {
                     if (id.contains(sku.name())) {
                        Iterable<Version> versions = osImageApi.listVersions(publisher.name(), offer.name(), sku.name());
                        for (Version version : versions) {
                           if (id.contains(version.name())) {
                              imageReference = ImageReference.create(publisher.name(), offer.name(), sku.name(), version.name());
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
      return imageReference;
   }

   private String getSubscriptionId() {
      return System.getProperty("azurecompute-arm.subscriptionid");
   }

   private String getLocation() {
      return "westus"; // TODO: get location
   }

   @Override
   public Iterable<Location> listLocations() {
      return api.getLocationApi(getSubscriptionId()).list();
   }

   @Override
   public Deployment getNode(final String id) {
      return null;
   }

   public Deployment internalDestroyNode(final String nodeId) {
      return null;
   }

   @Override
   public void destroyNode(final String id) {
      logger.debug("Destroying %s ...", id);
      if (internalDestroyNode(id) != null) {
         logger.debug("Destroyed %s!", id);
      } else {
         logger.warn("Can't destroy %s!", id);
      }
   }

   @Override
   public void rebootNode(final String id) {

   }

   @Override
   public void resumeNode(final String id) {
   }

   @Override
   public void suspendNode(final String id) {
   }

   @Override
   public Iterable<Deployment> listNodes() {
      List<Deployment> list = new ArrayList<Deployment>(1);
      return list;
   }

   @Override
   public Iterable<Deployment> listNodesByIds(final Iterable<String> ids) {
      return Iterables.filter(listNodes(), new Predicate<Deployment>() {
         @Override
         public boolean apply(final Deployment input) {
            return Iterables.contains(ids, input.name());
         }
      });
   }

}
