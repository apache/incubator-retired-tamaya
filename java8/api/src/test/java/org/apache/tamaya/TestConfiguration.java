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

/**
 * Test Configuration class, that is used to testdata the default methods provided by the API.
 */
public class TestConfiguration implements Configuration {

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
    public String get(String key) {
        return VALUES.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        if(type.equals(Long.class)){
            return (T)(Object)Long.MAX_VALUE;
        }
        else if(type.equals(Integer.class)){
            return (T)(Object) Integer.MAX_VALUE;
        }
        else if(type.equals(Double.class)){
            return (T)(Object) Double.MAX_VALUE;
        }
        else if(type.equals(Float.class)){
            return (T)(Object) Float.MAX_VALUE;
        }
        else if(type.equals(Short.class)){
            return (T)(Object) Short.MAX_VALUE;
        }
        else if(type.equals(Byte.class)){
            return (T)(Object) Byte.MAX_VALUE;
        }
        else if(type.equals(Boolean.class)){
            if("booleanTrue".equals(key)) {
                return (T)(Object) Boolean.TRUE;
            }
            else{
                return (T)(Object) Boolean.FALSE;
            }
        }
        else if(type.equals(String.class)){
            return (T)(Object) "aStringValue";
        }
        throw new ConfigException("No such property: " + key);
    }

    @Override
    public Map<String, String> getProperties() {
        return null;
    }
}
