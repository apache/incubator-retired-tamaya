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
package org.apache.tamaya.resource.internal;

import org.apache.tamaya.resource.AbstractPathPropertySourceProvider;
import org.apache.tamaya.spi.PropertySource;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Anatole on 03.03.2015.
 */
public class PathBasedPropertySourceProvider extends AbstractPathPropertySourceProvider{

    public PathBasedPropertySourceProvider() {
        super("META-INF/cfg/**/*.properties");
    }

    @Override
    protected PropertySource getPropertySource(URL url) {
        Properties props = new Properties();
        try(InputStream is = url.openStream()){
            props.load(is);
            return new PropertiesBasedPropertySource(url.toString(), props);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    private final static class PropertiesBasedPropertySource implements PropertySource{

        private String name;
        private Map<String,String> properties = new HashMap<>();

        public PropertiesBasedPropertySource(String name, Properties props) {
            this.name = name;
            props.forEach((k,v) -> this.properties.put(k.toString(), v.toString()));
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String get(String key) {
            return properties.get(key);
        }

        @Override
        public Map<String, String> getProperties() {
            return properties;
        }
    }
}
