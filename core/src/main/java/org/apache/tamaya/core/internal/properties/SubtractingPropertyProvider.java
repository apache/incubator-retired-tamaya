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
package org.apache.tamaya.core.internal.properties;

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertyProvider;
import org.apache.tamaya.core.properties.AbstractPropertyProvider;

import java.util.*;
import java.util.stream.Collectors;

class SubtractingPropertyProvider extends AbstractPropertyProvider {

    private static final long serialVersionUID = 4301042530074932562L;
    private PropertyProvider unit;
    private List<PropertyProvider> subtrahends;

    public SubtractingPropertyProvider(MetaInfo metaInfo, PropertyProvider configuration, List<PropertyProvider> subtrahends){
        super(metaInfo==null?MetaInfoBuilder.of(configuration.getMetaInfo()).setType("subtracted").build():
                MetaInfoBuilder.of(metaInfo).setType("subtracted").build());
        Objects.requireNonNull(configuration);
        this.unit = configuration;
        this.subtrahends = new ArrayList<>(subtrahends);
    }

    private boolean filter(Map.Entry<String,String> entry){
        for(PropertyProvider prov: subtrahends){
            if(prov.containsKey(entry.getKey())){
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String,String> toMap(){
        return this.unit.toMap().entrySet().stream().filter(this::filter).collect(Collectors.toMap(
                (en) -> en.getKey(),
                (en) -> en.getValue()
        ));
    }

    @Override
    public ConfigChangeSet load(){
        unit.load();
        return super.load();
    }

    /**
     * Apply a config change to this item. Hereby the change must be related to the same instance.
     * @param change the config change
     * @throws org.apache.tamaya.ConfigException if an unrelated change was passed.
     * @throws UnsupportedOperationException when the configuration is not writable.
     */
    @Override
    public void apply(ConfigChangeSet change){
        this.unit.apply(change);
    }

}
