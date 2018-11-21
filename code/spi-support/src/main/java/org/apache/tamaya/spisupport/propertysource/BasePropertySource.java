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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

/**
 * Abstract {@link org.apache.tamaya.spi.PropertySource} that allows setting a default ordinal to be used, if no
 * ordinal is provided with the config.
 */
public abstract class BasePropertySource implements PropertySource{
    /** default ordinal that will be used, if no ordinal is provided with the config. */
    private int defaultOrdinal;
    /** Used if the ordinal has been setCurrent explicitly. */
    private volatile Integer ordinal;
    /** The name of the property source. */
    private String name;
    /** The optional prefix. */
    private String prefix;
    /**
     * If true, this property source does not return any properties. This is useful since this
     * property source is applied by default, but can be switched off by setting the
     * {@code tamaya.envprops.disable} system/environment property to {@code true}.
     */
    private boolean disabled = false;
    private ChangeSupport changeSupport = ChangeSupport.UNSUPPORTED;

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
        if(disabled){
            return name + "(disabled)";
        }
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
     * Allows setting the ordinal of this property source explicitly. This will override any evaluated
     * ordinal, or default ordinal. To reset an explicit ordinal call {@code setOrdinal(null);}.
     * @param ordinal the explicit ordinal, or {@code null}.
     */
    public void setOrdinal(Integer ordinal){
        this.ordinal = ordinal;
    }

    /**
     * Allows setting the ordinal of this property source explicitly. This will override any evaluated
     * ordinal, or default ordinal. To reset an explicit ordinal call {@code setOrdinal(null);}.
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
     * Returns the  default ordinal used, when no ordinal is setCurrent, or the ordinal was not parseable to an int createValue.
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

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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
                "  prefix=" + prefix + '\n' +
                "  disabled=" + disabled + '\n' +
                "  name='" + name + '\''  + '\n';
    }

    protected Map<String,PropertyValue> mapProperties(Map<String, String> props, long timestamp) {
        Map<String,PropertyValue> result = new HashMap<>();
        String timestampVal = String.valueOf(timestamp);
        if (prefix == null) {
            for (Map.Entry<String, String> en : props.entrySet()) {
                result.put(en.getKey(),
                        PropertyValue.createValue(en.getKey(), en.getValue())
                                .setMeta("source", getName())
                                .setMeta("timestamp", timestampVal));
            }
        } else {
            for (Map.Entry<String, String> en : props.entrySet()) {
                result.put(prefix + en.getKey(),
                        PropertyValue.createValue(prefix + en.getKey(), en.getValue())
                                .setMeta("source", getName())
                                .setMeta("timestamp", timestampVal));
            }
        }
        return result;
    }

    @Override
    public ChangeSupport getChangeSupport() {
        return changeSupport;
    }

    /**
     * Sets the change support.
     * @param changeSupport the changeSupport, not null.
     * @return this instance, for chaining.
     */
    public PropertySource setChangeSupport(ChangeSupport changeSupport) {
        this.changeSupport = Objects.requireNonNull(changeSupport);
        return this;
    }
}
