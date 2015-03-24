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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} manages the system properties.
 */
public class SystemPropertySource implements PropertySource {

    /**
     * default ordinal for {@link SystemPropertySource}
     */
    public static final int DEFAULT_ORDINAL = 1000;

    private volatile Map<String,String> cachedProperties;

    /**
     * previous System.getProperties().hashCode()
     * so we can check if we need to reload
     */
    private int previousHash;


    public SystemPropertySource() {
        cachedProperties = loadProperties();
        previousHash = System.getProperties().hashCode();
    }

    private Map<String, String> loadProperties() {
        Map<String,String> props = new HashMap<>();
        Properties sysProps = System.getProperties();
        sysProps.forEach((k,v) -> props.put(k.toString(),v.toString()));
        return props;
    }

    @Override
    public int getOrdinal() {
        return DEFAULT_ORDINAL;
    }

    @Override
    public String getName() {
        return "system-properties";
    }

    @Override
    public Map<String, String> getProperties() {
        // only need to reload and fill our map if something has changed
        // synchonization was removed, Instance was marked as volatile. In the worst case it
        // is reloaded twice, but the values will be the same.
        if (previousHash != System.getProperties().hashCode()) {
            Properties systemProperties = System.getProperties();
            Map<String, String> properties = new HashMap<>();

            for (String propertyName : systemProperties.stringPropertyNames()) {
                properties.put(propertyName, System.getProperty(propertyName));
            }

            this.cachedProperties = Collections.unmodifiableMap(properties);
            previousHash = systemProperties.hashCode();
        }
        return this.cachedProperties;
    }
}
