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
package org.jclouds.azurecomputearm;

import java.io.Closeable;

import javax.ws.rs.PathParam;

import org.jclouds.azurecomputearm.features.DeploymentApi;
import org.jclouds.azurecomputearm.features.LocationApi;
import org.jclouds.azurecomputearm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecomputearm.features.OSImageApi;
import org.jclouds.azurecomputearm.features.ResourceGroupApi;
import org.jclouds.azurecomputearm.features.StorageAccountApi;
import org.jclouds.azurecomputearm.features.VirtualMachineApi;
import org.jclouds.azurecomputearm.features.VirtualNetworkApi;
import org.jclouds.azurecomputearm.features.VMSizeApi;
import org.jclouds.azurecomputearm.features.SubnetApi;
import org.jclouds.azurecomputearm.features.PublicIPAddressApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * The Azure Resource Manager API is a REST API for managing your services and deployments.
 * <p/>
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
   ResourceGroupApi getResourceGroupApi(@PathParam("subscriptionid") String subscriptionid);


   /**
    * This Azure Resource Manager API provides all of the locations that are available for resource providers
    *
    * @see <a href="https://msdn.microsoft.com/en-US/library/azure/dn790540.aspx">docs</a>
    */
   @Delegate
   LocationApi getLocationApi(@PathParam("subscriptionid") String subscriptionid);

   /**
    * This Azure Resource Manager API lists all available virtual machine sizes for a subscription in a given region
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt269440.aspx">docs</a>
    */
   @Delegate
   VMSizeApi getVMSizeApi(@PathParam("subscriptionid") String subscriptionid, @PathParam("location") String location);


   /**
    * The Virtual Machine API includes operations for managing the virtual machines in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163630.aspx">docs</a>
    */
   @Delegate
   VirtualMachineApi getVirtualMachineApi(@PathParam("subscriptionId") String subscriptionId,
                                          @PathParam("resourceGroup") String resourceGroup);

   /**
    * The Azure Resource Manager API gets all the OS images in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157175">docs</a>
    */
   @Delegate
   OSImageApi getOSImageApi(@PathParam("subscriptionId") String subscriptionId,
                            @PathParam("location") String location);


   /**
    * The Subnet API includes operations for managing the subnets in your virtual network.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163621.aspx">docs</a>
    */
   @Delegate
   SubnetApi getSubnetApi(@PathParam("subscriptionid") String subscriptionid,
                          @PathParam("resourcegroup") String resourcegroup,
                          @PathParam("virtualnetwork") String virtualnetwork);

   /**
    * The Virtual Network API includes operations for managing the virtual networks in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163661.aspx">docs</a>
    */
   @Delegate
   VirtualNetworkApi getVirtualNetworkApi(@PathParam("subscriptionid") String subscriptionid,
                                          @PathParam("resourcegroup") String resourcegroup);


   /**
    * The Network Interface Card API includes operations for managing the NICs in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/mt163668.aspx">docs</a>
    */
   @Delegate
   NetworkInterfaceCardApi getNetworkInterfaceCardApi(@PathParam("subscriptionid") String subscriptionid,
                                                      @PathParam("resourcegroup") String resourcegroup);

   /**
    * The Public IP Address API includes operations for managing public ID Addresses for NICs in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163638.aspx">docs</a>
    */
   @Delegate
   PublicIPAddressApi getPublicIPAddressApi(@PathParam("subscriptionid") String subscriptionid,
                                         @PathParam("resourcegroup") String resourcegroup);

   /**
    * The Azure Resource Manager API includes operations for managing the storage accounts in your subscription.
    *
    * @see <https://msdn.microsoft.com/en-us/library/mt163683.aspx">docs</a>
    */
   @Delegate
   StorageAccountApi getStorageAccountApi(@PathParam("subscriptionId") String subscriptionId,
                                          @PathParam("resourceGroup") String resourceGroup);

   /**
    * The Deployment API allows for the management of Azure Resource Manager resources through the use of templates.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn790549.aspx">docs</a>
    */
   @Delegate
   DeploymentApi getDeploymentApi(@PathParam("subscriptionid") String subscriptionId,
                                  @PathParam("resourcegroup") String resourceGroup);
}
