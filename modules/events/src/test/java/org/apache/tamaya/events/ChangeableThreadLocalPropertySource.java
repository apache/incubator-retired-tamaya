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

import org.apache.tamaya.core.propertysource.BasePropertySource;

import java.util.HashMap;
import java.util.Map;


/**
 * PropertySource implementation that accesses properties that are stored on ThreadLocal level, e.g. good to use for
 * testing..
 */
public class ChangeableThreadLocalPropertySource extends BasePropertySource{

    private static final ThreadLocal<Map<String,String>> STORED_ENTRIES = new ThreadLocal<Map<String,String>>(){
        protected Map<String,String> initialValue(){
            return new HashMap<>();
        }
    };

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Map<String, String> getProperties() {
        return null;
    }

    public static String put(String key, String value){
        return STORED_ENTRIES.get().put(key,value);
    }

    public static void putAll(Map<String,String> properties){
        STORED_ENTRIES.get().putAll(properties);
    }

}
