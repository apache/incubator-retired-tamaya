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

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spisupport.PropertySourceChangeSupport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} manages the system properties. You can disable this feature by
 * setting {@code tamaya.envprops.disable} or {@code tamaya.defaults.disable}.
 */
public class SystemPropertySource extends BasePropertySource {

    /**
     * default ordinal used.
     */
    public static final int DEFAULT_ORDINAL = 1000;

    private volatile PropertySourceChangeSupport cachedProperties = new PropertySourceChangeSupport(
            ChangeSupport.SUPPORTED, this);
    private AtomicInteger savedHashcode = new AtomicInteger();


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
        if(!isDisabled()){
            reload();
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
            value = System.getenv("tamaya.sysprops.prefix");
        }
        setPrefix(value);
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
            setDisabled(Boolean.parseBoolean(value));
        }
    }

    /**
     * Creates a new instance using a fixed ordinal createValue.
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
        setPrefix(prefix);
        setOrdinal(ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     */
    public SystemPropertySource(String prefix){
        setPrefix(prefix);
    }


    private Map<String, PropertyValue> loadProperties() {
        Map<String, String> props = MapPropertySource.getMap(System.getProperties());
        return mapProperties(props, System.currentTimeMillis());
    }

    @Override
    public PropertyValue get(String key) {
        if(isDisabled()){
            return null;
        }
        reload();
        return this.cachedProperties.getValue(key);
    }

    public void reload() {
        int hashCode = System.getProperties().hashCode();
        if(hashCode!=this.savedHashcode.get()) {
            this.savedHashcode.set(hashCode);
            this.cachedProperties.update(loadProperties());
        }
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        if(isDisabled()){
            return Collections.emptyMap();
        }
        reload();
        return cachedProperties.getProperties();
    }

    public String getVersion(){
        return cachedProperties.getVersion();
    }

    @Override
    public ChangeSupport getChangeSupport() {
        return ChangeSupport.SUPPORTED;
    }

}
