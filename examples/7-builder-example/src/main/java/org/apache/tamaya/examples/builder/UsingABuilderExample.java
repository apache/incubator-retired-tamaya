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
package org.apache.tamaya.examples.builder;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.builder.PropertySourceBasedConfigurationBuilder;
import org.apache.tamaya.resource.ConfigResources;


/**
 * Created by Anatole on 05.05.2015.
 */
public class UsingABuilderExample {

    private UsingABuilderExample() {
    }

    public static void main(String... args) {

        PropertySourceBasedConfigurationBuilder builder = new PropertySourceBasedConfigurationBuilder();
        Configuration config = builder.addPropertySources(ConfigResources.getResourceResolver().getResources("META-INF/boot/*.ini"))
                .addPropertySources(ConfigResources.getResourceResolver().getResources("META-INF/config/*.properties"))
                .enableProvidedPropertyConverters()
                .enabledProvidedPropertyFilters()
                .build();
        System.out.println(config);
    }

}
