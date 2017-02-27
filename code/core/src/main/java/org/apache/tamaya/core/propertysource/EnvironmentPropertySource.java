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

import org.apache.tamaya.spi.PropertyValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} provides all properties which are set
 * via
 * {@code export myprop=myval} on UNIX Systems or
 * {@code set myprop=myval} on Windows. You can disable this feature by setting {@code tamaya.envprops.disable}
 * or {@code tamaya.defaults.disable}.
 */
public class EnvironmentPropertySource extends BasePropertySource {

    private static final Logger LOG = Logger.getLogger(EnvironmentPropertySource.class.getName());

    /**
     * Default ordinal for {@link org.apache.tamaya.core.propertysource.EnvironmentPropertySource}
     */
    public static final int DEFAULT_ORDINAL = 300;

    /**
     * Prefix that allows environment properties to virtually be mapped on specified sub section.
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
    public EnvironmentPropertySource(){
        initFromSystemProperties();
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
        String value = System.getProperty("tamaya.envprops.prefix");
        if(value==null){
            prefix = System.getenv("tamaya.envprops.prefix");
        }
        value = System.getProperty("tamaya.envprops.disable");
        if(value==null){
            value = System.getenv("tamaya.envprops.disable");
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
    public EnvironmentPropertySource(int ordinal){
        this(null, ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     * @param ordinal the ordinal to be used.
     */
    public EnvironmentPropertySource(String prefix, int ordinal){
        this.prefix = prefix;
        setOrdinal(ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     */
    public EnvironmentPropertySource(String prefix){
        this.prefix = prefix;
    }

    @Override
    public int getDefaultOrdinal() {
        return DEFAULT_ORDINAL;
    }

    @Override
    public String getName() {
        if(disabled){
            return "environment-properties(disabled)";
        }
        return "environment-properties";
    }

    @Override
    public PropertyValue get(String key) {
        if(disabled){
            return null;
        }
        String prefix = this.prefix;
        if(prefix==null) {
            return PropertyValue.of(key, System.getenv(key), getName());
        }
        return PropertyValue.of(key, System.getenv(key.substring(prefix.length())), getName());
    }

    @Override
    public Map<String, String> getProperties() {
        if(disabled){
            return Collections.emptyMap();
        }
        String prefix = this.prefix;
        if(prefix==null) {
            Map<String, String> entries = new HashMap<>(System.getenv());
            for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                entries.put("_" + entry.getKey() + ".source", getName());
            }
            return entries;
        }else{
            Map<String, String> entries = new HashMap<>();
            for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                entries.put(prefix + entry.getKey(), entry.getValue());
                entries.put("_" + prefix + entry.getKey() + ".source", getName());
            }
            return entries;
        }
    }

    @Override
    public boolean isScannable() {
        return true;
    }

}
