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
package org.apache.tamaya.resolver;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Anatole on 04.01.2015.
 */
public class MyTestPropertySource implements PropertySource{

    private Map<String,String> properties = new HashMap<>();

    public MyTestPropertySource(){
        properties.put("Expression Only", "${java.version}");
        properties.put("Expression Only (prefixed)", "${sys:java.version}");
        properties.put("Before Text", "My Java version is ${java.version}");
        properties.put("Before Text (prefixed)", "My Java version is ${sys:java.version}");
        properties.put("Before and After Text", "My Java version is ${java.version}.");
        properties.put("Before and After Text (prefixed)", "My Java version is ${sys:java.version}.");
        properties.put("Multi-expression", "Java version ${sys:java.version} and line.separator ${line.separator}.");
    }

    @Override
    public int getOrdinal() {
        return 0;
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(properties.get(key));
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
