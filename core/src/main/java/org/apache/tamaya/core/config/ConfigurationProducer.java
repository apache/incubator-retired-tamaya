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
package org.apache.tamaya.core.config;

import org.apache.tamaya.Configuration;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
* Created by Anatole on 06.09.2014.
*/
final class ConfigurationProducer implements Supplier<Configuration>{

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private Predicate<String> ignoredSourcesFilter;
    private List<String> resources = new ArrayList<>();
    private String configId;

    ConfigurationProducer(ConfigurationProducerBuilder producerBuilder) {
        Objects.requireNonNull(producerBuilder);
        this.configId = producerBuilder.configId;
        this.classLoader = producerBuilder.classLoader;
        this.ignoredSourcesFilter = producerBuilder.ignoredSourcesFilter;
        this.resources.addAll(producerBuilder.resources);
    }


    public Configuration get() {
        ConfigurationBuilder builder = ConfigurationBuilder.of(configId);
        if(ignoredSourcesFilter!=null){
            builder.setIgnoredSourcesFilter(ignoredSourcesFilter);
        }
        builder.addResources(resources);
        if(classLoader!=null){
            builder.setClassLoader(classLoader);
        }
        return builder.build();
    }
}
