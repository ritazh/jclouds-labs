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
package org.jclouds.azurecomputearm.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.azurecomputearm.AzureComputeApi;
import org.jclouds.azurecomputearm.domain.ComputeNode;
import org.jclouds.azurecomputearm.domain.Deployment;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;

public class DeploymentToNodeMetadata implements Function<Deployment, NodeMetadata> {

   private static final Map<ComputeNode.Status, NodeMetadata.Status> INSTANCESTATUS_TO_NODESTATUS =
           ImmutableMap.<ComputeNode.Status, NodeMetadata.Status>builder().
                   put(ComputeNode.Status.GOOD, NodeMetadata.Status.RUNNING).
                   put(ComputeNode.Status.BAD, NodeMetadata.Status.ERROR).
                   put(ComputeNode.Status.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).
                   build();

   // When using the Deployment API to deploy an ARM template, the deployment goes through
   // stages.  Accepted -> Running -> Succeeded.  Only when the deployment has SUCCEEDED is
   // the resource deployed using the template actually ready.
   //
   // To get details about the resource(s) deployed via template, one needs to query the
   // various resources after the deployment has "SUCCEEDED".
   private static final Map<Deployment.ProvisioningState, NodeMetadata.Status> STATUS_TO_NODESTATUS =
           ImmutableMap.<Deployment.ProvisioningState, NodeMetadata.Status>builder().
                   put(Deployment.ProvisioningState.ACCEPTED, NodeMetadata.Status.PENDING).
                   put(Deployment.ProvisioningState.READY, NodeMetadata.Status.PENDING).
                   put(Deployment.ProvisioningState.RUNNING, NodeMetadata.Status.PENDING).
                   put(Deployment.ProvisioningState.CANCELED, NodeMetadata.Status.TERMINATED).
                   put(Deployment.ProvisioningState.FAILED, NodeMetadata.Status.ERROR).
                   put(Deployment.ProvisioningState.DELETED, NodeMetadata.Status.TERMINATED).
                   put(Deployment.ProvisioningState.SUCCEEDED, NodeMetadata.Status.RUNNING).
                   put(Deployment.ProvisioningState.UNRECOGNIZED, NodeMetadata.Status.UNRECOGNIZED).
                   build();

   private final AzureComputeApi api;

   private final Supplier<Set<? extends Location>> locations;

   private final GroupNamingConvention nodeNamingConvention;

   private final ImageReferenceToImage imageReferenceToImage;

   private final VMSizeToHardware vmSizeToHardware;

   private final Map<String, Credentials> credentialStore;

   @Inject
   DeploymentToNodeMetadata(
           AzureComputeApi api,
           @Memoized Supplier<Set<? extends Location>> locations,
           GroupNamingConvention.Factory namingConvention, ImageReferenceToImage imageReferenceToImage,
           VMSizeToHardware vmSizeToHardware, Map<String, Credentials> credentialStore) {

      this.nodeNamingConvention = namingConvention.createWithoutPrefix();
      this.locations = checkNotNull(locations, "locations");
      this.imageReferenceToImage = imageReferenceToImage;
      this.vmSizeToHardware = vmSizeToHardware;
      this.credentialStore = credentialStore;
      this.api = api;
   }

   @Override
   public NodeMetadata apply(final Deployment from) {
      final NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.id(from.name());
      builder.providerId(from.name());
      builder.name(from.name());
      //builder.hostname(getHostname(from));
      //builder.group(nodeNamingConvention.groupInUniqueNameOrNull(getHostname(from)));

      /* TODO
       if (from.getDatacenter() != null) {
       builder.location(from(locations.get()).firstMatch(
       LocationPredicates.idEquals(from.getDatacenter().getId() + "")).orNull());
       }
       builder.hardware(roleSizeToHardware.apply(from.instanceSize()));
       Image image = osImageToImage.apply(from);
       if (image != null) {
       builder.imageId(image.getId());
       builder.operatingSystem(image.getOperatingSystem());
       }

      // TODO -- update
      if (from.status() != null) {
         final Optional<RoleInstance> roleInstance = tryFindFirstRoleInstanceInDeployment(from);
         if (roleInstance.isPresent() && roleInstance.get().instanceStatus() != null) {
            builder.status(INSTANCESTATUS_TO_NODESTATUS.get(roleInstance.get().instanceStatus()));
         } else {
            builder.status(STATUS_TO_NODESTATUS.get(from.status()));
         }
      } else {
         builder.status(NodeMetadata.Status.UNRECOGNIZED);
      }

      final Set<String> publicIpAddresses = Sets.newLinkedHashSet();
      if (from.virtualIPs() != null) {
         for (Deployment.VirtualIP virtualIP : from.virtualIPs()) {
            publicIpAddresses.add(virtualIP.address());
         }
         builder.publicAddresses(publicIpAddresses);
      }
      final Set<String> privateIpAddresses = Sets.newLinkedHashSet();
      if (from.roleInstanceList() != null) {
         for (RoleInstance roleInstance : from.roleInstanceList()) {
            if (roleInstance.ipAddress() != null) {
               privateIpAddresses.add(roleInstance.ipAddress());
            }
         }
         builder.privateAddresses(privateIpAddresses);
      }
      */
      return builder.build();
   }
/*
   private String getHostname(final Deployment from) {
      final Optional<RoleInstance> roleInstance = tryFindFirstRoleInstanceInDeployment(from);
      return !roleInstance.isPresent() || roleInstance.get().hostname() == null
              ? from.name()
              : roleInstance.get().hostname();
   }

   private Optional<RoleInstance> tryFindFirstRoleInstanceInDeployment(final Deployment deployment) {
      return (deployment.roleInstanceList() == null || deployment.roleInstanceList().isEmpty())
              ? Optional.<RoleInstance>absent()
              : Optional.of(deployment.roleInstanceList().get(0));
   }
*/
}
