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

import org.apache.tamaya.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

class FilteredPropertySource extends AbstractPropertySource {

    private static final long serialVersionUID = 4301042530074932562L;
    private PropertySource unit;
    private Predicate<String> filter;

    public FilteredPropertySource(String name, PropertySource configuration, Predicate<String> filter){
        super(name==null?Objects.requireNonNull(configuration).getName():name);
        this.unit = configuration;
        this.filter = filter;
    }

    @Override
    public Map<String,String> getProperties(){
        final Map<String,String> result = new HashMap<>();
        this.unit.getProperties().entrySet().forEach(e -> {
            if(filter.test(e.getKey())){
                result.put(e.getKey(), e.getValue());
            }
        });
        return result;
    }

}
