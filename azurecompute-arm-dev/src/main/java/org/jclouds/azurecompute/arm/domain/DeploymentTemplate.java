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
package org.jclouds.azurecompute.arm.domain;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jclouds.javax.annotation.Nullable;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class DeploymentTemplate {

    //Empty placeholders as we want to generate the empty JSON object
    @AutoValue
    public abstract static class Parameters{
        public static Parameters create(){ return new AutoValue_DeploymentTemplate_Parameters(); }
    }

    public abstract String $schema();

    public abstract String contentVersion();

    public abstract Parameters parameters();

    public abstract ImmutableMap<String, String> variables();

    public abstract ImmutableList<ResourceDefinition> resources();

    @Nullable
    public abstract ImmutableList<?> outputs();

    //@SerializedNames({"$schema", "contentVersion", "parameters", "variables", "resources" , "outputs"})
    public static DeploymentTemplate create(final String $schema,
                                  final String contentVersion,
                                  final Parameters parameters,
                                  final Map<String, String> variables,
                                  final List<ResourceDefinition> resources,
                                  final List<?> outputs) {
        DeploymentTemplate.Builder builder = DeploymentTemplate.builder()
                .$schema($schema)
                .contentVersion(contentVersion)
                .parameters(parameters);

        if (variables != null)
            builder.variables(variables);

        if (resources != null)
            builder.resources(resources);

        if (outputs != null)
                builder.outputs(outputs);

        return builder.build();
    }

    public static Builder builder() {
        return new AutoValue_DeploymentTemplate.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder $schema(String $schema);
        public abstract Builder contentVersion(String type);
        public abstract Builder parameters(Parameters parameters);
        public abstract Builder variables(Map<String, String> variables);
        public abstract Builder resources(List<ResourceDefinition> resources);
        public abstract Builder outputs(List<?> outputs);
        public abstract DeploymentTemplate build();
    }
}
