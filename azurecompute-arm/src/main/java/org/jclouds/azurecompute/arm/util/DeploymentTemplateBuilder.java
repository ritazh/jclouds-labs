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
package org.jclouds.azurecompute.arm.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.assistedinject.Assisted;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import org.jclouds.azurecompute.arm.compute.config.AzureComputeServiceContextModule;
import org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension;
import org.jclouds.azurecompute.arm.compute.options.AzureTemplateOptions;
import org.jclouds.azurecompute.arm.domain.DataDisk;
import org.jclouds.azurecompute.arm.domain.DeploymentBody;
import org.jclouds.azurecompute.arm.domain.DeploymentProperties;
import org.jclouds.azurecompute.arm.domain.DeploymentTemplate;
import org.jclouds.azurecompute.arm.domain.DiagnosticsProfile;
import org.jclouds.azurecompute.arm.domain.DnsSettings;
import org.jclouds.azurecompute.arm.domain.HardwareProfile;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.IpConfiguration;
import org.jclouds.azurecompute.arm.domain.IpConfigurationProperties;
import org.jclouds.azurecompute.arm.domain.KeyVaultReference;
import org.jclouds.azurecompute.arm.domain.NetworkInterfaceCardProperties;
import org.jclouds.azurecompute.arm.domain.NetworkProfile;
import org.jclouds.azurecompute.arm.domain.OSDisk;
import org.jclouds.azurecompute.arm.domain.OSProfile;
import org.jclouds.azurecompute.arm.domain.PublicIPAddressProperties;
import org.jclouds.azurecompute.arm.domain.StorageProfile;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.TemplateParameterType;
import org.jclouds.azurecompute.arm.domain.VHD;
import org.jclouds.azurecompute.arm.domain.VirtualMachineProperties;
import org.jclouds.azurecompute.arm.domain.StorageService.StorageServiceProperties;
import org.jclouds.azurecompute.arm.domain.IdReference;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityGroupProperties;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRule;
import org.jclouds.azurecompute.arm.domain.NetworkSecurityRuleProperties;
import org.jclouds.compute.domain.Template;
import org.jclouds.json.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.io.BaseEncoding.base64;
import com.google.inject.Inject;

import static org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension.CUSTOM_IMAGE_PREFIX;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.STORAGE_API_VERSION;

public class DeploymentTemplateBuilder {
   public interface Factory {
      DeploymentTemplateBuilder create(@Assisted("group") String group, @Assisted("name") String name, Template template);
   }

   private final String name;
   private final String azureGroup;
   private final String group;
   private final Template template;
   private final Json json;

   private AzureTemplateOptions options;
   private Iterable<String> tags;
   private Map<String, String> userMetaData;
   private List<ResourceDefinition> resources;
   private Map<String, String> variables;
   private static String loginUser;
   private static String loginPassword;
   private String location;
   private AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants;

   private static final String DEPLOYMENT_MODE = "Incremental";

   @Inject
   DeploymentTemplateBuilder(Json json, @Assisted("group") String group, @Assisted("name") String name, @Assisted Template template,
                             final AzureComputeServiceContextModule.AzureComputeConstants azureComputeConstants) {
      this.name = name;
      this.group = group;
      this.template = template;
      this.options = template.getOptions().as(AzureTemplateOptions.class);
      this.tags = template.getOptions().getTags();
      this.userMetaData = template.getOptions().getUserMetadata();
      this.variables = new HashMap<String, String>();
      this.resources = new ArrayList<ResourceDefinition>();
      this.location = template.getLocation().getId();
      this.json = json;

      this.azureComputeConstants = azureComputeConstants;
      this.azureGroup = this.azureComputeConstants.azureResourceGroup();

      String[] defaultLogin = this.azureComputeConstants.azureDefaultImageLogin().split(":");
      String defaultUser = null;
      String defaultPassword = null;

      if (defaultLogin.length == 2) {
         defaultUser = defaultLogin[0].trim();
         defaultPassword = defaultLogin[1].trim();
      }

      loginUser = options.getLoginUser() == null ? defaultUser : options.getLoginUser();
      loginPassword = options.getLoginPassword() == null ? defaultPassword : options.getLoginPassword();
   }

   public static String getLoginUserUsername() {
      return loginUser;
   }

   public static String getLoginPassword() {
      return loginPassword;
   }

   public Template getTemplate() {
      return template;
   }

   public DeploymentBody getDeploymentTemplate() {

      addStorageResource();
      addPublicIpAddress();
      addNetworkSecurityGroup();
      addNetworkInterfaceCard();
      addVirtualMachine();


      DeploymentTemplate.TemplateParameters templateParameters = null;
      DeploymentTemplate.Parameters parameters = null;

      if (keyVaultInUse()){
         String[] keyVaultInfo = options.getKeyVaultIdAndSecret().split(":");
         Preconditions.checkArgument(keyVaultInfo.length == 2);
         String vaultId = keyVaultInfo[0].trim();
         String secretName = keyVaultInfo[1].trim();

         templateParameters = DeploymentTemplate.TemplateParameters.create(TemplateParameterType.create("securestring"));
         parameters = DeploymentTemplate.Parameters.create(KeyVaultReference.create(KeyVaultReference.Reference.create(IdReference.create(vaultId), secretName)));
      } else {
         templateParameters = DeploymentTemplate.TemplateParameters.create(null);
         parameters = DeploymentTemplate.Parameters.create(null);
      }


      DeploymentTemplate template = DeploymentTemplate.builder()
              .schema("https://schema.management.azure.com/schemas/2015-01-01/deploymentTemplate.json#")
              .contentVersion("1.0.0.0")
              .resources(resources)
              .variables(variables)
              .parameters(templateParameters)
              .build();

      DeploymentBody body = DeploymentBody.create(template, DEPLOYMENT_MODE, parameters);

      return body;
   }

   public String getDeploymentTemplateJson(DeploymentProperties properties) {
      return json.toJson(properties);
   }

   private void addStorageResource() {
      String storageAccountName = name.replaceAll("[^A-Za-z0-9 ]", "") + "stor";

      String storageName = template.getImage().getName();
      if (storageName.startsWith(CUSTOM_IMAGE_PREFIX)) {
         storageAccountName = storageName.substring(CUSTOM_IMAGE_PREFIX.length()); // get group name
      }

      variables.put("storageAccountName", storageAccountName);

      ResourceDefinition storageAccount = ResourceDefinition.builder()
              .name("[variables('storageAccountName')]")
              .type("Microsoft.Storage/storageAccounts")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .properties(
                      StorageServiceProperties.builder()
                              .accountType(StorageService.AccountType.Standard_LRS)
                              .build()
              )
              .build();

      resources.add(storageAccount);
   }

   private void addPublicIpAddress() {
      String publicIPAddressName = name + "publicip";
      String dnsLabelPrefix = options.getDNSLabelPrefix();

      PublicIPAddressProperties.Builder properties = PublicIPAddressProperties.builder();

      if (!Strings.isNullOrEmpty(dnsLabelPrefix)) {
         properties.dnsSettings(DnsSettings.builder().domainNameLabel(dnsLabelPrefix).build());
         variables.put("dnsLabelPrefix", dnsLabelPrefix);
      }

      properties.publicIPAllocationMethod("Dynamic");
      variables.put("publicIPAddressName", publicIPAddressName);
      variables.put("publicIPAddressReference", "[resourceId('Microsoft.Network/publicIPAddresses',variables('publicIPAddressName'))]");

      ResourceDefinition publicIpAddress = ResourceDefinition.builder()
              .name("[variables('publicIPAddressName')]")
              .type("Microsoft.Network/publicIPAddresses")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .properties(properties.build())
              .build();

      resources.add(publicIpAddress);
   }

   private void addNetworkInterfaceCard() {

      List<IpConfiguration> ipConfigurations = new ArrayList<IpConfiguration>();

      String ipConfigurationName = name + "ipconfig";
      String subnetId = options.getSubnetId();
      String vnetName = options.getVirtualNetworkName();

      variables.put("ipConfigurationName", ipConfigurationName);
      variables.put("subnetReference", subnetId);

      IpConfiguration ipConfig = IpConfiguration.create(ipConfigurationName, null, null, null,
              IpConfigurationProperties.builder()
                      .privateIPAllocationMethod("Dynamic")
                      .publicIPAddress(IdReference.create("[variables('publicIPAddressReference')]"))
                      .subnet(IdReference.create("[variables('subnetReference')]"))
                      .build());

      ipConfigurations.add(ipConfig);

      // Check to see if we have defined a network security group
      IdReference networkSecurityGroup = null;
      int ports[] = options.getInboundPorts();
      if ((ports != null) && (ports.length > 0)) {
         networkSecurityGroup = IdReference.create("[variables('networkSecurityGroupNameReference')]");
      }

      ArrayList<String> depends = new ArrayList<String>(Arrays.asList("[concat('Microsoft.Network/publicIPAddresses/', variables('publicIPAddressName'))]"));

      NetworkInterfaceCardProperties.Builder networkInterfaceCardPropertiesBuilder = NetworkInterfaceCardProperties.builder();
      networkInterfaceCardPropertiesBuilder.ipConfigurations(ipConfigurations);
      if (networkSecurityGroup != null) {
         networkInterfaceCardPropertiesBuilder.networkSecurityGroup(networkSecurityGroup);
         depends.add("[concat('Microsoft.Network/networkSecurityGroups/', variables('networkSecurityGroupName'))]");
      }
      NetworkInterfaceCardProperties networkInterfaceCardProperties = networkInterfaceCardPropertiesBuilder.build();

      String networkInterfaceCardName = name + "nic";
      variables.put("networkInterfaceCardName", networkInterfaceCardName);
      variables.put("networkInterfaceCardReference", "[resourceId('Microsoft.Network/networkInterfaces',variables('networkInterfaceCardName'))]");

      ResourceDefinition networkInterfaceCard = ResourceDefinition.builder()
              .name("[variables('networkInterfaceCardName')]")
              .type("Microsoft.Network/networkInterfaces")
              .location(location)
              .apiVersion(STORAGE_API_VERSION)
              .dependsOn(depends)
              .properties(networkInterfaceCardProperties)
              .build();

      resources.add(networkInterfaceCard);
   }

   private void addNetworkSecurityGroup() {
      int ports[] = options.getInboundPorts();
      if ((ports != null) && (ports.length > 0)) {
         variables.put("networkSecurityGroupName", name + "nsg");
         variables.put("networkSecurityGroupNameReference", "[resourceId('Microsoft.Network/networkSecurityGroups',variables('networkSecurityGroupName'))]");

         List<NetworkSecurityRule> rules = new ArrayList<NetworkSecurityRule>();
         for (int i = 0; i < ports.length; i++) {
            NetworkSecurityRuleProperties ruleProperties = NetworkSecurityRuleProperties.builder()
                    .description("default-allow-port-" + ports[i])
                    .protocol(NetworkSecurityRuleProperties.Protocol.All)
                    .access(NetworkSecurityRuleProperties.Access.Allow)
                    .sourcePortRange("*")
                    .destinationPortRange(Integer.toString(ports[i]))
                    .sourceAddressPrefix("*")
                    .destinationAddressPrefix("*")
                    .priority(1234 + i)
                    .direction(NetworkSecurityRuleProperties.Direction.Inbound)
                    .build();

            NetworkSecurityRule networkSecurityRule = NetworkSecurityRule.create(
                    "default-allow-port-" + ports[i],
                    null,
                    null,
                    ruleProperties);

            rules.add(networkSecurityRule);
         }

         NetworkSecurityGroupProperties networkSecurityGroupProperties = NetworkSecurityGroupProperties.builder()
                 .securityRules(rules)
                 .build();

         ResourceDefinition networkSecurityGroup = ResourceDefinition.builder()
                 .name("[variables('networkSecurityGroupName')]")
                 .type("Microsoft.Network/networkSecurityGroups").location(location)
                 .apiVersion(STORAGE_API_VERSION)
                 .properties(networkSecurityGroupProperties)
                 .build();
         resources.add(networkSecurityGroup);
      }

   }

   private void addVirtualMachine() {
      //Build OS Profile
      final String computerName = name + "pc";

      variables.put("loginUser", loginUser);
      OSProfile.Builder profileBuilder = OSProfile.builder()
              .adminUsername(loginUser)
              .computerName(computerName);

      profileBuilder.adminPassword(loginPassword);
      //boolean usePublicKey = options.getPublicKey() != null;

      if (keyVaultInUse()) {
         OSProfile.LinuxConfiguration configuration = OSProfile.LinuxConfiguration.create("false",
                 OSProfile.LinuxConfiguration.SSH.create(Arrays.asList(
                         OSProfile.LinuxConfiguration.SSH.SSHPublicKey.create(
                                 "[concat('/home/',variables('loginUser'),'/.ssh/authorized_keys')]",
                                 "[parameters('publicKeyFromAzureKeyVault')]"
                         ))
                 ));
         profileBuilder.linuxConfiguration(configuration);
      }

      if (!Strings.isNullOrEmpty(options.getCustomData())){
         String encodedCustomData = base64().encode(options.getCustomData().getBytes());
         profileBuilder.customData(encodedCustomData);
      }

      OSProfile osProfile = profileBuilder.build();

      //Build OsDisk
      final String storageAccountContainerName = name + "vhds";
      variables.put("storageAccountContainerName", storageAccountContainerName);

      final String osDiskName = name + "osdisk";
      variables.put("osDiskName", osDiskName);

      boolean usingMarketplaceImage = true;
      String cusotomImageUri = "";

      // TODO: make new fields for group information
      String publisher = template.getImage().getProviderId();
      String storageName = template.getImage().getName();
      String sku = template.getImage().getDescription(); // this is actual VHD
      if (storageName.startsWith(CUSTOM_IMAGE_PREFIX)) {
         storageName = storageName.substring(CUSTOM_IMAGE_PREFIX.length()); // get group name
         cusotomImageUri = sku;
         cusotomImageUri = "https://" + storageName + ".blob.core.windows.net/system/Microsoft.Compute/Images/" + AzureComputeImageExtension.CONTAINER_NAME + "/" + cusotomImageUri;
      }

      if (!cusotomImageUri.isEmpty()) {
         usingMarketplaceImage = false;
      }

      OSDisk osDisk = getOsDisk("[concat('http://',variables('storageAccountName'),'.blob.core.windows.net/',variables('storageAccountContainerName'),'/',variables('osDiskName'),'.vhd')]", cusotomImageUri);

      //Create Data Disk(s) and add to list
      final String dataDiskName = name + "datadisk";
      variables.put("dataDiskName", dataDiskName);

      List<DataDisk> dataDisks = new ArrayList<DataDisk>();
      DataDisk dataDisk = DataDisk.builder()
              .name("[variables('dataDiskName')]")
              .diskSizeGB(azureComputeConstants.azureDefaultDataDiskSizeProperty())
              .lun(0)
              .vhd(
                      VHD.create("[concat('http://',variables('storageAccountName'),'.blob.core.windows.net/',variables('storageAccountContainerName'),'/',variables('dataDiskName'),'.vhd')]")
              )
              .createOption("Empty")
              .build();

      dataDisks.add(dataDisk);

      //Create Storage Profile
      StorageProfile.Builder storageProfileBuilder = StorageProfile.builder()
              .osDisk(osDisk)
              .dataDisks(dataDisks);

      if (usingMarketplaceImage) {
         //Build Image Reference if marketplace image is used
         ImageReference imageReference = getImageReference(template.getImage().getProviderId(),
                 template.getImage().getName(),
                 template.getImage().getVersion());

         storageProfileBuilder.imageReference(imageReference);
      }
      StorageProfile storageProfile = storageProfileBuilder.build();


      //Create Network Profile for this VM (links to network interface cards)
      NetworkProfile networkProfile = NetworkProfile.create(
              Arrays.asList(
                      IdReference.create("[variables('networkInterfaceCardReference')]")
              ));

      //Boot Diagnostics
      DiagnosticsProfile diagnosticsProfile = DiagnosticsProfile.create(
              DiagnosticsProfile.BootDiagnostics.builder()
                      .enabled(true)
                      .storageUri("[concat('http://',variables('storageAccountName'),'.blob.core.windows.net')]")
                      .build());

      //Build VirtualMachine properties based on above properties.
      final String vmSize = template.getHardware().getId();
      HardwareProfile hw = HardwareProfile.create(vmSize);

      VirtualMachineProperties properties = VirtualMachineProperties.builder()
              .hardwareProfile(hw)
              .osProfile(osProfile)
              .storageProfile(storageProfile)
              .networkProfile(networkProfile)
              .diagnosticsProfile(diagnosticsProfile)
              .build();


      String tagString = Joiner.on(",").join(Lists.newArrayList(tags));
      if (tagString.isEmpty())
         tagString = "jclouds";
      userMetaData.put("tags", tagString);

      variables.put("virtualMachineName", name);
      ResourceDefinition virtualMachine = ResourceDefinition.builder()
              .name("[variables('virtualMachineName')]")
              .type("Microsoft.Compute/virtualMachines")
              .location(location)
              .apiVersion("2015-06-15")
              .dependsOn(Arrays.asList("[concat('Microsoft.Storage/storageAccounts/', variables('storageAccountName'))]",
                      "[concat('Microsoft.Network/networkInterfaces/', variables('networkInterfaceCardName'))]"))
              .tags(userMetaData)
              .properties(properties)
              .build();

      resources.add(virtualMachine);
   }


   private ImageReference getImageReference(String publisher, String offer, String sku) {
      return ImageReference.builder()
              .publisher(publisher)
              .offer(offer)
              .sku(sku)
              .version("latest")
              .build();

   }

   private OSDisk getOsDisk(String vhdUri, String imageUri) {
      OSDisk.Builder builder = OSDisk.builder();
      builder.name("[variables('osDiskName')]");
      builder.caching("ReadWrite");
      builder.createOption("FromImage");
      builder.vhd(VHD.create(vhdUri));

      if (!imageUri.isEmpty()) {
         builder.osType("Linux");
         builder.image(VHD.create(imageUri));
      }
      return builder.build();
   }

   private boolean keyVaultInUse(){
      return !Strings.isNullOrEmpty(options.getKeyVaultIdAndSecret());
   }

}
