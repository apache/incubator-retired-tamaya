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
package org.apache.tamaya.base.configsource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This {@link javax.config.spi.ConfigSource} manages the system properties. You can disable this feature by
 * setting {@code tamaya.envprops.disable} or {@code tamaya.defaults.disable}.
 */
public class SystemConfigSource extends BaseConfigSource {

    /**
     * default ordinal used.
     */
    public static final int DEFAULT_ORDINAL = 1000;

    private volatile Map<String,String> cachedProperties;

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
    public SystemConfigSource(){
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
    public SystemConfigSource(int ordinal){
        this(null, ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     * @param ordinal the ordinal to be used.
     */
    public SystemConfigSource(String prefix, int ordinal){
        this.prefix = prefix;
        setOrdinal(ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     */
    public SystemConfigSource(String prefix){
        this.prefix = prefix;
    }


    private Map<String, String> loadProperties() {
        Properties sysProps = System.getProperties();
        previousHash = System.getProperties().hashCode();
        final String prefix = this.prefix;
        Map<String, String> entries = new HashMap<>();
        for (Map.Entry<Object,Object> entry : sysProps.entrySet()) {
            if(entry.getKey() instanceof String && entry.getValue() instanceof String) {
                if (prefix == null) {
                    entries.put((String) entry.getKey(), (String) entry.getValue());
                } else {
                    entries.put(prefix + entry.getKey(), (String) entry.getValue());
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
    public String getValue(String key) {
        if(disabled){
            return null;
        }
        String prefix = this.prefix;
        if(prefix==null) {
            return System.getProperty(key);
        }
        return System.getProperty(key.substring(prefix.length()));
    }

    @Override
    public Map<String, String> getProperties() {
        if(disabled){
            return Collections.emptyMap();
        }
        // only need to reload and fill our map if something has changed
        // synchronization was removed, Instance was marked as volatile. In the worst case it
        // is reloaded twice, but the values will be the same.
        if (previousHash != System.getProperties().hashCode()) {
            Map<String, String> properties = loadProperties();
            this.cachedProperties = Collections.unmodifiableMap(properties);
        }
        return this.cachedProperties;
    }

    @Override
    protected String toStringValues() {
        return  super.toStringValues() +
                "  prefix=" + prefix + '\n' +
                "  disabled=" + disabled + '\n';
    }
}
