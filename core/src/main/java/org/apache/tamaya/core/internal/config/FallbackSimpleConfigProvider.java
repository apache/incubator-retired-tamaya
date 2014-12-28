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
package org.apache.tamaya.core.internal.config;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.properties.AggregationPolicy;
import org.apache.tamaya.core.properties.PropertySourceBuilder;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

/**
 * Implementation of a default config provider used as fallback, if no {@link org.apache.tamaya.core.spi.ConfigurationProviderSpi}
 * instance is registered for providing the {@code default} {@link org.apache.tamaya.Configuration}. The providers loads the follwing
 * config resources:
 * <ul>
 *     <li>Classpath: META-INF/cfg/default/&#42;&#42;/&#42;.xml, META-INF/cfg/default/&#42;&#42;/&#42;.properties, META-INF/cfg/default/&#42;&#42;/&#42;.ini</li>
 *     <li>Classpath: META-INF/cfg/config/#42;#42;/#42;.xml, META-INF/cfg/config/#42;#42;/#42;.properties, META-INF/cfg/config/#42;#42;/#42;.ini</li>
 *     <li>Files: defined by the system property -Dconfig.dir</li>
 *     <li>system properties</li>
 * </ul>
 */
public class FallbackSimpleConfigProvider implements ConfigurationProviderSpi {

    private static final String DEFAULT_CONFIG_NAME = "default";

    /**
     * The loaded configuration instance.
     */
    private volatile Configuration configuration;

    @Override
    public String getConfigName() {
        return DEFAULT_CONFIG_NAME;
    }

    @Override
    public Configuration getConfiguration() {
        Configuration cfg = configuration;
        if (cfg == null) {
            reload();
            cfg = configuration;
        }
        return cfg;
    }


    @Override
    public void reload() {
        this.configuration = Configuration.from(
                PropertySourceBuilder.of(DEFAULT_CONFIG_NAME)
                        .addProviders(PropertySourceBuilder.of("CL default")
                                .withAggregationPolicy(AggregationPolicy.LOG_ERROR)
                                .addPaths("META-INF/cfg/default/**/*.xml", "META-INF/cfg/default/**/*.properties", "META-INF/cfg/default/**/*.ini")
                                .build())
                        .addProviders(PropertySourceBuilder.of("CL default")
                                .withAggregationPolicy(AggregationPolicy.LOG_ERROR)
                                .addPaths("META-INF/cfg/config/**/*.xml", "META-INF/cfg/config/**/*.properties", "META-INF/cfg/config/**/*.ini")
                                .build())
                        .addSystemProperties()
                        .addEnvironmentProperties()
                        .build());
    }
}