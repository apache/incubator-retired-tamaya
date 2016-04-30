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
package test.model;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Map;

/**
 * Created by atsticks on 30.04.16.
 */
public final class TestConfigAccessor {

    private TestConfigAccessor(){}

    public static Map<String,String> readAllProperties(){
        return ConfigurationProvider.getConfiguration()
                .getProperties();
    }

    public static Configuration readConfiguration(){
        return ConfigurationProvider.getConfiguration();
    }

    public static String readProperty(Configuration config, String key){
        return config.get(key);
    }
}
