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
import org.osgi.service.component.annotations.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} manages the system properties. You can disable this feature by
 * setting {@code tamaya.envprops.disable} or {@code tamaya.defaults.disable}.
 */
@Component(service = PropertySource.class)
public class SystemPropertySource extends BasePropertySource {

    /**
     * default ordinal for {@link org.apache.tamaya.core.propertysource.SystemPropertySource}
     */
    public static final int DEFAULT_ORDINAL = 1000;

    private volatile Map<String, PropertyValue> cachedProperties;

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

    @Override
    public int getDefaultOrdinal() {
        return DEFAULT_ORDINAL;
    }


    private Map<String,PropertyValue> loadProperties() {
        Properties sysProps = System.getProperties();
        previousHash = System.getProperties().hashCode();
        final String prefix = this.prefix;
        Map<String,PropertyValue> values = new HashMap<>();
        for (Map.Entry<Object,Object> entry : sysProps.entrySet()) {
            if(prefix==null) {
                values.put(entry.getKey().toString(), PropertyValue.of(entry.getKey().toString(), entry.getValue().toString(), getName()));
            }else {
                values.put(prefix + entry.getKey(), PropertyValue.of(prefix + entry.getKey(), entry.getValue().toString(), getName()));
            }
        }
        return values;
    }

    @Override
    public String getName() {
        if(disabled){
            return "system-properties(disabled)";
        }
        return "system-properties";
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
            this.cachedProperties = Collections.unmodifiableMap(loadProperties());
        }
        return this.cachedProperties;
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
