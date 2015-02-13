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
package org.apache.tamaya.core.properties;

import org.apache.tamaya.spi.PropertySource;

import java.util.*;
import java.util.stream.Collectors;

class SubtractingPropertySource extends AbstractPropertySource {

    private static final long serialVersionUID = 4301042530074932562L;
    private PropertySource unit;
    private List<PropertySource> subtrahends;

    public SubtractingPropertySource(String name, PropertySource configuration, List<PropertySource> subtrahends){
        super(name);
        Objects.requireNonNull(configuration);
        this.unit = configuration;
        this.subtrahends = new ArrayList<>(subtrahends);
    }

    private boolean filter(Map.Entry<String,String> entry){
        for(PropertySource prov: subtrahends){
            if(prov.get(entry.getKey()).isPresent()){
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String,String> getProperties(){
        return this.unit.getProperties().entrySet().stream().filter(this::filter).collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }

}
