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
package org.jclouds.azurecompute.arm.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;

import com.google.common.base.Function;
import com.google.inject.Inject;

public class ImageReferenceToImage implements Function<ImageReference, Image> {

   private static final String UNRECOGNIZED = "UNRECOGNIZED";

   private static final String UBUNTU = "Ubuntu";

   private static final String WINDOWS = "Windows";

   private static final String OPENLOGIC = "openLogic";

   private static final String CENTOS = "CentOS";

   private static final String COREOS = "CoreOS";

   private static final String OPENSUSE = "openSUSE";

   private static final String SUSE = "SUSE";

   private static final String SQL_SERVER = "SQL Server";

   private static final String ORACLE_lINUX = "Oracle Linux";

   @Inject
   ImageReferenceToImage() {
   }

   @Override
   public Image apply(final ImageReference image) {
      final ImageBuilder builder = new ImageBuilder()
              .name(image.offer())
              .description(image.sku())
              .status(Image.Status.AVAILABLE)
              .version(image.sku())
              .id(image.offer() + image.sku())
              .providerId(image.publisher());

      final OperatingSystem.Builder osBuilder = osFamily().apply(image);
      return builder.operatingSystem(osBuilder.build()).build();
   }

   public static Function<ImageReference, OperatingSystem.Builder> osFamily() {
      return new Function<ImageReference, OperatingSystem.Builder>() {
         @Override
         public OperatingSystem.Builder apply(final ImageReference image) {
            checkNotNull(image.offer(), "offer");
            final String label = image.offer();

            OsFamily family = OsFamily.UNRECOGNIZED;
            if (label.contains(CENTOS)) {
               family = OsFamily.CENTOS;
            } else if (label.contains(OPENLOGIC)) {
               family = OsFamily.CENTOS;
            } else if (label.contains(SUSE)) {
               family = OsFamily.SUSE;
            } else if (label.contains(UBUNTU)) {
               family = OsFamily.UBUNTU;
            } else if (label.contains(WINDOWS)) {
               family = OsFamily.WINDOWS;
            } else if (label.contains(ORACLE_lINUX)) {
               family = OsFamily.OEL;
            }

            // only 64bit OS images are supported by Azure ARM
            return OperatingSystem.builder().
                    family(family).
                    is64Bit(true).
                    description(image.sku()).
                    version(image.sku());
         }
      };
   }
}
