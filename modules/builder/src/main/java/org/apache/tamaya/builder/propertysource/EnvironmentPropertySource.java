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
package org.apache.tamaya.builder.propertysource;

import org.apache.tamaya.builder.spi.PropertySource;
import org.apache.tamaya.builder.spi.PropertyValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This {@link PropertySource} provides all Properties which are set
 * via
 * {@code export myprop=myval} on UNIX Systems or
 * {@code set myprop=myval} on Windows. You can disable this feature by setting {@code tamaya.envprops.disable}
 * or {@code tamaya.defaults.disable}.
 */
public class EnvironmentPropertySource implements PropertySource {

    private static final Logger LOG = Logger.getLogger(EnvironmentPropertySource.class.getName());

    /**
     * default ordinal for {@link EnvironmentPropertySource}
     */
    public static final int DEFAULT_ORDINAL = 300;

    private final boolean disabled = evaluateDisabled();

    private boolean evaluateDisabled() {
        String value = System.getProperty("tamaya.envprops.disable");
        if(value==null){
            value = System.getenv("tamaya.envprops.disable");
        }
        if(value==null){
            value = System.getProperty("tamaya.defaults.disable");
        }
        if(value==null){
            value = System.getenv("tamaya.defaults.disable");
        }
        if(value==null){
            return false;
        }
        return value.isEmpty() || Boolean.parseBoolean(value);
    }

    @Override
    public int getOrdinal() {
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
        return PropertyValue.of(key, System.getenv(key), getName());
    }

    @Override
    public Map<String, String> getProperties() {
        if(disabled){
            return Collections.emptyMap();
        }
        Map<String, String> entries = new HashMap<>(System.getenv());
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            entries.put("_" + entry.getKey() + ".source", getName());
        }
        return entries;
    }

    @Override
    public boolean isScannable() {
        return true;
    }

}
