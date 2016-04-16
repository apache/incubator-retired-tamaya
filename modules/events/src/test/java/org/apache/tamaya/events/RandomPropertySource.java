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

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.HashMap;
import java.util.Map;

/**
 * PropertySource that provides a randome entry, different on each access!
 */
public class RandomPropertySource implements PropertySource{

    private Map<String, String> data = new HashMap<>();

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getName() {
        return "random";
    }

    @Override
    public PropertyValue get(String key) {
        if(key.equals("random.new")){
            return PropertyValue.of(key, String.valueOf(Math.random()),getName());
        }
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        synchronized(data) {
            data.put("random.new", String.valueOf(Math.random()));
            data.put("_random.new.source", getName());
            data.put("_random.new.timestamp", String.valueOf(System.currentTimeMillis()));
            return new HashMap<>(data);
        }
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
