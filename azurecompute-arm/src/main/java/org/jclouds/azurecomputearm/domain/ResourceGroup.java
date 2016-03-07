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

import com.google.auto.value.AutoValue;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ResourceGroup {

   public abstract String id();
   public abstract String name();
   public abstract String location();
   @Nullable
   public abstract HashMap<String, String> tags();

   @SerializedNames({"id", "name", "location", "tags"})
   public static ResourceGroup create(String id, String name, String location, HashMap<String, String> tags) {
      return new AutoValue_ResourceGroup(id, name, location, tags);
   }

   ResourceGroup() {}
}