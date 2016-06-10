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
package org.jclouds.azurecompute.arm;

import org.jclouds.azurecompute.arm.features.DeploymentApi;
import org.jclouds.azurecompute.arm.util.DeploymentTemplateBuilder;
import org.jclouds.azurecompute.arm.features.JobApi;
import org.jclouds.azurecompute.arm.features.LocationApi;
import org.jclouds.azurecompute.arm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecompute.arm.features.OSImageApi;
import org.jclouds.azurecompute.arm.features.PublicIPAddressApi;
import org.jclouds.azurecompute.arm.features.ResourceGroupApi;
import org.jclouds.azurecompute.arm.features.ResourceProviderApi;
import org.jclouds.azurecompute.arm.features.StorageAccountApi;
import org.jclouds.azurecompute.arm.features.SubnetApi;
import org.jclouds.azurecompute.arm.features.VirtualMachineApi;
import org.jclouds.azurecompute.arm.features.VirtualNetworkApi;
import org.jclouds.azurecompute.arm.features.VMSizeApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityGroupApi;
import org.jclouds.azurecompute.arm.features.NetworkSecurityRuleApi;
import org.jclouds.rest.annotations.Delegate;

import com.google.inject.Provides;
import javax.ws.rs.PathParam;
import java.io.Closeable;

/**
 * The Azure Resource Manager API is a REST API for managing your services and deployments.
 * <p>
 *
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790568.aspx" >doc</a>
 */
public interface AzureComputeApi extends Closeable {

   /**
    * The Azure Resource Manager API includes operations for managing resource groups in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790546.aspx">docs</a>
    */
   @Delegate
   ResourceGroupApi getResourceGroupApi();

   @Delegate
   JobApi getJobApi();

   /**
    * This Azure Resource Manager API provides all of the locations that are available for resource providers
    *
    * @see <a href="https://msdn.microsoft.com/en-US/library/azure/dn790540.aspx">docs</a>
    */
   @Delegate
   LocationApi getLocationApi();

   /**
    * The Azure Resource Manager API includes operations for managing the storage accounts in your subscription.
    *
    * @see <https://msdn.microsoft.com/en-us/library/mt163683.aspx">docs</a>
    */
   @Delegate
   StorageAccountApi getStorageAccountApi(@PathParam("resourceGroup") String resourceGroup);

   /**
    * The Subnet API includes operations for managing the subnets in your virtual network.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163621.aspx">docs</a>
    */
   @Delegate
   SubnetApi getSubnetApi(@PathParam("resourcegroup") String resourcegroup,
                          @PathParam("virtualnetwork") String virtualnetwork);

   /**
    * The Virtual Network API includes operations for managing the virtual networks in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163661.aspx">docs</a>
    */
   @Delegate
   VirtualNetworkApi getVirtualNetworkApi(@PathParam("resourcegroup") String resourcegroup);


   /**
    * The Network Interface Card API includes operations for managing the NICs in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/mt163668.aspx">docs</a>
    */
   @Delegate
   NetworkInterfaceCardApi getNetworkInterfaceCardApi(@PathParam("resourcegroup") String resourcegroup);

   /**
    * The Public IP Address API includes operations for managing public ID Addresses for NICs in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163638.aspx">docs</a>
    */
   @Delegate
   PublicIPAddressApi getPublicIPAddressApi(@PathParam("resourcegroup") String resourcegroup);


   /**
    * The Virtual Machine API includes operations for managing the virtual machines in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163630.aspx">docs</a>
    */
   @Delegate
   VirtualMachineApi getVirtualMachineApi(@PathParam("resourceGroup") String resourceGroup);

   /**
    * This Azure Resource Manager API lists all available virtual machine sizes for a subscription in a given region
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt269440.aspx">docs</a>
    */
   @Delegate
   VMSizeApi getVMSizeApi(@PathParam("location") String location);

   /**
    * The Azure Resource Manager API gets all the OS images in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157175">docs</a>
    */
   @Delegate
   OSImageApi getOSImageApi(@PathParam("location") String location);

   /**
    * The Deployment API allows for the management of Azure Resource Manager resources through the use of templates.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790549.aspx">docs</a>
    */
   @Delegate
   DeploymentApi getDeploymentApi(@PathParam("resourcegroup") String resourceGroup);

   /**
    * The NetworkSecurityGroup API includes operations for managing network security groups within your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163615.aspx">docs</a>
    */
   @Delegate
   NetworkSecurityGroupApi getNetworkSecurityGroupApi(@PathParam("resourcegroup") String resourcegroup);
 
   /**
    * The NetworkSecurityRule API includes operations for managing network security rules within a network security group.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163580.aspx">docs</a>
    */
   @Delegate
   NetworkSecurityRuleApi getNetworkSecurityRuleApi(@PathParam("resourcegroup") String resourcegroup,
                                                    @PathParam("networksecuritygroup") String networksecuritygroup);

   /**
    * The Azure Resource Provider API provides information about a resource provider and its supported resource types.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790534.aspx">docs</a>
    */
   @Delegate
   ResourceProviderApi getResourceProviderApi();

   @Provides
   DeploymentTemplateBuilder.Factory deploymentTemplateFactory();
}
