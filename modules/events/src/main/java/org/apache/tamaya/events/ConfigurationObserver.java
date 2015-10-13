/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.events;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.events.spi.ConfigObserverSpi;
import org.apache.tamaya.spi.ServiceContextManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Singleton accessor for managing {@link ConfigListener} instances and mappings.
 */
public class ConfigurationObserver {

    /**
     * Private singleton constructor.
     */
    private ConfigurationObserver() {
    }

    /**
     * The backing SPI.
     */
    private static final ConfigObserverSpi SPI = ServiceContextManager.getServiceContext()
            .getService(ConfigObserverSpi.class);


    /**
     * Add key expressions for generating ConfigurationChange events.
     *
     * @param keys             the keys to be observed for changes.
     */
    public static <T> void addObservedKeys(Collection<String> keys) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigurationObserver.class.getName());
        }
        SPI.addObservedKeys(keys);
    }

    /**
     * Add key expressions for generating ConfigurationChange events.
     *
     * @param keys             the keys to be observed for changes.
     */
    public static <T> void addObservedKeys(String... keys) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigurationObserver.class.getName());
        }
        SPI.addObservedKeys(Arrays.asList(keys));
    }

    /**
     * Removes key expressions for generating ConfigurationChange events.
     *
     * @param keys the keys to be observed for changes.
     */
    public static <T> void removeObservedKeys(Collection<String> keys) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigurationObserver.class.getName());
        }
        SPI.removeObservedKeys(keys);
    }

    /**
     * Removes key expressions for generating ConfigurationChange events.
     *
     * @param keys the keys to be observed for changes.
     */
    public static <T> void removeObservedKeys(String... keys) {
        if (SPI == null) {
            throw new ConfigException("No SPI registered for " +
                    ConfigurationObserver.class.getName());
        }
        SPI.removeObservedKeys(Arrays.asList(keys));
    }

    /**
     * Get all registered key expressions for generating ConfigurationChange events.
     *
     * @return  set with the keys found, never null.
     */
    public static Set<String> getObservedKeys() {
        return SPI.getObservedKeys();
    }
}
