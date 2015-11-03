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
package org.apache.tamaya.examples.resolver;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Map;
import java.util.TreeMap;

/**
 * Example illustrating the usage of value resolver expressions (placeholders).
 */
public class ExampleMain {

    private ExampleMain() {
    }

    public static void main(String... args){
        System.out.println("****************************************************");
        System.out.println("Resolver Example");
        System.out.println("****************************************************");
        System.out.println();
        Configuration cfg = ConfigurationProvider.getConfiguration();
        System.out.println("Example Metadata:");
        System.out.println("  Type        :  " + cfg.get("example.type"));
        System.out.println("  Name        :  " + cfg.get("example.name"));
        System.out.println("  Description :  " + cfg.get("example.description"));
        System.out.println("  Version     :  " + cfg.get("example.version"));
        System.out.println("  Author      :  " + cfg.get("example.author"));
        System.out.println();
        System.out.println("Resolved Data:");
        System.out.println("  FullName     :  " + cfg.get("example.fullName"));
        System.out.println("  FullVersion  :  " + cfg.get("example.fullVersion"));
        System.out.println();
        System.out.println();
        System.out.println("FULL DUMP:\n\n" + dump(cfg.getProperties()));
    }

    private static String dump(Map<String, String> properties) {
        StringBuilder b = new StringBuilder();
        for(Map.Entry en: new TreeMap<>(properties).entrySet()){
            b.append("  " + en.getKey() + " = " + en.getValue() + '\n');
        }
        return b.toString();
    }
}
