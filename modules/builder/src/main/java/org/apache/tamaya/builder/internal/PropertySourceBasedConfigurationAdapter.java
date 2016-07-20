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
package org.apache.tamaya.builder.internal;


import org.apache.tamaya.Configuration;
import org.apache.tamaya.adapter.spi.ConfigurationAdapter;
import org.apache.tamaya.builder.PropertySourceBasedConfiguration;

/**
 * Created by atsticks on 19.07.16.
 */
public class PropertySourceBasedConfigurationAdapter implements ConfigurationAdapter {

    @Override
    public <T> T adapt(Configuration configuration, Class<T> targetType) {
        if(PropertySourceBasedConfiguration.class.equals(targetType)){
            if(configuration instanceof PropertySourceBasedConfiguration){
                return (T)configuration;
            }
            return (T)new DelegatingPropertySourceBasedConfiguration(configuration);
        }
        return null;
    }
}
