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
package org.apache.tamaya.examples.custompropertysource;

import org.apache.tamaya.spisupport.propertysource.BasePropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SimplePropertySource extends BasePropertySource {

    public static final String CONFIG_PROPERTIES_LOCATION = "META-INF/MyOtherConfigProperties.properties";
    private Map<String,PropertyValue> props = new HashMap<>();

    public SimplePropertySource() throws IOException {
        URL url = ClassLoader.getSystemClassLoader().getResource(CONFIG_PROPERTIES_LOCATION);
        Properties properties = new Properties();

        try(InputStream is = url.openStream()){
            properties.load(is);

            for(Map.Entry en: properties.entrySet()){
                props.put(en.getKey().toString(),
                        PropertyValue.createValue(en.getKey().toString(), en.getValue().toString())
                        .setMeta("source", getName()));
            }
        }
        finally{
            props = Collections.unmodifiableMap(props);
        }
    }

    @Override
    public String getName() {
        return CONFIG_PROPERTIES_LOCATION;
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return props;
    }
}