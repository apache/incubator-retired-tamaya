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
package org.apache.tamaya.examples.fileobserver;

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.Configuration;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Anatole on 24.03.2015.
 */
public class TestConfigView implements ConfigOperator{

    private static final TestConfigView INSTANCE = new TestConfigView();

    private TestConfigView(){}

    public static ConfigOperator of(){
        return INSTANCE;
    }

    @Override
    public Configuration operate(Configuration config) {
        return new Configuration() {
            @Override
            public Map<String, String> getProperties() {
                return config.getProperties().entrySet().stream().filter(e -> e.getKey().startsWith("test")).collect(
                        Collectors.toMap(en -> en.getKey(), en -> en.getValue()));
            }
        };
    }
}
