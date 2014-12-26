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
package org.apache.tamaya.metamodel.environment;

import org.apache.tamaya.core.spi.EnvironmentProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Environment provider used by some tests.
 */
public class TestEnvironmentProvider implements EnvironmentProvider {
    private Map<String, String> data = new HashMap<>();

    public TestEnvironmentProvider(){
        data.put("user.country", System.getProperty("user.country"));
        data.put("java.version", System.getProperty("java.version"));
        data = Collections.unmodifiableMap(data);
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public Map<String, String> getEnvironmentData() {
        return data;
    }
}
