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
package org.jclouds.azurecompute.arm.compute;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.DeploymentProperties;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.PublicIPAddress;
import org.jclouds.azurecompute.arm.domain.ResourceGroup;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.VMDeployment;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.azurecompute.arm.util.DeploymentTemplateBuilder;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.json.internal.GsonWrapper;
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
public class AzureComputeServiceAdapter implements ComputeServiceAdapter<VMDeployment, VMSize, ImageReference, Location> {

   private static int runningNumber = 1;

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
   public NodeAndInitialCredentials<VMDeployment> createNodeWithGroupEncodedIntoName(
           final String group, final String name, final Template template) {

      final String location = template.getLocation().getId();
      DeploymentTemplateBuilder deploymentTemplateBuilder = new DeploymentTemplateBuilder(group, name, template);

      final String loginUser = deploymentTemplateBuilder.getLoginUserUsername();
      final String loginPassword = deploymentTemplateBuilder.getLoginPassword();

      DeploymentBody deploymentTemplateBody =  deploymentTemplateBuilder.getDeploymentTemplate();

      DeploymentProperties properties = DeploymentProperties.create(deploymentTemplateBody);
      Gson gson = new GsonBuilder().disableHtmlEscaping().create();
      org.jclouds.json.Json json = new GsonWrapper(gson);

      final String deploymentTemplate = UrlEscapers.urlFormParameterEscaper().escape(json.toJson(properties));

      logger.debug("Deployment created with name: %s", name);

      final Set<VMDeployment> deployments = Sets.newHashSet();
      if (!retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            runningNumber++;


            ResourceGroupApi resourceGroupApi = api.getResourceGroupApi();
            ResourceGroup resourceGroup = resourceGroupApi.get(getGroupId());
            String resourceGroupName;

            if (resourceGroup == null){
               final Map<String, String> tags = ImmutableMap.of("description", "jClouds managed VMs");
               resourceGroupName = resourceGroupApi.create(getGroupId(), location, tags).name();
            } else {
               resourceGroupName = resourceGroup.name();
            }

            Deployment deployment = api.getDeploymentApi(resourceGroupName).createDeployment(name, deploymentTemplate);

            if (deployment != null) {
               VMDeployment vmDeployment = new VMDeployment();
               vmDeployment.deployment = deployment;
               deployments.add(vmDeployment);
            } else {
               logger.debug("Failed to create deployment!");
            }
            return !deployments.isEmpty();
         }
      }, azureComputeConstants.operationTimeout(), 1, SECONDS).apply(name)) {
         final String illegalStateExceptionMessage = format("Deployment %s was not created within %sms so it will be destroyed.",
                 name, azureComputeConstants.operationTimeout());
         logger.warn(illegalStateExceptionMessage);

         throw new IllegalStateException(illegalStateExceptionMessage);
      }

      final VMDeployment deployment = deployments.iterator().next();


      return new NodeAndInitialCredentials<VMDeployment>(deployment, name,
              LoginCredentials.builder().user(loginUser).identity(loginUser).password(loginPassword).authenticateSudo(true).build());
   }

   @Override
   public Iterable<VMSize> listHardwareProfiles() {
      return api.getVMSizeApi(getLocation()).list();
   }

   private void getImagesFromPublisher(String publisherName, List<ImageReference> osImagesRef) {
      OSImageApi osImageApi = api.getOSImageApi(getLocation());
      Iterable<Offer> offerList = osImageApi.listOffers(publisherName);
      for (Offer offer : offerList) {
         Iterable<SKU> skuList = osImageApi.listSKUs(publisherName, offer.name());
         for (SKU sku : skuList) {
            osImagesRef.add(ImageReference.create(publisherName, offer.name(), sku.name(), null));
         }
      }
   }

   @Override
   public Iterable<ImageReference> listImages() {
      final List<ImageReference> osImages = Lists.newArrayList();
      getImagesFromPublisher("Microsoft.WindowsAzure.Compute", osImages);
      getImagesFromPublisher("MicrosoftWindowsServer", osImages);
      getImagesFromPublisher("Canonical", osImages);
      return osImages;
   }

   @Override
   public ImageReference getImage(final String id) {
      Iterable<ImageReference> images = listImages();
      for (ImageReference image : images) {
         if (id.contains(image.offer()) && id.contains(image.sku())) {
            return image;
         }
      }
      return null;
   }

   private String getLocation() {
      return "eastasia"; // TODO: get location
   }

   @Override
   public Iterable<Location> listLocations() {
      return api.getLocationApi().list();
   }

   private String getResourceGroupFromId(String id) {
      String searchStr = "/resourceGroups/";
      int indexStart = id.lastIndexOf(searchStr) + searchStr.length();
      searchStr = "/providers/";
      int indexEnd = id.lastIndexOf(searchStr);

      String resourceGroup = id.substring(indexStart, indexEnd);
      return resourceGroup;
   }

   @Override
   public VMDeployment getNode(final String id) {
      Deployment deployment = api.getDeploymentApi(getGroupId()).getDeployment(id);
      String resourceGroup = getResourceGroupFromId(deployment.id());
      VMDeployment vmDeployment = new VMDeployment();
      vmDeployment.deployment = deployment;
      List<PublicIPAddress> list = getIPAddresses(deployment);
      vmDeployment.ipAddressList = list;
      return vmDeployment;
   }

   public VMDeployment internalDestroyNode(final String nodeId) {
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
      getNode(id);
   }

   @Override
   public void suspendNode(final String id) {
   }

   private List<PublicIPAddress> getIPAddresses(Deployment deployment) {
      List<PublicIPAddress> list = new ArrayList<PublicIPAddress>();
      String resourceGroup = getResourceGroupFromId(deployment.id());

      if (deployment.properties() != null && deployment.properties().dependencies() != null) {
         List<Deployment.Dependency> dependencies = deployment.properties().dependencies();
         for (int d = 0; d < dependencies.size(); d++) {
            if (dependencies.get(d).resourceType().equals("Microsoft.Network/networkInterfaces")) {
               List<Deployment.Dependency> dependsOn = dependencies.get(d).dependsOn();
               for (int e = 0; e < dependsOn.size(); e++) {
                  if (dependsOn.get(e).resourceType().equals("Microsoft.Network/publicIPAddresses")) {
                     String resourceName = dependsOn.get(e).resourceName();
                     PublicIPAddress ip = api.getPublicIPAddressApi(resourceGroup).getPublicIPAddress(resourceName);
                     list.add(ip);
                  }
               }
            }
         }
      }
      return list;
   }

   @Override
   public Iterable<VMDeployment> listNodes() {
      System.out.println("listNodes");

      List<Deployment> deployments = api.getDeploymentApi(getGroupId()).listDeployments();

      System.out.println("Found " + deployments.size() + " nodes");
      List<VMDeployment> vmDeployments = new ArrayList<VMDeployment>();

      for (Deployment d : deployments){
         VMDeployment vmDeployment = new VMDeployment();
         vmDeployment.deployment = d;
         List<PublicIPAddress> list = getIPAddresses(d);
         vmDeployment.ipAddressList = list;
         vmDeployments.add(vmDeployment);
      }
      return vmDeployments;
   }

   @Override
   public Iterable<VMDeployment> listNodesByIds(final Iterable<String> ids) {
      System.out.println("listNodesByIds");
      for (String str : ids) {
         System.out.println(str);
      }
      return Iterables.filter(listNodes(), new Predicate<VMDeployment>() {
         @Override
         public boolean apply(final VMDeployment input) {
            return Iterables.contains(ids, input.deployment.name());
         }
      });
   }

   private String getGroupId() {
      String group =  System.getProperty("test.azurecompute-arm.groupname");
      if (group == null)
         group = "jCloudsGroup";
      return group;
   }

}
