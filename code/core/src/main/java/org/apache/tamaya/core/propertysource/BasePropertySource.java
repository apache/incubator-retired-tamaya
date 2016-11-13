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
package org.apache.tamaya.core.propertysource;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.PropertyValueBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract {@link org.apache.tamaya.spi.PropertySource} that allows to set a default ordinal that will be used, if no
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
     * @param name the (unique) property source name, not null.
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
     * @param name the (unique) property source name, not null.
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
     * @param name the name, not null.
     */
    public void setName(String name){
        this.name = Objects.requireNonNull(name);
    }

    /**
     * Allows to set the ordinal of this property source explcitly. This will override any evaluated
     * ordinal, or default ordinal. To reset an explcit ordinal call {@code setOrdinal(null);}.
     * @param ordinal the explicit ordinal, or null.
     */
    public void setOrdinal(Integer ordinal){
        this.ordinal = ordinal;
    }

    /**
     * Allows to set the ordinal of this property source explcitly. This will override any evaluated
     * ordinal, or default ordinal. To reset an explcit ordinal call {@code setOrdinal(null);}.
     * @param defaultOrdinal the default ordinal, or null.
     */
    public void setDefaultOrdinal(Integer defaultOrdinal){
        this.defaultOrdinal = defaultOrdinal;
    }

    @Override
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
                        "Configured Ordinal is not an int number: " + configuredOrdinal, e);
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
        Map<String,String> properties = getProperties();
        String val = properties.get(key);
        if(val==null){
            return null;
        }
        PropertyValueBuilder b = new PropertyValueBuilder(key, val, getName());
        String metaKeyStart = "_" + key + ".";
        for(Map.Entry<String,String> en:properties.entrySet()) {
            if(en.getKey().startsWith(metaKeyStart) && en.getValue()!=null){
                b.addContextData(en.getKey().substring(metaKeyStart.length()), en.getValue());
            }
        }
        return b.build();
    }

    @Override
    public boolean isScannable(){
        return true;
    }
}
