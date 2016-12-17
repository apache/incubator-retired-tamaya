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
package org.apache.tamaya.examples.custompropertysource;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Small example demonstrating the usage of a self-written {@link org.apache.tamaya.spi.PropertySource}
 * and {@link org.apache.tamaya.spi.PropertySourceProvider}.
 *
 * <p>
 *  {@link SimplePropertySource} is a custom implementation of a
 *  {@link org.apache.tamaya.spi.PropertySource}. It reads its properties from a
 *  {@value SimplePropertySource#CONFIG_PROPERTIES_LOCATION}. As it is an implementation
 *  of {@code PropertySource} and it is listed as service implementation
 *  in {@code META-INF/services/org.apache.tamaya.spi.PropertySource} Tamaya is able
 *  to find and to use it through the Service Provider Interface service of Java.
 * </p>
 *
 * <p>
 *  The same applies to {@link SimplePropertySourceProvider} which is an implementation
 *  of {@link org.apache.tamaya.spi.PropertySourceProvider}. Tamaya finds implementations
 *  of a {@link org.apache.tamaya.spi.PropertySourceProvider} also through the
 *  Service Provider Interface service of Java. Therefore it is listed in
 *  {@code META-INF/services/org.apache.tamaya.spi.PropertySourceProvider} file.
 * </p>
 */
public class Main {
    /*
     * Turns off all logging.
     */
    static {
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(java.util.logging.Level.OFF);
    }

    private Main() {
    }

    public static void main(String[] args) {
        Configuration cfg = ConfigurationProvider.getConfiguration();

        System.out.println("*****************************************************");
        System.out.println("Simple Example (with a PropertySource and a Provider)");
        System.out.println("*****************************************************");
        System.out.println();
        System.out.println("Example Metadata:");
        System.out.println("\tType        :  " + cfg.get("example.type"));
        System.out.println("\tName        :  " + cfg.get("example.name"));
        System.out.println("\tDescription :  " + cfg.get("example.description"));
        System.out.println("\tVersion     :  " + cfg.get("example.version"));
        System.out.println("\tAuthor      :  " + cfg.get("example.author"));
        System.out.println();
        System.out.println("\tPath        :  " + cfg.get("Path"));
        System.out.println("\taProp       :  " + cfg.get("aProp"));
        System.out.println();

        dump(cfg.getProperties(), System.out);
    }

    private static void dump(Map<String, String> properties, PrintStream stream) {
        stream.println("FULL DUMP:\n\n");

        for (Map.Entry<String, String> en : new TreeMap<>(properties).entrySet()) {
            stream.println(format("\t%s = %s", en.getKey(), en.getValue()));
        }
    }
}
