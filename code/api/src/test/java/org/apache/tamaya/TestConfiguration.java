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
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.mockito.Mockito;

/**
 * Test Configuration class, that is used to testdata the default methods
 * provided by the API.
 */
public class TestConfiguration implements Configuration {

    private static final Map<String, Object> VALUES;

    static {
        VALUES = new HashMap<>();
        VALUES.put("long", Long.MAX_VALUE);
        VALUES.put("int", Integer.MAX_VALUE);
        VALUES.put("double", Double.MAX_VALUE);
        VALUES.put("float", Float.MAX_VALUE);
        VALUES.put("short", Short.MAX_VALUE);
        VALUES.put("byte", Byte.MAX_VALUE);
        VALUES.put("String", "aStringValue");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, TypeLiteral<T> type) {
        if (type.getRawType().equals(Long.class)) {
            return (T) VALUES.get(key);
        } else if (type.getRawType().equals(Integer.class)) {
            return (T) VALUES.get(key);
        } else if (type.getRawType().equals(Double.class)) {
            return (T) VALUES.get(key);
        } else if (type.getRawType().equals(Float.class)) {
            return (T) VALUES.get(key);
        } else if (type.getRawType().equals(Short.class)) {
            return (T) VALUES.get(key);
        } else if (type.getRawType().equals(Byte.class)) {
            return (T) VALUES.get(key);
        } else if (type.getRawType().equals(Boolean.class)) {
            if ("booleanTrue".equals(key)) {
                return (T) Boolean.TRUE;
            } else {
                return (T) Boolean.FALSE;
            }
        } else if (type.getRawType().equals(String.class)) {
            return (T) VALUES.get(key);
        }
        throw new ConfigException("No such property: " + key);
    }

    @Override
    public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
        T val = get(key, type);
        if (val == null) {
            return defaultValue;
        }
        return val;
    }

    @Override
    public ConfigurationContext getContext() {
        return Mockito.mock(ConfigurationContext.class);
    }

    @Override
    public Map<String, String> getProperties() {
        // run toString on each value of the (key, value) set in VALUES
        return VALUES.entrySet().stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().toString()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TestConfiguration)) {
            return false;
        }
        TestConfiguration that = (TestConfiguration) o;
        return that.getProperties().equals(this.getProperties());
    }

    @Override
    public int hashCode() {
        return VALUES.hashCode();
    }

}
