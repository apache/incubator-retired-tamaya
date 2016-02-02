/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.tamaya.integration.cdi.cfg;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import javax.enterprise.inject.Vetoed;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anatole on 17.09.2015.
 */
@Vetoed
class ProvidedPropertySource implements PropertySource{

    final Map<String,String> config = new HashMap<>();

    public ProvidedPropertySource(){
        config.put("a.b.c.key3", "keys current a.b.c.key3");
        config.put("a.b.c.key4", "keys current a.b.c.key4");
        config.put("{meta}source.type:"+getClass().getName(), "PropertySourceProvider");
    }

    @Override
    public int getOrdinal() {
        return 10;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public PropertyValue get(String key) {
        return PropertyValue.of(key, config.get(key), getName());
    }

    @Override
    public Map<String, String> getProperties() {
        return config;
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
