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

import org.apache.tamaya.core.spi.ConfigurationFormat;

import org.apache.tamaya.Configuration;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
* Created by Anatole on 06.09.2014.
*/
public final class ConfigurationProducerBuilder{

    ClassLoader classLoader;
    Predicate<String> ignoredSourcesFilter;
    List<String> resources = new ArrayList<>();
    List<ConfigurationFormat> privateFormats = new ArrayList<>();
    String configId;

    private ConfigurationProducerBuilder(String configId, ClassLoader classLoader) {
        Objects.requireNonNull(configId);
        this.configId = configId;
        if(classLoader==null){
            classLoader = getClass().getClassLoader();
        }
    }

    public static final ConfigurationProducerBuilder of(String configName) {
        return new ConfigurationProducerBuilder(configName, null);
    }

    public static final ConfigurationProducerBuilder of(String configName, ClassLoader classLoader) {
        return new ConfigurationProducerBuilder(configName, null);
    }

    public ConfigurationProducerBuilder addResources(String... resources){
        this.resources.addAll(Arrays.asList(resources));
        return  this;
    }

    public ConfigurationProducerBuilder setClassLoader(ClassLoader classLoader){
        this.classLoader = classLoader;
        return  this;
    }


    public ConfigurationProducerBuilder addResources(Stream<String> resources){
       resources.forEach(this.resources::add);
       return  this;
    }

    public ConfigurationProducerBuilder addPrivateFormats(ConfigurationFormat... formats){
        this.privateFormats.addAll(Arrays.asList(formats));
        return  this;
    }

    @SafeVarargs
    public final ConfigurationProducerBuilder addPrivateFormats(Iterable<ConfigurationFormat>... formats){
        for(Iterable<ConfigurationFormat> it: formats){
            for (ConfigurationFormat anIt : it) {
                this.privateFormats.add(anIt);
            }
        }
        return  this;
    }

    public ConfigurationProducerBuilder setIgnoredSourcesFilter(Predicate<String> sourceFilter){
        return  this;
    }

    public Supplier<Configuration> build() {
        return new ConfigurationProducer(this);
    }
}
