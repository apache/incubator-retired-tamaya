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

import org.apache.tamaya.spi.PropertyValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} manages the system properties. You can disable this feature by
 * setting {@code tamaya.envprops.disable} or {@code tamaya.defaults.disable}.
 */
public class SystemPropertySource extends BasePropertySource {

    /**
     * default ordinal used.
     */
    public static final int DEFAULT_ORDINAL = 1000;

    private volatile Map<String,PropertyValue> cachedProperties;

    /**
     * previous System.getProperties().hashCode()
     * so we can check if we need to reload
     */
    private volatile int previousHash;

    /**
     * Prefix that allows system properties to virtually be mapped on specified sub section.
     */
    private String prefix;

    /**
     * If true, this property source does not return any properties. This is useful since this
     * property source is applied by default, but can be switched off by setting the
     * {@code tamaya.envprops.disable} system/environment property to {@code true}.
     */
    private boolean disabled = false;

    /**
     * Creates a new instance. Also initializes the {@code prefix} and {@code disabled} properties
     * from the system-/ environment properties:
     * <pre>
     *     tamaya.envprops.prefix
     *     tamaya.envprops.disable
     * </pre>
     */
    public SystemPropertySource(){
        super("system-properties", DEFAULT_ORDINAL);
        initFromSystemProperties();
        if(!disabled){
            cachedProperties = Collections.unmodifiableMap(loadProperties());
        }
    }

    /**
     * Initializes the {@code prefix} and {@code disabled} properties from the system-/
     * environment properties:
     * <pre>
     *     tamaya.envprops.prefix
     *     tamaya.envprops.disable
     * </pre>
     */
    private void initFromSystemProperties() {
        String value = System.getProperty("tamaya.sysprops.prefix");
        if(value==null){
            prefix = System.getenv("tamaya.sysprops.prefix");
        }
        value = System.getProperty("tamaya.sysprops.disable");
        if(value==null){
            value = System.getenv("tamaya.sysprops.disable");
        }
        if(value==null){
            value = System.getProperty("tamaya.defaults.disable");
        }
        if(value==null){
            value = System.getenv("tamaya.defaults.disable");
        }
        if(value!=null && !value.isEmpty()) {
            this.disabled = Boolean.parseBoolean(value);
        }
    }

    /**
     * Creates a new instance using a fixed ordinal value.
     * @param ordinal the ordinal number.
     */
    public SystemPropertySource(int ordinal){
        this(null, ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     * @param ordinal the ordinal to be used.
     */
    public SystemPropertySource(String prefix, int ordinal){
        this.prefix = prefix;
        setOrdinal(ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     */
    public SystemPropertySource(String prefix){
        this.prefix = prefix;
    }


    private Map<String, PropertyValue> loadProperties() {
        Properties sysProps = System.getProperties();
        previousHash = System.getProperties().hashCode();
        final String prefix = this.prefix;
        Map<String, PropertyValue> entries = new HashMap<>();
        for (Map.Entry<Object,Object> entry : sysProps.entrySet()) {
            if(entry.getKey() instanceof String && entry.getValue() instanceof String) {
                if (prefix == null) {
                    entries.put((String) entry.getKey(),
                            PropertyValue.of((String) entry.getKey(),
                                    (String) entry.getValue(),
                                    getName()));
                } else {
                    entries.put(prefix + entry.getKey(),
                            PropertyValue.of(prefix + entry.getKey(),
                                    (String) entry.getValue(),
                                    getName()));
                }
            }
        }
        return entries;
    }

    @Override
    public String getName() {
        if(disabled){
            return super.getName() + "(disabled)";
        }
        return super.getName();
    }

    @Override
    public PropertyValue get(String key) {
        if(disabled){
            return null;
        }
        String prefix = this.prefix;
        if(prefix==null) {
            return PropertyValue.of(key, System.getProperty(key), getName());
        }
        return PropertyValue.of(key, System.getProperty(key.substring(prefix.length())), getName());
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        if(disabled){
            return Collections.emptyMap();
        }
        // only need to reload and fill our map if something has changed
        // synchronization was removed, Instance was marked as volatile. In the worst case it
        // is reloaded twice, but the values will be the same.
        if (previousHash != System.getProperties().hashCode()) {
            Map<String, PropertyValue> properties = loadProperties();
            this.cachedProperties = Collections.unmodifiableMap(properties);
        }
        return this.cachedProperties;
    }

    @Override
    public boolean isScannable() {
        return true;
    }

    @Override
    protected String toStringValues() {
        return  super.toStringValues() +
                "  prefix=" + prefix + '\n' +
                "  disabled=" + disabled + '\n';
    }
}
