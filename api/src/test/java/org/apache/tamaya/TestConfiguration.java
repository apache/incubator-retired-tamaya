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

import java.util.Optional;

/**
 * Test Configuration class, that is used to test the default methods provided by the API.
 */
public class TestConfiguration implements Configuration{

    @Override
    public Optional<String> get(String key) {
        if("long".equals(key)){
            return Optional.ofNullable(String.valueOf(Long.MAX_VALUE));
        }
        else if("int".equals(key)){
            return Optional.ofNullable(String.valueOf(Integer.MAX_VALUE));
        }
        else if("double".equals(key)){
            return Optional.ofNullable(String.valueOf(Double.MAX_VALUE));
        }
        else if("float".equals(key)){
            return Optional.ofNullable(String.valueOf(Float.MAX_VALUE));
        }
        else if("short".equals(key)){
            return Optional.ofNullable(String.valueOf(Short.MAX_VALUE));
        }
        else if("byte".equals(key)){
            return Optional.ofNullable(String.valueOf(Byte.MAX_VALUE));
        }
        else if("booleanTrue".equals(key)){
            return Optional.ofNullable("true");
        }
        else if("booleanFalse".equals(key)){
            return Optional.ofNullable("false");
        }
        else if("String".equals(key)){
            return Optional.ofNullable("aStringValue");
        }
        return Optional.ofNullable("noValue");
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

}
