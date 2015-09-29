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
package org.apache.tamaya.examples.simple;

import org.apache.tamaya.core.propertysource.BasePropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Anatole on 20.03.2015.
 */
public class SimplePropertySource extends BasePropertySource{

    private Map<String,String> props = new HashMap<>();

    public SimplePropertySource() throws IOException {
        URL url = ClassLoader.getSystemClassLoader().getResource("META-INF/MyOtherConfigProperties.properties");
        Properties properties = new Properties();
        try(InputStream is = url.openStream()){
            properties.load(is);
        }
        finally{
            properties.forEach((k,v) -> props.put(k.toString(), v.toString()));
            props = Collections.unmodifiableMap(props);
        }
    }

    @Override
    public String getName() {
        return "META-INF/MyOtherConfigProperties.properties";
    }

    @Override
    public Map<String, String> getProperties() {
        return props;
    }
}
