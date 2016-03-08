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
package org.jclouds.azurecomputearm.internal;

import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecomputearm.oauth.v2.config.OAuthProperties.AUDIENCE;
import static org.jclouds.azurecomputearm.oauth.v2.config.OAuthProperties.CREDENTIAL_TYPE;
import static org.jclouds.azurecomputearm.oauth.v2.config.CredentialType.CLIENT_CREDENTIALS_SECRET;

public class AzureLiveTestUtils {

    public static Properties defaultProperties(Properties properties) {
       properties = properties == null ? new Properties() : properties;
       properties.put("oauth.identity", "foo");
       properties.put("oauth.credential", "password");
       properties.put("oauth.endpoint", "https://login.microsoftonline.com/oauth2/token");
       properties.put(AUDIENCE, "https://login.microsoftonline.com/oauth2/token");
       properties.put(CREDENTIAL_TYPE, CLIENT_CREDENTIALS_SECRET.toString());
       return properties;
    }

    /*
    public static Properties bearerTokenAuthProperties(Properties properties) {
        properties = properties == null ? new Properties() : properties;
        properties.put("oauth.identity", "761326798069-r5mljlln1rd4lrbhg75efgigp36m78j5@developer.gserviceaccount.com");
        properties.put("oauth.credential", "1/8xbJqaOZXSUZbHLl5EOtu1pxz3fmmetKx9W8CV4t79M");
        properties.put("oauth.endpoint", "http://localhost:5000/o/oauth2/token");
        properties.put(AUDIENCE, "https://accounts.google.com/o/oauth2/token");
        properties.put(CREDENTIAL_TYPE, BEARER_TOKEN_CREDENTIALS.toString());
        return properties;
    }
    */

    // TODO: move to jclouds-core
    public static String setCredentialAsSecret(Properties overrides, String key) {
        String val = null;
        String testKey = "test." + key;

        if (System.getProperties().containsKey(testKey)) {
            val = System.getProperty(testKey);
        }
        checkNotNull(val, String.format("the property %s must be set (the client secret)", testKey));

        overrides.setProperty(key, val);

        return val;
    }

    public static String getMandatoryProperty(Properties properties, String key) {
        checkNotNull(properties);
        checkNotNull(key);
        String value = properties.getProperty(key);
        return checkNotNull(value, String.format("mandatory property %s or test.%s was not present", key, key));
    }

}

