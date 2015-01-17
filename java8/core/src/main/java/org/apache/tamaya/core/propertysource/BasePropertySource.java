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

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A base class for {@link PropertySource}s. It provides a {@link #initializeOrdinal(int)} method that
 * reads the ordinal from the config source itself, allowing the ordinal to be "self-configured" by
 * the configuration read.
 */
public abstract class BasePropertySource implements PropertySource {

    private static final Logger LOG = Logger.getLogger(BasePropertySource.class.getName());


    private int ordinal = DefaultOrdinal.PROPERTY_SOURCE;


    @Override
    public int getOrdinal() {
        return ordinal;
    }


    @Override
    public String get(String key) {
        Objects.requireNonNull(key, "key must not be null");
        return getProperties().get(key);
    }


    /**
     * Initializing the ordinal of this {@link PropertySource} with the given defaultOrdinal.
     *
     * If {@link PropertySource#TAMAYA_ORDINAL} is present via {@link #get(String)} and the
     * value is a valid {@link Integer} then, the defaultOrdinal will be overridden.
     *
     * @param defaultOrdinal of the {@link PropertySource}
     */
    protected void initializeOrdinal(final int defaultOrdinal) {
        this.ordinal = defaultOrdinal;

        String ordinal = get(PropertySource.TAMAYA_ORDINAL);
        if (ordinal != null) {

            try {
                this.ordinal = Integer.parseInt(ordinal);
            } catch (NumberFormatException e) {
                LOG.log(Level.WARNING,
                        "Specified {0} is not a valid Integer value: {1} - using defaultOrdinal {2}",
                        new Object[]{PropertySource.TAMAYA_ORDINAL, ordinal, defaultOrdinal});
            }
        }
    }

}
