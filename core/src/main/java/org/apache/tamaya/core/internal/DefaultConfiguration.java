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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.ServiceContext;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the Configuration API.
 */
public class DefaultConfiguration implements Configuration{

    @Override
    public Optional<String> get(String key) {
        List<PropertySource> propertySources = ServiceContext.getInstance().getService(ConfigurationContext.class).get().getPropertySources();
        for(PropertySource propertySource:propertySources){
            Optional<String> value = propertySource.get(key);
            if(value.isPresent()){
                return value;
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        Optional<String> value = get(key);
        if(value.isPresent()){
            List<PropertyConverter<T>> converters = ServiceContext.getInstance().getService(ConfigurationContext.class).get().getPropertyConverters(type);
            for(PropertyConverter<T> converter:converters){
                try{
                    T t = converter.convert(value.get());
                    if(t!=null){
                        return Optional.of(t);
                    }
                }
                catch(Exception e){
                    // TODO LOG
                }
            }
            throw new ConfigException("Unparseable config value for type: " + type.getName() + ": " + key);
        }
        return Optional.empty();
    }
}
