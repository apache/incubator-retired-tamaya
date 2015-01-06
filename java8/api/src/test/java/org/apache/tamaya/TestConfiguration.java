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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Test Configuration class, that is used to testdata the default methods provided by the API.
 */
public class TestConfiguration implements Configuration{

    private static final Map<String, String> VALUES;
    static {
        VALUES = new HashMap<String, String>();
        VALUES.put("long", String.valueOf(Long.MAX_VALUE));
        VALUES.put("int", String.valueOf(Integer.MAX_VALUE));
        VALUES.put("double", String.valueOf(Double.MAX_VALUE));
        VALUES.put("float", String.valueOf(Float.MAX_VALUE));
        VALUES.put("short", String.valueOf(Short.MAX_VALUE));
        VALUES.put("byte", String.valueOf(Byte.MAX_VALUE));
        VALUES.put("booleanTrue", "true");
        VALUES.put("booleanFalse", "false");
        VALUES.put("String", "aStringValue");
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(VALUES.get(key));
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        if(type.equals(Long.class)){
            return Optional.class.cast(Optional.ofNullable(Long.MAX_VALUE));
        }
        else if(type.equals(Integer.class)){
            return Optional.class.cast(Optional.ofNullable(Integer.MAX_VALUE));
        }
        else if(type.equals(Double.class)){
            return Optional.class.cast(Optional.ofNullable(Double.MAX_VALUE));
        }
        else if(type.equals(Float.class)){
            return Optional.class.cast(Optional.ofNullable(Float.MAX_VALUE));
        }
        else if(type.equals(Short.class)){
            return Optional.class.cast(Optional.ofNullable(Short.MAX_VALUE));
        }
        else if(type.equals(Byte.class)){
            return Optional.class.cast(Optional.ofNullable(Byte.MAX_VALUE));
        }
        else if(type.equals(Boolean.class)){
            if("booleanTrue".equals(key)) {
                return Optional.class.cast(Optional.ofNullable(Boolean.TRUE));
            }
            else{
                return Optional.class.cast(Optional.ofNullable(Boolean.FALSE));
            }
        }
        else if(type.equals(String.class)){
            return Optional.class.cast(Optional.ofNullable("aStringValue"));
        }
        throw new ConfigException("No such property: " + key);
    }

    @Override
    public Map<String, String> getProperties() {
        return null;
    }
}
