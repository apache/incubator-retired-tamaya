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
package org.apache.tamaya.core.testdata;

import java.util.HashMap;
import java.util.Map;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.propertysource.BasePropertySource;
import static org.junit.Assert.assertEquals;

/**
 * Test provider reading properties from classpath:cfg/final/**.properties.
 */
public class TestPropertySource extends BasePropertySource {

    private static final Map<String, String> VALUES;
    static {
        VALUES = new HashMap<String, String>();
        VALUES.put("name", "Robin");
        VALUES.put("name2", "Sabine");
        VALUES.put("name3", "Lukas");
        VALUES.put("name4", "Sereina");
        VALUES.put("name5", "Benjamin");
    }


    public TestPropertySource() {
        initialzeOrdinal(100);
    }

    @Override
    public String getName() {
        return "TestPropertySource";
    }

    @Override
    public Map<String, String> getProperties() {
        return VALUES;
    }
}
