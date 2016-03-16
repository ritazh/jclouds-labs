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

import org.jclouds.azurecomputearm.features.AffinityGroupApi;
import org.jclouds.azurecomputearm.features.CloudServiceApi;
import org.jclouds.azurecomputearm.features.DeploymentApi;
import org.jclouds.azurecomputearm.features.DiskApi;
import org.jclouds.azurecomputearm.features.LocationApi;
import org.jclouds.azurecomputearm.features.NetworkInterfaceCardApi;
import org.jclouds.azurecomputearm.features.NetworkSecurityGroupApi;
import org.jclouds.azurecomputearm.features.OSImageApi;
import org.jclouds.azurecomputearm.features.OperationApi;
import org.jclouds.azurecomputearm.features.ReservedIPAddressApi;
import org.jclouds.azurecomputearm.features.ResourceGroupApi;
import org.jclouds.azurecomputearm.features.ServiceCertificatesApi;
import org.jclouds.azurecomputearm.features.StorageAccountApi;
import org.jclouds.azurecomputearm.features.SubscriptionApi;
import org.jclouds.azurecomputearm.features.TrafficManagerApi;
import org.jclouds.azurecomputearm.features.VMImageApi;
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

   @Delegate
   AffinityGroupApi getAffinityGroupApi();

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
    * The Service Management API includes operations for managing the cloud services beneath your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460812">docs</a>
    */
   @Delegate
   CloudServiceApi getCloudServiceApi();

   /**
    * The Service Management API includes operations for managing the virtual machines in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157206">docs</a>
    */
   @Delegate
   DeploymentApi getDeploymentApiForService(@PathParam("serviceName") String serviceName);

   /**
    * The Virtual Machine API includes operations for managing the virtual machines in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/mt163630.aspx">docs</a>
    */
   @Delegate
   VirtualMachineApi getVirtualMachineApi(@PathParam("subscriptionId") String subscriptionId,
                                          @PathParam("resourceGroup") String resourceGroup);

   /**
    * The Service Management API includes operations for managing the OS images in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157175">docs</a>
    */
   @Delegate
   OSImageApi getOSImageApi();

   /**
    * The Service Management API includes operations for Tracking Asynchronous Service Management Requests.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/ee460791">docs</a>
    */
   @Delegate
   OperationApi getOperationApi();

   /**
    * The Service Management API includes operations for managing Disks in your subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/jj157188">docs</a>
    */
   @Delegate
   DiskApi getDiskApi();

   /**
    * The Service Management API includes operations for retrieving information about a subscription.
    *
    * @see <a href="http://msdn.microsoft.com/en-us/library/azure/gg715315.aspx">docs</a>
    */
   @Delegate
   SubscriptionApi getSubscriptionApi();

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
    * The Service Management API includes operations for managing the storage accounts in your subscription.
    *
    * @see <https://msdn.microsoft.com/en-us/library/mt163683.aspx">docs</a>
    */
   @Delegate
   StorageAccountApi getStorageAccountApi(@PathParam("subscriptionId") String subscriptionId,
                                          @PathParam("resourceGroup") String resourceGroup);

   /**
    * The Service Management API includes operations for managing the Network Security Groups in your subscription.
    *
    */
   @Delegate
   NetworkSecurityGroupApi getNetworkSecurityGroupApi();

   /**
    * The Service Management API includes operations for creating, updating, listing, and deleting Azure Traffic Manager
    * profiles and definitions.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/hh758255.aspx">docs</a>
    */
   @Delegate
   TrafficManagerApi getTrafficManaerApi();

   /**
    * The Service Management API includes operations for managing service certificates in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ee795178.aspx">docs</a>
    */
   @Delegate
   ServiceCertificatesApi getServiceCertificatesApi();

   /**
    * The Service Management API includes operations for managing the reserved IP addresses in your subscription.
    *
    * @see <a href="https://msdn.microsoft.com/en-us/library/azure/dn722420.aspxx">docs</a>
    */
   @Delegate
   ReservedIPAddressApi getReservedIPAddressApi();
   /*
   * The Service Management API includes operations for managing the VM Images in your subscription.
   */
   @Delegate
   VMImageApi getVMImageApi();
}
