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

import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

class FilteredPropertySource implements PropertySource {

    private PropertySource baseSource;
    private Predicate<String> filter;

    public FilteredPropertySource(PropertySource baseSource, Predicate<String> filter){
        this.baseSource = Objects.requireNonNull(baseSource);
        this.filter = Objects.requireNonNull(filter);
    }

    @Override
    public int ordinal(){
        return baseSource.ordinal();
    }

    @Override
    public String getName(){
        return baseSource + "(filtered)";
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

    @Override
    public String toString() {
        return "FilteredPropertySource{" +
                "baseSource=" + baseSource +
                ", filter=" + filter +
                '}';
    }
}
