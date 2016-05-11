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
package org.apache.tamaya.dsl;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.Map;
import java.util.Objects;

/**
 * Wrapped property source that allows dynamically reassigning the property source's
 * ordinal value. This is needed for reordering the property sources to
 * match the DSL configured ordering.
 */
final class WrappedPropertySource implements PropertySource {

    private Integer ordinalAssigned;
    private PropertySource wrapped;

    private WrappedPropertySource(PropertySource wrapped){
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    /**
     * Wraps a given property source.
     * @param propertySource the property source to be wrapped.
     * @return a wrapped property source.
     */
    public static WrappedPropertySource of(PropertySource propertySource){
        if(propertySource instanceof WrappedPropertySource){
            return (WrappedPropertySource)propertySource;
        }
        return new WrappedPropertySource(propertySource);
    }

    @Override
    public int getOrdinal() {
        return ordinalAssigned!=null?ordinalAssigned.intValue():wrapped.getOrdinal();
    }

    /**
     * Applies the given ordinal to the instance.
     * @param ordinal the new ordinal.
     */
    public void setOrdinal(int ordinal){
        this.ordinalAssigned = ordinal;
    }

    /**
     * Resetting the ordinal to the one of the wrapped property source.
     */
    public void resetOrdinal(){
        this.ordinalAssigned = null;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public PropertyValue get(String key) {
        return wrapped.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return wrapped.getProperties();
    }

    @Override
    public boolean isScannable() {
        return wrapped.isScannable();
    }

    @Override
    public String toString() {
        return "WrappedPropertySource{" +
                "ordinalAssigned=" + ordinalAssigned +
                ", wrapped=" + wrapped +
                '}';
    }
}
