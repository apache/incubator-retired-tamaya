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

import org.apache.tamaya.*;
import org.apache.tamaya.core.spi.ResourceLoader;

import org.apache.tamaya.spi.Bootstrap;
import java.net.URI;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Anatole on 06.09.2014.
 */
public final class ConfigurationBuilder{

    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private Predicate<URI> ignoredSourcesPredicate;
    private List<URI> sources = new ArrayList<>();
    private String configId;
    private Map<String,String> data = new HashMap<>();
    private MetaInfo metaInfo;
    private List<ConfigMapAddition> addedMaps = new ArrayList<>();


    public static final ConfigurationBuilder of(String configId){
        return of(configId, null);
    }

    public static final ConfigurationBuilder of(String configId, ClassLoader classLoader){
        return new ConfigurationBuilder(configId, classLoader);
    }

    private ConfigurationBuilder(String configId, ClassLoader classLoader){
        Objects.requireNonNull(configId);
        this.configId = configId;
        if(classLoader == null){
            classLoader = getClass().getClassLoader();
        }
    }

    public ConfigurationBuilder put(String key, String value){
        this.data.put(key, value);
        return this;
    }

    public ConfigurationBuilder setMetainfo(MetaInfo metaInfo){
        this.metaInfo = metaInfo;
        return this;
    }

    public ConfigurationBuilder setMetainfo(String key, String metaInfo){
        this.data.put(key + ".metainfo", metaInfo);
        return this;
    }

    public ConfigurationBuilder putAll(Map<String,String> data){
        Objects.requireNonNull(data);
        this.data.putAll(data);
        return this;
    }

    public ConfigurationBuilder addResources(String... sources){
        this.sources.addAll(Bootstrap.getService(ResourceLoader.class).getResources(classLoader, sources));
        return this;
    }

    public ConfigurationBuilder addResources(Stream<String> sources){
        this.sources.addAll(Bootstrap.getService(ResourceLoader.class).getResources(classLoader, sources));
        return this;
    }

    public ConfigurationBuilder setIgnoredSourcesFilter(Predicate<String> sourceFilter){
        return this;
    }

    public ConfigurationBuilder setClassLoader(ClassLoader classLoader){
        Objects.requireNonNull(classLoader);
        this.classLoader = classLoader;
        return this;
    }

    public Configuration build(){
        Stream<URI> sourcesToRead = sources.parallelStream();
        if(ignoredSourcesPredicate != null){
            sourcesToRead = sourcesToRead.filter(ignoredSourcesPredicate);
        }
        if(metaInfo == null){
            metaInfo = MetaInfoBuilder.of(this.configId).setTimestamp(System.currentTimeMillis()).setName(this.configId)
                    .build();
        }else{
            metaInfo = MetaInfoBuilder.of(this.metaInfo).setTimestamp(System.currentTimeMillis()).setName(this.configId)
                    .build();
        }
        PropertyProvider prov = PropertyProviders.fromUris(metaInfo, sourcesToRead.collect(Collectors.toList()));
        if(!this.data.isEmpty()){
            prov = PropertyProviders.aggregate(AggregationPolicy.OVERRIDE, prov, PropertyProviders.fromMap(this.data));
        }
        for(ConfigMapAddition addition : addedMaps){
            PropertyProvider[] newMaps = new PropertyProvider[addition.configMaps.length + 1];
            newMaps[0] = prov;
            System.arraycopy(addition.configMaps, 0, newMaps, 1, addition.configMaps.length);
            prov = PropertyProviders.aggregate(addition.policy, newMaps);
        }
        final PropertyProvider finalProvider = prov;
        return new MapConfiguration(metaInfo, () -> finalProvider.toMap());
    }

    public ConfigurationBuilder addConfigMaps(AggregationPolicy override, PropertyProvider... configMaps){
        ConfigMapAddition maps = new ConfigMapAddition(override, configMaps);
        this.addedMaps.add(maps);
        return this;
    }

    private static final class ConfigMapAddition{
        AggregationPolicy policy;
        PropertyProvider[] configMaps;

        public ConfigMapAddition(AggregationPolicy policy, PropertyProvider[] configMaps){
            Objects.requireNonNull(policy, "AggregationPolicy");
            Objects.requireNonNull(configMaps, "ConfigMaps");
            for(PropertyProvider map : configMaps){
                Objects.requireNonNull(map, "A contained ConfigMap is null.");
            }
            this.policy = policy;
            this.configMaps = configMaps.clone();
        }
    }
}
