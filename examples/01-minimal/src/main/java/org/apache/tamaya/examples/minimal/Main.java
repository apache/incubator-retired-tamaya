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
package org.apache.tamaya.examples.minimal;

import javax.config.Config;
import javax.config.ConfigProvider;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Minimal example showing how to use Tamaya in the simplest possible way.
 *
 * <p>
 *  Without any additional configuration Tamaya allows you access via
 *  {@link ConfigProvider#getConfig} all configuration values.
 *  Accessable are all system environment properties, all system properties,
 *  and all properties which are found in {@code /META-INF/javaconfiguration.properties}
 *  or {@code /META-INF/javaconfiguration.xml}.
 * </p>
 */
public class Main {
    /*
     * Turns off all logging.
     */
    static {
        LogManager.getLogManager().reset();
        Logger globalLogger = Logger.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);
        globalLogger.setLevel(java.util.logging.Level.OFF);
    }

    private Main() {
    }

    public static void main(String[] args) {
        Config cfg = ConfigProvider.getConfig();

        System.out.println("****************************************************");
        System.out.println("Minimal Example");
        System.out.println("****************************************************");
        System.out.println();
        System.out.println("Example Metadata:");
        System.out.println("\tType        :  " + cfg.getValue("example.type", String.class));
        System.out.println("\tName        :  " + cfg.getValue("example.name", String.class));
        System.out.println("\tDescription :  " + cfg.getValue("example.description", String.class));
        System.out.println("\tVersion     :  " + cfg.getValue("example.version", String.class));
        System.out.println("\tAuthor      :  " + cfg.getValue("example.author", String.class));
        System.out.println();

        dump(cfg.getPropertyNames(), System.out, cfg);
    }

    private static void dump(Iterable<String> properties, PrintStream stream, Config config) {
        stream.println("FULL DUMP:\n\n");

        for (String key : properties) {
            stream.println(format("\t%s = %s", key, config.getValue(key, String.class)));
        }
    }
}
