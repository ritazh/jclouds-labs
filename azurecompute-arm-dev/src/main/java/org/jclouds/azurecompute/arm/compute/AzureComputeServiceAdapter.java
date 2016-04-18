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
import static com.google.common.base.Predicates.notNull;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.jclouds.util.Predicates2.retry;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule.AzureComputeConstants;

import org.jclouds.azurecompute.arm.compute.options.AzureComputeArmTemplateOptions;
import org.jclouds.azurecompute.arm.domain.Deployment;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.VirtualMachine;
import org.jclouds.azurecompute.arm.features.OSImageApi;
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

   private static int runningNumber = 1;

   private static final String DEFAULT_LOGIN_USER = "jclouds";

   private static final String DEFAULT_LOGIN_PASSWORD = "p@sswOrd1!";

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

   private String getPutBody(String template, String mode, String parameters) {
      String body = "{ " +
              "\"properties\" : " +
              "  { " +
              "    \"template\" : " + template + ", " +
              "    \"mode\" : \"" + mode + "\", " +
              "    \"parameters\" : " + parameters + " " +
              "  } " +
              "}";
      return body;
   }

   private String getTemplate(String name, Template template) {
      //String osFamily = template.getImage().getOperatingSystem().getFamily().name();
      String imageProvider = template.getImage().getProviderId();
      String imageOffer = template.getImage().getName();
      String imageSku = template.getImage().getDescription();
      String vmSize = template.getHardware().getId();
      String templateStr = "{\n" +
            "  \"$schema\": \"https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#\",\n" +
            "  \"contentVersion\": \"1.0.0.0\",\n" +
            "  \"parameters\": {\n" +
            "    \"adminUsername\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"metadata\": {\n" +
            "        \"description\": \"User name for the Virtual Machine.\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"adminPassword\": {\n" +
            "      \"type\": \"securestring\",\n" +
            "      \"metadata\": {\n" +
            "        \"description\": \"Password for the Virtual Machine.\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"dnsLabelPrefix\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"metadata\": {\n" +
            "        \"description\": \"Unique DNS Name for the Public IP used to access the Virtual Machine.\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"vmLocation\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"metadata\": {\n" +
            "        \"description\": \"Location id.\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"OSVersion\": {\n" +
            "      \"type\": \"string\",\n" +
            "      \"metadata\": {\n" +
            "        \"description\": \"The OS version for the VM. \"\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"variables\": {\n" +
            "    \"storageAccountName\": \"" + name.replaceAll("[^A-Za-z0-9 ]", "") + "savm\",\n" +
            "    \"location\": \"[resourceGroup().location]\",\n" +
            "    \"dataDisk1VhdName\": \"datadisk1\",\n" +
            "    \"imagePublisher\": \"" + imageProvider +  "\",\n" +
            "    \"imageOffer\": \"" + imageOffer + "\",\n" +
            "    \"OSDiskName\": \"osdiskforvmsimple\",\n" +
            "    \"nicName\": \"myVMNic" + name + "\",\n" +
            "    \"addressPrefix\": \"10.0.0.0/16\",\n" +
            "    \"subnetName\": \"Subnet" + name + "\",\n" +
            "    \"subnetPrefix\": \"10.0.0.0/24\",\n" +
            "    \"storageAccountType\": \"Standard_LRS\",\n" +
            "    \"publicIPAddressName\": \"PublicIP" + name + "\",\n" +
            "    \"publicIPAddressType\": \"Dynamic\",\n" +
            "    \"vmStorageAccountContainerName\": \"vhds\",\n" +
            "    \"vmName\": \"" + name + "\",\n" +
            "    \"vmSize\": \"" + vmSize + "\",\n" +
            "    \"virtualNetworkName\": \"VNET" + name + "\",\n" +
            "    \"vnetID\": \"[resourceId('Microsoft.Network/virtualNetworks',variables('virtualNetworkName'))]\",\n" +
            "    \"subnetRef\": \"[concat(variables('vnetID'),'/subnets/',variables('subnetName'))]\",\n" +
            "    \"apiVersion\": \"2015-06-15\"\n" +
            "  },\n" +
            "  \"resources\": [\n" +
            "    {\n" +
            "      \"type\": \"Microsoft.Storage/storageAccounts\",\n" +
            "      \"name\": \"[variables('storageAccountName')]\",\n" +
            "      \"apiVersion\": \"[variables('apiVersion')]\",\n" +
            "      \"location\": \"[parameters('vmLocation')]\",\n" +
            "      \"properties\": {\n" +
            "        \"accountType\": \"[variables('storageAccountType')]\"\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"apiVersion\": \"[variables('apiVersion')]\",\n" +
            "      \"type\": \"Microsoft.Network/publicIPAddresses\",\n" +
            "      \"name\": \"[variables('publicIPAddressName')]\",\n" +
            "      \"location\": \"[parameters('vmLocation')]\",\n" +
            "      \"properties\": {\n" +
            "        \"publicIPAllocationMethod\": \"[variables('publicIPAddressType')]\",\n" +
            "        \"dnsSettings\": {\n" +
            "          \"domainNameLabel\": \"[parameters('dnsLabelPrefix')]\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"apiVersion\": \"[variables('apiVersion')]\",\n" +
            "      \"type\": \"Microsoft.Network/virtualNetworks\",\n" +
            "      \"name\": \"[variables('virtualNetworkName')]\",\n" +
            "      \"location\": \"[parameters('vmLocation')]\",\n" +
            "      \"properties\": {\n" +
            "        \"addressSpace\": {\n" +
            "          \"addressPrefixes\": [\n" +
            "            \"[variables('addressPrefix')]\"\n" +
            "          ]\n" +
            "        },\n" +
            "        \"subnets\": [\n" +
            "          {\n" +
            "            \"name\": \"[variables('subnetName')]\",\n" +
            "            \"properties\": {\n" +
            "              \"addressPrefix\": \"[variables('subnetPrefix')]\"\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"apiVersion\": \"[variables('apiVersion')]\",\n" +
            "      \"type\": \"Microsoft.Network/networkInterfaces\",\n" +
            "      \"name\": \"[variables('nicName')]\",\n" +
            "      \"location\": \"[parameters('vmLocation')]\",\n" +
            "      \"dependsOn\": [\n" +
            "        \"[concat('Microsoft.Network/publicIPAddresses/', variables('publicIPAddressName'))]\",\n" +
            "        \"[concat('Microsoft.Network/virtualNetworks/', variables('virtualNetworkName'))]\"\n" +
            "      ],\n" +
            "      \"properties\": {\n" +
            "        \"ipConfigurations\": [\n" +
            "          {\n" +
            "            \"name\": \"ipconfig1\",\n" +
            "            \"properties\": {\n" +
            "              \"privateIPAllocationMethod\": \"Dynamic\",\n" +
            "              \"publicIPAddress\": {\n" +
            "                \"id\": \"[resourceId('Microsoft.Network/publicIPAddresses',variables('publicIPAddressName'))]\"\n" +
            "              },\n" +
            "              \"subnet\": {\n" +
            "                \"id\": \"[variables('subnetRef')]\"\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        ]\n" +
            "      }\n" +
            "    },\n" +
            "    {\n" +
            "      \"apiVersion\": \"[variables('apiVersion')]\",\n" +
            "      \"type\": \"Microsoft.Compute/virtualMachines\",\n" +
            "      \"name\": \"[variables('vmName')]\",\n" +
            "      \"location\": \"[parameters('vmLocation')]\",\n" +
            "      \"dependsOn\": [\n" +
            "        \"[concat('Microsoft.Storage/storageAccounts/', variables('storageAccountName'))]\",\n" +
            "        \"[concat('Microsoft.Network/networkInterfaces/', variables('nicName'))]\"\n" +
            "      ],\n" +
            "      \"properties\": {\n" +
            "        \"hardwareProfile\": {\n" +
            "          \"vmSize\": \"[variables('vmSize')]\"\n" +
            "        },\n" +
            "        \"osProfile\": {\n" +
            "          \"computerName\": \"[variables('vmName')]\",\n" +
            "          \"adminUsername\": \"[parameters('adminUsername')]\",\n" +
            "          \"adminPassword\": \"[parameters('adminPassword')]\"\n" +
            "        },\n" +
            "        \"storageProfile\": {\n" +
            "          \"imageReference\": {\n" +
            "            \"publisher\": \"[variables('imagePublisher')]\",\n" +
            "            \"offer\": \"[variables('imageOffer')]\",\n" +
            "            \"sku\": \"[parameters('OSVersion')]\",\n" +
            "            \"version\": \"latest\"\n" +
            "          },\n" +
            "          \"osDisk\": {\n" +
            "            \"name\": \"osdisk\",\n" +
            "            \"vhd\": {\n" +
            "              \"uri\": \"[concat('http://',variables('storageAccountName'),'.blob.core.windows.net/',variables('vmStorageAccountContainerName'),'/',variables('OSDiskName'),'.vhd')]\"\n" +
            "            },\n" +
            "            \"caching\": \"ReadWrite\",\n" +
            "            \"createOption\": \"FromImage\"\n" +
            "          },\n" +
            "          \"dataDisks\": [\n" +
            "            {\n" +
            "              \"name\": \"datadisk1\",\n" +
            "              \"diskSizeGB\": \"100\",\n" +
            "              \"lun\": 0,\n" +
            "              \"vhd\": {\n" +
            "                \"uri\": \"[concat('http://',variables('storageAccountName'),'.blob.core.windows.net/',variables('vmStorageAccountContainerName'),'/',variables('dataDisk1VhdName'),'.vhd')]\"\n" +
            "              },\n" +
            "              \"createOption\": \"Empty\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"networkProfile\": {\n" +
            "          \"networkInterfaces\": [\n" +
            "            {\n" +
            "              \"id\": \"[resourceId('Microsoft.Network/networkInterfaces',variables('nicName'))]\"\n" +
            "            }\n" +
            "          ]\n" +
            "        },\n" +
            "        \"diagnosticsProfile\": {\n" +
            "          \"bootDiagnostics\": {\n" +
            "             \"enabled\": \"true\",\n" +
            "             \"storageUri\": \"[concat('http://',variables('storageAccountName'),'.blob.core.windows.net')]\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ],\n" +
            "  \"outputs\": {\n" +
            "     \"hostname\": {\n" +
            "         \"type\": \"string\",\n" +
            "         \"value\": \"[concat(parameters('dnsLabelPrefix'), '.', parameters('vmLocation'), '.cloudapp.azure.com')]\"\n" +
            "     },\n" +
            "     \"sshCommand\": {\n" +
            "         \"type\": \"string\",\n" +
            "         \"value\": \"[concat('ssh ', parameters('adminUsername'), '@', parameters('dnsLabelPrefix'), '.', parameters('vmLocation'), '.cloudapp.azure.com')]\"\n" +
            "     } \n" +
            "  }\n" +
            "}";
      return templateStr;
   }

   @Override
   public NodeAndInitialCredentials<Deployment> createNodeWithGroupEncodedIntoName(
           final String group, final String name, final Template template) {
      // azure-specific options
      final AzureComputeArmTemplateOptions options = template.getOptions().as(AzureComputeArmTemplateOptions.class);

      final String loginUser = options.getLoginUser() == null ? DEFAULT_LOGIN_USER : options.getLoginUser();
      final String loginPassword = options.getLoginPassword() == null ? DEFAULT_LOGIN_PASSWORD : options.getLoginPassword();
      final String location = template.getLocation().getId();
      final String osVersion = template.getImage().getDescription();

      logger.info("Deployment created with name: %s", name);

      final Set<Deployment> deployments = Sets.newHashSet();
      if (!retry(new Predicate<String>() {
         @Override
         public boolean apply(final String name) {
            Long now = System.currentTimeMillis() + runningNumber;
            runningNumber++;
            String parameters = "{" +
                    "    \"adminUsername\": {\n" +
                    "      \"value\": \"" + loginUser + "\"\n" +
                    "    },\n" +
                    "    \"adminPassword\": {\n" +
                    "      \"value\": \"" + loginPassword + "\"\n" +
                    "    },\n" +
                    "    \"dnsLabelPrefix\": {\n" +
                    "      \"value\": \"" + name.replaceAll("[^A-Za-z0-9 ]", "") + "\"\n" +
                    "    },\n" +
                    "    \"vmLocation\": {\n" +
                    "      \"value\": \"" + location + "\"\n" +
                    "    },\n" +
                    "    \"OSVersion\": {\n" +
                    "      \"value\": \"" + osVersion + "\"\n" +
                    "    }\n}";
            String properties = getPutBody(getTemplate(name, template), "Incremental", parameters);
            HashMap<String, String> tags = new HashMap<String, String>();
            tags.put("tagname1", "tagvalue1");

            String resourceGroup = api.getResourceGroupApi().create(getGroupId(), "westus", tags).name();
            Deployment deployment = api.getDeploymentApi(getGroupId()).createDeployment(name, properties);

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

   @Override
   public Deployment getNode(final String id) {
      Deployment deployment = api.getDeploymentApi(getGroupId()).getDeployment(id);

      return deployment;
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
      getNode(id);
   }

   @Override
   public void suspendNode(final String id) {
   }

   @Override
   public Iterable<Deployment> listNodes() {

      return FluentIterable.from(api.getVirtualMachineApi(getGroupId()).list()).
              transform(new Function<VirtualMachine, Deployment>() {
                 @Override
                 public Deployment apply(final VirtualMachine vm) {
                    return api.getDeploymentApi(getGroupId()).getDeployment(vm.name());
                 }
              }).
              filter(notNull()).
              toSet();
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

   private String getGroupId() {
      String group =  System.getProperty("test.azurecompute-arm.groupname");
      if (group == null)
         group = "jCloudsGroup1";
      return group;
   }

}
