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

import org.apache.tamaya.*;

import java.io.Serializable;
import java.util.Map;

/**
 * Configuration implementation that stores all current values current a given (possibly dynamic, contextual and non remote
 * capable instance) and is fully serializable.
 */
final class FrozenPropertySource extends AbstractPropertySource implements Serializable{
    private static final long serialVersionUID = -6373137316556444171L;

    private PropertySource properties;

    /**
     * Constructor.
     * @param config The base configuration.
     */
    private FrozenPropertySource(PropertySource config){
        super(config.getName());
        this.properties = PropertySourceBuilder.of(config).buildFrozen();
    }

    public static final PropertySource of(PropertySource config){
        if(config instanceof FrozenPropertySource){
            return config;
        }
        return new FrozenPropertySource(config);
    }

    @Override
    public Map<String,String> getProperties(){
        return properties.getProperties();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FrozenPropertySource that = (FrozenPropertySource) o;

        if (!properties.equals(that.properties)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = properties.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FreezedConfiguration{" +
                "properties=" + properties +
                ", name=" + name +
                '}';
    }
}
