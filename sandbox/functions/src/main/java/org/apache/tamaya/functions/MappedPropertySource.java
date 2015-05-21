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
package org.apache.tamaya.functions;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

/**
 * PropertySource implementation that maps certain parts (defined by an {@code UnaryOperator<String>}) to alternate areas.
 */
class MappedPropertySource implements PropertySource {

	private static final long serialVersionUID = 8690637705511432083L;

	/** The mapping operator. */
    private UnaryOperator<String> keyMapper;
    /** The base configuration. */
    private PropertySource propertySource;

    /**
     * Creates a new instance.
     * @param config the base configuration, not null
     * @param keyMapper The mapping operator, not null
     */
    public MappedPropertySource(PropertySource config, UnaryOperator<String> keyMapper) {
        this.propertySource = Objects.requireNonNull(config);
        this.keyMapper = Objects.requireNonNull(keyMapper);
    }

    @Override
    public int getOrdinal(){
        return this.propertySource.getOrdinal();
    }

    @Override
    public String getName(){
        return this.propertySource.getName()+"[mapped]";
    }

    @Override
    public Map<String, String> getProperties() {
        Map<String, String> result = new HashMap<>();
        Map<String, String> map = this.propertySource.getProperties();
        map.forEach((k,v) -> {
            String targetKey = keyMapper.apply(k);
            if(targetKey!=null){
                result.put(targetKey, v);
            }
        });
        return result;
    }

    @Override
    public String get(String key){
        return getProperties().get(key);
    }

}