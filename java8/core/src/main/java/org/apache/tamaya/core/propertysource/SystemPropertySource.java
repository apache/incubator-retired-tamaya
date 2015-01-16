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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} manages the system properties.
 */
public class SystemPropertySource extends PropertiesPropertySource {

    /**
     * Lock for internal synchronization.
     */
    private StampedLock propertySourceLock = new StampedLock();


    /**
     * previous System.getProperties().hashCode()
     * so we can check if we need to reload
     */
    private int previousHash;


    public SystemPropertySource() {
        super(System.getProperties());
        previousHash = System.getProperties().hashCode();
        initializeOrdinal(DefaultOrdinal.SYSTEM_PROPERTIES);
    }


    @Override
    public String getName() {
        return "system-properties";
    }

    @Override
    public Map<String, String> getProperties() {

        Lock writeLock = propertySourceLock.asWriteLock();
        // only need to reload and fill our map if something has changed
        try {
            writeLock.lock();
            if (previousHash != System.getProperties().hashCode()) {

                if (previousHash != System.getProperties().hashCode()) {

                    Properties systemProperties = System.getProperties();
                    Map<String, String> properties = new HashMap<>();

                    for (String propertyName : systemProperties.stringPropertyNames()) {
                        properties.put(propertyName, System.getProperty(propertyName));
                    }

                    this.properties = Collections.unmodifiableMap(properties);
                    previousHash = systemProperties.hashCode();
                }
            }
        } finally {
            writeLock.unlock();
        }

        return super.getProperties();
    }
}
