/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract {@link org.apache.tamaya.spi.PropertySource} that allows setting a default ordinal to be used, if no
 * ordinal is provided with the config.
 */
public abstract class BasePropertySource implements PropertySource{
    /** default ordinal that will be used, if no ordinal is provided with the config. */
    private int defaultOrdinal;
    /** Used if the ordinal has been set explicitly. */
    private volatile Integer ordinal;
    /** The name of the property source. */
    private String name;

    /**
     * Constructor.
     * @param name the (unique) property source name, not {@code null}.
     */
    protected BasePropertySource(String name){
        this.name = Objects.requireNonNull(name);
        this.defaultOrdinal = 0;
    }

    /**
     * Constructor.
     * @param defaultOrdinal default ordinal that will be used, if no ordinal is provided with the config.
     */
    protected BasePropertySource(int defaultOrdinal){
        this.name = getClass().getSimpleName();
        this.defaultOrdinal = defaultOrdinal;
    }

    /**
     * Constructor.
     * @param name the (unique) property source name, not {@code null}.
     * @param defaultOrdinal default ordinal that will be used, if no ordinal is provided with the config.
     */
    protected BasePropertySource(String name, int defaultOrdinal){
        this.name = Objects.requireNonNull(name);
        this.defaultOrdinal = defaultOrdinal;
    }


    /**
     * Constructor, using a default ordinal of 0.
     */
    protected BasePropertySource(){
        this(0);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the property source's (unique) name.
     * @param name the name, not {@code null}.
     */
    public void setName(String name){
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Allows setting the ordinal of this property source explcitly. This will override any evaluated
     * ordinal, or default ordinal. To reset an explcit ordinal call {@code setOrdinal(null);}.
     * @param ordinal the explicit ordinal, or {@code null}.
     */
    public void setOrdinal(Integer ordinal){
        this.ordinal = ordinal;
    }

    /**
     * Allows setting the ordinal of this property source explcitly. This will override any evaluated
     * ordinal, or default ordinal. To reset an explcit ordinal call {@code setOrdinal(null);}.
     * @param defaultOrdinal the default ordinal, or {@code null}.
     */
    public void setDefaultOrdinal(Integer defaultOrdinal){
        this.defaultOrdinal = defaultOrdinal;
    }

    public int getOrdinal() {
        Integer ordinal = this.ordinal;
        if(ordinal!=null){
            Logger.getLogger(getClass().getName()).finest(
                    "Using explicit ordinal '"+ordinal+"' for property source: " + getName());
            return ordinal;
        }
        PropertyValue configuredOrdinal = get(TAMAYA_ORDINAL);
        if(configuredOrdinal!=null){
            try {
                return Integer.parseInt(configuredOrdinal.getValue());
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Configured ordinal is not an int number: " + configuredOrdinal, e);
            }
        }
        return getDefaultOrdinal();
    }

    /**
     * Returns the  default ordinal used, when no ordinal is set, or the ordinal was not parseable to an int value.
     * @return the  default ordinal used, by default 0.
     */
    public int getDefaultOrdinal(){
        return defaultOrdinal;
    }

    @Override
    public PropertyValue get(String key) {
        Map<String,PropertyValue> properties = getProperties();
        PropertyValue val = properties.get(key);
        if(val==null){
            return null;
        }
        return val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasePropertySource that = (BasePropertySource) o;

        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                toStringValues() +
                '}';
    }

    protected String toStringValues() {
        return  "  defaultOrdinal=" + defaultOrdinal + '\n' +
                "  ordinal=" + ordinal  + '\n' +
                "  name='" + name + '\''  + '\n';
    }
}
