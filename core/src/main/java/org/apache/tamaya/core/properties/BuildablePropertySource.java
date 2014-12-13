/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.properties;

import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.PropertySource;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by Anatole on 07.12.2014.
 */
class BuildablePropertySource implements PropertySource
{

    private MetaInfo metaInfo;
    private PropertySource baseProvider;

    public BuildablePropertySource(MetaInfo metaInfo, PropertySource baseProvider) {
        this.metaInfo = Objects.requireNonNull(metaInfo);
        this.baseProvider = Objects.requireNonNull(baseProvider);
    }

    @Override
    public Optional<String> get(String key) {
        return this.baseProvider.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return this.baseProvider.containsKey(key);
    }

    @Override
    public Map<String, String> toMap() {
        return this.baseProvider.toMap();
    }

    @Override
    public MetaInfo getMetaInfo() {
        return this.metaInfo;
    }

    @Override
    public String toString(){
        return "BuildablePropertyProvider -> " + getMetaInfo().toString();
    }

}
