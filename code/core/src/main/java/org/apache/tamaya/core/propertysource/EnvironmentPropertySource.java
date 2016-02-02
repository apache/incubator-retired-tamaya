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

import java.util.Map;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} provides all Properties which are set
 * via
 * {@code export myprop=myval} on UNIX Systems or
 * {@code set myprop=myval} on Windows
 */
public class EnvironmentPropertySource implements PropertySource {

    /**
     * default ordinal for {@link org.apache.tamaya.core.propertysource.EnvironmentPropertySource}
     */
    public static final int DEFAULT_ORDINAL = 300;

    @Override
    public int getOrdinal() {
        return DEFAULT_ORDINAL;
    }

    @Override
    public String getName() {
        return "environment-properties";
    }

    @Override
    public PropertyValue get(String key) {
        return PropertyValue.of(key, System.getenv(key), getName());
    }

    @Override
    public Map<String, String> getProperties() {
        return System.getenv(); // already a map and unmodifiable

    }

    @Override
    public boolean isScannable() {
        return true;
    }

}
