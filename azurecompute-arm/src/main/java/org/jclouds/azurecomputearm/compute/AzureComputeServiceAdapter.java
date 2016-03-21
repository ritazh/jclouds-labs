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

import static com.google.common.base.Objects.firstNonNull;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.azurecomputearm.AzureComputeApi;
import org.jclouds.azurecomputearm.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecomputearm.compute.options.AzureComputeTemplateOptions;
import org.jclouds.azurecomputearm.config.AzureComputeProperties;
import org.jclouds.azurecomputearm.domain.Deployment;
import org.jclouds.azurecomputearm.domain.VMSize;
import org.jclouds.azurecomputearm.domain.ImageReference;
import org.jclouds.azurecomputearm.domain.Location;
import org.jclouds.azurecomputearm.domain.DeploymentParams;
import org.jclouds.azurecomputearm.domain.Publisher;
import org.jclouds.azurecomputearm.domain.Offer;
import org.jclouds.azurecomputearm.domain.SKU;
import org.jclouds.azurecomputearm.domain.Version;
import org.jclouds.azurecomputearm.domain.Deployment.RoleInstance;
import org.jclouds.azurecomputearm.domain.DeploymentParams.ExternalEndpoint;
import org.jclouds.azurecomputearm.features.OSImageApi;
import org.jclouds.azurecomputearm.util.ConflictManagementPredicate;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
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

   private static final String DEFAULT_LOGIN_PASSWORD = "Azur3Compute!";
   public static final String POST_SHUTDOWN_ACTION = "StoppedDeallocated";

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   private Logger logger = Logger.NULL;

   private final AzureComputeApi api;

   private final Predicate<String> operationSucceededPredicate;

   private final AzureComputeConstants azureComputeConstants;

   @Inject
   AzureComputeServiceAdapter(final AzureComputeApi api,
           final Predicate<String> operationSucceededPredicate, final AzureComputeConstants azureComputeConstants) {

      this.api = api;
      this.operationSucceededPredicate = operationSucceededPredicate;
      this.azureComputeConstants = azureComputeConstants;
   }

   @Override
   public NodeAndInitialCredentials<Deployment> createNodeWithGroupEncodedIntoName(
           final String group, final String name, final Template template) {

      // azure-specific options
      final AzureComputeTemplateOptions templateOptions = template.getOptions().as(AzureComputeTemplateOptions.class);

      final String loginUser = firstNonNull(templateOptions.getLoginUser(), DEFAULT_LOGIN_USER);
      final String loginPassword = firstNonNull(templateOptions.getLoginPassword(), DEFAULT_LOGIN_PASSWORD);
      final String location = template.getLocation().getId();
      final int[] inboundPorts = template.getOptions().getInboundPorts();

      final String storageAccountName = templateOptions.getStorageAccountName();

      String message = String.format("Creating a cloud service with name '%s', label '%s' in location '%s'", name, name, location);
      logger.debug(message);
      final String createCloudServiceRequestId = api.getCloudServiceApi().createWithLabelInLocation(name, name, location);
      if (!operationSucceededPredicate.apply(createCloudServiceRequestId)) {
         final String exceptionMessage = generateIllegalStateExceptionMessage(message, createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(exceptionMessage);
         throw new IllegalStateException(exceptionMessage);
      }
      logger.info("Cloud Service (%s) created with operation id: %s", name, createCloudServiceRequestId);


      final Set<ExternalEndpoint> externalEndpoints = Sets.newHashSet();
      for (int inboundPort : inboundPorts) {
         externalEndpoints.add(ExternalEndpoint.inboundTcpToLocalPort(inboundPort, inboundPort));
      }

      final DeploymentParams params = DeploymentParams.builder()
              .name(name)
              //.os(os)
              .username(loginUser)
              .password(loginPassword)
              //.sourceImageName((template.getImage().getId())[0])
              .mediaLink(createMediaLink(storageAccountName, name))
              .size(template.getHardware().getName())
              .externalEndpoints(externalEndpoints)
              .virtualNetworkName(templateOptions.getVirtualNetworkName())
              .subnetNames(templateOptions.getSubnetNames())
              .build();

      message = String.format("Creating a deployment with params '%s' ...", params);
      logger.debug(message);

      if (!new ConflictManagementPredicate(api) {
         @Override
         protected String operation() {
            return api.getDeploymentApiForService(name).create(params);
         }
      }.apply(name)) {
         final String illegalStateExceptionMessage = generateIllegalStateExceptionMessage(message, createCloudServiceRequestId, azureComputeConstants.operationTimeout());
         logger.warn(illegalStateExceptionMessage);
         logger.debug("Deleting cloud service (%s) ...", name);
         deleteCloudService(name);
         logger.debug("Cloud service (%s) deleted.", name);
         throw new IllegalStateException(illegalStateExceptionMessage);
      }

      logger.info("Deployment created with name: %s", name);

      final Set<Deployment> deployments = Sets.newHashSet();
      if (!retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            final Deployment deployment = api.getDeploymentApiForService(name).get(name);
            if (deployment != null) {
               deployments.add(deployment);
            }
            return !deployments.isEmpty();
         }
      }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(name)) {
         final String illegalStateExceptionMessage = format("Deployment %s was not created within %sms so it will be destroyed.",
                 name, azureComputeConstants.operationTimeout());
         logger.warn(illegalStateExceptionMessage);

         api.getDeploymentApiForService(name).delete(name);
         api.getCloudServiceApi().delete(name);

         throw new IllegalStateException(illegalStateExceptionMessage);
      }

      final Deployment deployment = deployments.iterator().next();

      // check if the role inside the deployment is ready
      checkRoleStatusInDeployment(name, deployment);

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
      return null; // TODO: get subscription id
   }
   private String getLocation() {
      return null; // TODO: get location
   }
   @Override
   public Iterable<Location> listLocations() {
      return api.getLocationApi(getSubscriptionId()).list();
   }

   @Override
   public Deployment getNode(final String id) {
      return null;
   }

   private void trackRequest(final String requestId) {
      if (!operationSucceededPredicate.apply(requestId)) {
         final String message = generateIllegalStateExceptionMessage(
                 "tracking request", requestId, azureComputeConstants.operationTimeout());
         logger.warn(message);
         throw new IllegalStateException(message);
      }
   }

   public Deployment internalDestroyNode(final String nodeId) {
      return null;
   }

   public Deployment getDeploymentFromNodeId(final String nodeId) {
      final List<Deployment> nodes = Lists.newArrayList();
      retry(new Predicate<String>() {
         @Override
         public boolean apply(final String input) {
            final Deployment deployment = getNode(nodeId);
            if (deployment != null) {
               nodes.add(deployment);
            }
            return !nodes.isEmpty();
         }
      }, 30 * 60, 1, SECONDS).apply(nodeId);

      return Iterables.getFirst(nodes, null);
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
      return null;
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

   @VisibleForTesting
   public static URI createMediaLink(final String storageServiceName, final String diskName) {
      return URI.create(
              String.format("https://%s.blob.core.windows.net/vhds/disk-%s.vhd", storageServiceName, diskName));
   }

   private void deleteCloudService(final String name) {
      if (!new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getCloudServiceApi().delete(name);
         }

      }.apply(name)) {
         final String deleteMessage = generateIllegalStateExceptionMessage("Delete cloud service " + name,
                 "CloudService delete", azureComputeConstants.operationTimeout());
         logger.warn(deleteMessage);
         throw new IllegalStateException(deleteMessage);
      }
   }

   private void deleteDeployment(final String id, final String cloudServiceName) {
      if (!new ConflictManagementPredicate(api) {

         @Override
         protected String operation() {
            return api.getDeploymentApiForService(cloudServiceName).delete(id);
         }

      }.apply(id)) {
         final String deleteMessage = generateIllegalStateExceptionMessage("Delete deployment " + cloudServiceName,
                 "Deployment delete", azureComputeConstants.operationTimeout());
         logger.warn(deleteMessage);
         throw new IllegalStateException(deleteMessage);
      }
   }


   private void checkRoleStatusInDeployment(final String name, Deployment deployment) {
      if (!retry(new Predicate<Deployment>() {

         @Override
         public boolean apply(Deployment deployment) {
            deployment = api.getDeploymentApiForService(deployment.name()).get(name);
            if (deployment.roleInstanceList() == null || deployment.roleInstanceList().isEmpty()) return false;
            return Iterables.all(deployment.roleInstanceList(), new Predicate<RoleInstance>() {
               @Override
               public boolean apply(RoleInstance input) {
                  if (input.instanceStatus() == Deployment.InstanceStatus.PROVISIONING_FAILED) {
                     final String message = format("Deployment %s is in provisioning failed status, so it will be destroyed.", name);
                     logger.warn(message);

                     api.getDeploymentApiForService(name).delete(name);
                     api.getCloudServiceApi().delete(name);

                     throw new IllegalStateException(message);
                  }
                  return input.instanceStatus() == Deployment.InstanceStatus.READY_ROLE;
               }
            });
         }
      }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(deployment)) {
         final String message = format("Role %s has not reached the READY_ROLE within %sms so it will be destroyed.",
                 deployment.name(), azureComputeConstants.operationTimeout());
         logger.warn(message);

         api.getDeploymentApiForService(name).delete(name);
         api.getCloudServiceApi().delete(name);

         throw new IllegalStateException(message);
      }
   }

   public static String generateIllegalStateExceptionMessage(String prefix, final String operationId, final long timeout) {
      final String warnMessage = format("%s - %s has not been completed within %sms.", prefix, operationId, timeout);
      return format("%s. Please, try by increasing `%s` and try again",
              warnMessage, AzureComputeProperties.OPERATION_TIMEOUT);
   }

}
