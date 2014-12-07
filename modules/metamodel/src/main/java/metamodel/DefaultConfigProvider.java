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
package main.java.metamodel;

import org.apache.tamaya.core.experimental.ConfigurationBuilder;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

/**
 * Created by Anatole on 30.09.2014.
 */
public class DefaultConfigProvider implements ConfigurationProviderSpi{

    private Configuration config;

    @Override
    public String getConfigName(){
        return "default";
    }

    @Override
    public Configuration getConfiguration(){
        if(config == null){
            config = ConfigBuilder.create(getConfigName()
                            .addPaths(
                                    "classpath*:META-INF/config/**/*.xml", "classpath*:META-INF/config/**/*.properties",
                                    "classpath*:META-INF/config/**/*.init")
                            .addPaths("classpath*:META-INF/config/**/*.xml", "classpath*:META-INF/config/**/*.properties",
                                    "classpath*:META-INF/config/**/*.init")
                    addConfig(AggregationPolicy.IGNORE_DUPLICATES(),
                            ConfigBuilder.create("Default Configuration")
                                    .addPaths("classpath*:META-INF/config/**/*.xml",
                                            "classpath*:META-INF/config/**/*" + ".properties",
                                            "classpath*:META-INF/config/**/*.ini").build())
                            .addConfig(AggregationPolicy.IGNORE_DUPLICATES(), Configuration.fromEnvironmentProperties(),
                                    Configuration.fromSystemProperties()).build();
        }
        return config;
    }
}
