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

import javax.config.spi.ConfigSource;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Created by atsticks on 18.10.16.
 */
public class TestConfigSource implements ConfigSource {

    private String id;
    private int ordinal;

    public TestConfigSource() {
        this("TestConfigSource", 0);
    }

    public TestConfigSource(String id, int ordinal) {
        this.id = id;
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getName() {
        return id != null ? id : "TestConfigSource";
    }

    @Override
    public String getValue(String key) {
        if(key.endsWith("[meta]")){
            return "ordinal=" + String.valueOf(getOrdinal()) + '\n'+
                    "createdAt=" + String.valueOf(new Date());
        }
        return key + "Value";
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.emptyMap();
    }

}
