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
package org.apache.tamaya.spi;

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.PropertySource;

import java.util.Map;
import java.util.Optional;

/**
 * Factory to create configurations from property sources. If not defines a default is used.
 */
public interface ConfigurationFactorySpi {
    /**
     * Creates a configuration from a {@link org.apache.tamaya.PropertySource}.
     *
     * @param propertySource the property source
     * @return the corresponding Configuration instance, never null.
     */
    default Configuration from(PropertySource propertySource){
        return new Configuration() {
            @Override
            public String getName() {
                return propertySource.getName();
            }

            @Override
            public Optional<String> get(String key) {
                return propertySource.get(key);
            }

            @Override
            public void update(ConfigChangeSet changeSet) {
                propertySource.update(changeSet);
            }

            @Override
            public void registerForUpdate(ConfigChangeSetCallback callback) {
                propertySource.registerForUpdate(callback);
            }

            @Override
            public void removeForUpdate(ConfigChangeSetCallback callback) {
                propertySource.removeForUpdate(callback);
            }

            @Override
            public Map<String, String> getProperties() {
                return propertySource.getProperties();
            }

            @Override
            public String toString(){
                return "Configuration, based on " + propertySource;
            }
        };
    }
}
