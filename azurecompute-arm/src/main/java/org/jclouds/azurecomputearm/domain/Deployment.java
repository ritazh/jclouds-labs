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
package org.jclouds.azurecomputearm.domain;

import static com.google.common.collect.ImmutableList.copyOf;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

@AutoValue
public abstract class Deployment {

   public enum ProvisioningState {
      ACCEPTED("Accepted"),
      READY("Ready"),
      CANCELED("Canceled"),
      FAILED("Failed"),
      DELETED("Deleted"),
      SUCCEEDED("Succeeded"),
      RUNNING("Running"),
      UNRECOGNIZED("");

      private final String key;

      private ProvisioningState(final String key) {
         this.key = key;
      }

      public static ProvisioningState fromString(final String text) {
         if (text != null) {
            for (ProvisioningState state : ProvisioningState.values()) {
               if (text.equalsIgnoreCase(state.key)) {
                  return state;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   public enum DeploymentMode {
      INCREMENTAL("Incremental"),
      COMPLETE("Complete"),
      UNRECOGNIZED("");

      private final String key;

      private DeploymentMode(final String key) { this.key = key; }

      public static DeploymentMode fromString(final String text) {
         if (text != null) {
            for (DeploymentMode mode : DeploymentMode.values()) {
               if (text.equalsIgnoreCase(mode.key)) {
                  return mode;
               }
            }
         }
         return UNRECOGNIZED;
      }
   }

   @AutoValue
   public abstract static class TypeValue {
      public abstract String type();

      public abstract String value();

      TypeValue() { // For AutoValue only
      }

      @SerializedNames({"type", "value"})
      public static TypeValue create(final String type, final String value) {
         return new AutoValue_Deployment_TypeValue(type, value);
      }

      public Builder toBuilder() {
         return builder().fromTypeValue(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private String type;
         private String value;

         public Builder typeValue(final String type, final String value) {
            this.type = type;
            this.value = value;
            return this;
         }

         public TypeValue build() {
            return TypeValue.create(type, value);
         }

         public Builder fromTypeValue(final TypeValue typeValue) {
            return typeValue(typeValue.type(), typeValue.value());
         }
      }
   }

   @AutoValue
   public abstract static class ProviderResourceType {
      @Nullable
      public abstract String resourceType();

      @Nullable
      public abstract List<String> locations();

      @Nullable
      public abstract List<String> apiVersions();

      @Nullable
      public abstract Map<String, String> properties();

      ProviderResourceType() { // For AutoValue only
      }

      @SerializedNames({"resourceType", "locations", "apiVersions", "properties"})
      public static ProviderResourceType create(final String resourceType,
                                                final List<String> locations,
                                                final List<String> apiVersions,
                                                final Map<String, String> properties) {
         return new AutoValue_Deployment_ProviderResourceType(resourceType,
                 locations == null ? null : copyOf(locations),
                 apiVersions == null ? null : copyOf(apiVersions),
                 properties == null ? null : ImmutableMap.copyOf(properties));
      }

      public Builder toBuilder() {
         return builder().fromProviderResourceType(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private String resourceType;
         private List<String> locations;
         private List<String> apiVersions;
         private Map<String, String> properties;

         public Builder providerResourceType(final String resourceType,
                                             final List<String> locations,
                                             final List<String> apiVersions,
                                             final Map<String, String> properties) {
            this.resourceType = resourceType;
            this.locations = locations;
            this.apiVersions = apiVersions;
            this.properties = properties;
            return this;
         }

         public ProviderResourceType build() {
            return ProviderResourceType.create(resourceType, locations, apiVersions, properties);
         }

         public Builder fromProviderResourceType(final ProviderResourceType providerResourceType) {
            return providerResourceType(providerResourceType.resourceType(),
                                        providerResourceType.locations(),
                                        providerResourceType.apiVersions(),
                                        providerResourceType.properties());
         }
      }
   }

   @AutoValue
   public abstract static class Provider {
      @Nullable
      public abstract String id();

      @Nullable
      public abstract String namespace();

      @Nullable
      public abstract String registrationState();

      @Nullable
      public abstract List<ProviderResourceType> resourceTypes();

      Provider() { // For AutoValue only
      }

      @SerializedNames({"id", "namespace", "registrationState", "resourceTypes"})
      public static Provider create(final String id,
                                    final String namespace,
                                    final String registrationState,
                                    final List<ProviderResourceType> resourceTypes) {
         return new AutoValue_Deployment_Provider(id, namespace, registrationState, resourceTypes == null ? null : copyOf(resourceTypes));
      }

      public Builder toBuilder() {
         return builder().fromProvider(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private String id;
         private String namespace;
         private String registrationState;
         private List<ProviderResourceType> resourceTypes;

         public Builder provider(final String id, final String namespace, final String registrationState,
                                 final List<ProviderResourceType> resourceTypes) {
            this.id = id;
            this.namespace = namespace;
            this.registrationState = registrationState;
            this.resourceTypes = resourceTypes;
            return this;
         }

         public Provider build() {
            return Provider.create(id, namespace, registrationState, resourceTypes);
         }

         public Builder fromProvider(final Provider provider) {
            return provider(provider.id(), provider.namespace(), provider.registrationState(), provider.resourceTypes());
         }
      }
   }

   @AutoValue
   public abstract static class BasicDependency {
      @Nullable
      public abstract String id();

      @Nullable
      public abstract String resourceType();

      @Nullable
      public abstract String resourceName();

      BasicDependency() { // For AutoValue only
      }

      @SerializedNames({"id", "resourceType", "resourceName"})
      public static BasicDependency create(final String id, final String resourceType, final String resourceName) {
         return new AutoValue_Deployment_BasicDependency(id, resourceType, resourceName);
      }

      public Builder toBuilder() {
         return builder().fromBasicDependency(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private String id;
         private String resourceType;
         private String resourceName;

         public Builder basicDependency(final String id, final String resourceType, final String resourceName) {
            this.id = id;
            this.resourceType = resourceType;
            this.resourceName = resourceName;
            return this;
         }

         public BasicDependency build() {
            return BasicDependency.create(id, resourceType, resourceName);
         }

         public Builder fromBasicDependency(final BasicDependency basicDependency) {
            return basicDependency(basicDependency.id(), basicDependency.resourceType(), basicDependency.resourceName());
         }
      }
   }

   @AutoValue
   public abstract static class Dependency {
      @Nullable
      public abstract List<BasicDependency> dependencies();

      @Nullable
      public abstract String id();

      @Nullable
      public abstract String resourceType();

      @Nullable
      public abstract String resourceName();

      Dependency() { // For AutoValue only
      }

      @SerializedNames({"dependencies", "id", "resourceType", "resourceName"})
      public static Dependency create(final List<BasicDependency> dependencies,
                                      final String id,
                                      final String resourceType,
                                      final String resourceName) {
         return new AutoValue_Deployment_Dependency(dependencies == null ? null : copyOf(dependencies), id, resourceType, resourceName);
      }

      public Builder toBuilder() {
         return builder().fromDependency(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private List<BasicDependency> dependencies;
         private String id;
         private String resourceType;
         private String resourceName;

         public Builder dependency(final List<BasicDependency> dependencies,
                                   final String id,
                                   final String resourceType,
                                   final String resourceName) {
            this.dependencies = dependencies;
            this.id = id;
            this.resourceType = resourceType;
            this.resourceName = resourceName;
            return this;
         }

         public Dependency build() {
            return Dependency.create(dependencies, id, resourceType, resourceName);
         }

         public Builder fromDependency(final Dependency dependency) {
            return dependency(dependency.dependencies(), dependency.id(), dependency.resourceType(), dependency.resourceName());
         }
      }
   }

   @AutoValue
   public abstract static class ContentLink {
      public abstract String uri();

      @Nullable
      public abstract String contentVersion();

      ContentLink() { // For AutoValue only
      }

      @SerializedNames({"uri", "contentVersion"})
      public static ContentLink create(final String uri, final String contentVersion) {
         return new AutoValue_Deployment_ContentLink(uri, contentVersion);
      }

      public Builder toBuilder() {
         return builder().fromContentLink(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private String uri;
         private String contentVersion;

         public Builder contentLink(final String uri, final String contentVersion) {
            this.uri = uri;
            this.contentVersion = contentVersion;
            return this;
         }

         public ContentLink build() {
            return ContentLink.create(uri, contentVersion);
         }

         public Builder fromContentLink(final ContentLink contentLink) {
            return contentLink(contentLink.uri(), contentLink.contentVersion());
         }
      }
   }

   @AutoValue
   public abstract static class DeploymentProperties {
      @Nullable
      public abstract String provisioningState();

      @Nullable
      public abstract String correlationId();

      @Nullable
      public abstract String timestamp();

      @Nullable
      public abstract Map<String, String> outputs();

      @Nullable
      public abstract List<Provider> providers();

      @Nullable
      public abstract List<Dependency> dependencies();

      // Included for completeness, but template is actually a complex type that
      // would be difficult to model.
      @Nullable
      public abstract String template();

      @Nullable
      public abstract ContentLink templateLink();

      // Included for completeness, but parameters is actually a complex type that
      // would be difficult to model.
      @Nullable
      public abstract String parameters();

      @Nullable
      public abstract ContentLink parametersLink();

      public abstract String mode();

      // The entries below seem to be dynamic/not documented in the specification
      @Nullable
      public abstract String duration();

      @Nullable
      public abstract List<Map<String, String>> outputResources();

      DeploymentProperties() { // For AutoValue only
      }

      // TODO - leaving out "template" and "parameters", those objects are quite dynamic and hard to map, placed XXX in name to have them skipped
      @SerializedNames({"provisioningState", "correlationId", "timestamp", "outputs", "providers", "dependencies", "XXX-template", "templateLink", "XXX-parameters", "parametersLink", "mode", "duration", "outputResources"})
      public static DeploymentProperties create(final String provisioningState,
                                                final String correlationId,
                                                final String timestamp,
                                                final Map<String, String> outputs,
                                                final List<Provider> providers,
                                                final List<Dependency> dependencies,
                                                final String template,
                                                final ContentLink templateLink,
                                                final String parameters,
                                                final ContentLink parametersLink,
                                                final String mode,
                                                final String duration,
                                                final List<Map<String, String>> outputResources) {
         return new AutoValue_Deployment_DeploymentProperties(provisioningState,
                                                              correlationId,
                                                              timestamp,
                                                              outputs == null ? null : ImmutableMap.copyOf(outputs),
                                                              providers == null ? null : copyOf(providers),
                                                              dependencies == null ? null : copyOf(dependencies),
                                                              template,
                                                              templateLink,
                                                              parameters,
                                                              parametersLink,
                                                              mode,
                                                              duration,
                                                              outputResources == null ? null : copyOf(outputResources));
      }

      public Builder toBuilder() {
         return builder().fromDeploymentProperties(this);
      }

      public static Builder builder() {
         return new Builder();
      }

      public static final class Builder {

         private String provisioningState;
         private String correlationId;
         private String timestamp;
         private Map<String, String> outputs;
         private List<Provider> providers;
         private List<Dependency> dependencies;
         private String template;
         private ContentLink templateLink;
         private String parameters;
         private ContentLink parametersLink;
         private String mode;
         private String duration;
         private List<Map<String, String>> outputResources;

         public Builder deploymentProperties(final String provisioningState,
                                             final String correlationId,
                                             final String timestamp,
                                             final Map<String, String> outputs,
                                             final List<Provider> providers,
                                             final List<Dependency> dependencies,
                                             final String template,
                                             final ContentLink templateLink,
                                             final String parameters,
                                             final ContentLink parametersLink,
                                             final String mode,
                                             final String duration,
                                             final List<Map<String, String>> outputResources) {
            this.provisioningState = provisioningState;
            this.correlationId = correlationId;
            this.timestamp = timestamp;
            this.outputs = outputs;
            this.providers = providers;
            this.dependencies = dependencies;
            this.template = template;
            this.templateLink = templateLink;
            this.parameters = parameters;
            this.parametersLink = parametersLink;
            this.mode = mode;
            this.duration = duration;
            this.outputResources = outputResources;
            return this;
         }

         public DeploymentProperties build() {
            return DeploymentProperties.create(provisioningState,
                    correlationId,
                    timestamp,
                    outputs,
                    providers,
                    dependencies,
                    template,
                    templateLink,
                    parameters,
                    parametersLink,
                    mode,
                    duration,
                    outputResources);
         }

         public Builder fromDeploymentProperties(final DeploymentProperties deploymentProperties) {
            return deploymentProperties(deploymentProperties.provisioningState(),
                    deploymentProperties.correlationId(),
                    deploymentProperties.timestamp(),
                    deploymentProperties.outputs(),
                    deploymentProperties.providers(),
                    deploymentProperties.dependencies(),
                    deploymentProperties.template(),
                    deploymentProperties.templateLink(),
                    deploymentProperties.parameters(),
                    deploymentProperties.parametersLink(),
                    deploymentProperties.mode(),
                    deploymentProperties.duration(),
                    deploymentProperties.outputResources());
         }
      }
   }

   /**
    * The ID associated with the template deployment.
    */
   @Nullable
   public abstract String id();

   /**
    * The name associated with the template deployment.
    */
   public abstract String name();

   /**
    * Properties of the deployment.
    */
   @Nullable
   public abstract DeploymentProperties properties();

   Deployment() {
   } // For AutoValue only!

   @SerializedNames({"id", "name", "properties"})
   public static Deployment create(final String id, final String name, final DeploymentProperties properties) {
      return new AutoValue_Deployment(id, name, properties);
   }

   public Builder toBuilder() {
      return builder().fromDeployment(this);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {

      private String id;
      private String name;
      private DeploymentProperties properties;

      public Builder deployment(final String id, final String name, DeploymentProperties properties) {
         this.id = id;
         this.name = name;
         this.properties = properties;
         return this;
      }

      public Deployment build() {
         return Deployment.create(id, name, properties);
      }

      public Builder fromDeployment(final Deployment deployment) {
         return deployment(deployment.id(), deployment.name(), deployment.properties());
      }
   }
}
