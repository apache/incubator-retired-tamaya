/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.tamaya.core;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Created by atsticks on 18.10.16.
 */
public class TestPropertySource implements PropertySource {

    private String id;
    private int ordinal;

    public TestPropertySource() {
        this("TestPropertySource", 0);
    }

    public TestPropertySource(String id, int ordinal) {
        this.id = id;
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return id != null ? id : "TestPropertySource";
    }

    @Override
    public PropertyValue get(String key) {
        return PropertyValue.create(key, key + "Value")
                .setMeta("source", getName())
                .setMeta("ordinal", String.valueOf(getOrdinal()))
                .setMeta("createdAt", String.valueOf(new Date()));
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isScannable() {
        return false;
    }
}
