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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.json.SerializedNames;

import com.google.auto.value.AutoValue;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

/**
 * Base class for all collections that return paginated results.
 */
public abstract class PaginatedCollection<T> extends IterableWithMarker<T> {

   private final List<T> items;

   protected PaginatedCollection(List<T> items) {
      this.items = ImmutableList.copyOf(checkNotNull(items, "items cannot be null"));
   }

   public List<T> items() {
      return items;
   }

   @Override public Iterator<T> iterator() {
      return items.iterator();
   }

   @Override public Optional<Object> nextMarker() {
      return Optional.absent();

      /// TODO:
//      if (links.pages() == null) {
//         return Optional.absent();
//      }
//      return Optional.fromNullable((Object) links.pages().next());
   }

}