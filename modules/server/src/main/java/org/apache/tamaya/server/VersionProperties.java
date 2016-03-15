/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * <p>This class gives access to the current name and the current version information
 * at runtime.</p>
 *
 * <p>All information offered by this is loaded from a properties file at
 * {@link VersionProperties#VERSION_PROPERTY_FILE}.</p>
 */
public class VersionProperties {
    private static final String VERSION_PROPERTY_FILE = "/META-INF/tamaya-server-version.properties";

    static {
        try (InputStream resource = VersionProperties.class.getResourceAsStream(VERSION_PROPERTY_FILE)) {
            if (null == resource) {
                throw new ExceptionInInitializerError("Failed to version information resource. " +
                                                       VERSION_PROPERTY_FILE + " not found.");
            }

            Properties properties = new Properties();
            properties.load(resource);

            product = properties.getProperty("server.product", "n/a");
            version = properties.getProperty("server.version", "n/a");

        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static String product;
    private static String version;

    private VersionProperties() {
    }

    public static String getProduct() {
        return product;
    }

    public static String getVersion() {
        return version;
    }


}
