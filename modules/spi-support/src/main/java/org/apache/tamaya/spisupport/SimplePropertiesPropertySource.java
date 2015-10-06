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
package org.apache.tamaya.spisupport;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple PropertySource, with a fixed ordinal that reads a .properties file.
 */
public final class SimplePropertiesPropertySource extends BasePropertySource {

    private static final Logger LOGGER = Logger.getLogger(SimplePropertiesPropertySource.class.getName());
    private URL url;
    private Map<String, String> properties = new HashMap<>();
    private final int priority;

    public SimplePropertiesPropertySource(URL url, int priority){
        this(null, url, priority);
    }

    public SimplePropertiesPropertySource(String rootContext, URL url, int priority){
        this.priority = priority;
        this.url = Objects.requireNonNull(url);
        try(InputStream is = url.openStream();){
            Properties props = new Properties();
            props.load(is);
            for(Map.Entry en: props.entrySet()){
                if(rootContext!=null){
                    String prefix = rootContext;
                    if(!prefix.endsWith(".")){
                        prefix += ".";
                    }
                    this.properties.put(prefix + en.getKey().toString(), en.getValue().toString());
                } else{
                    this.properties.put(en.getKey().toString(), en.getValue().toString());
                }
            }
        }
        catch(Exception e){
            LOGGER.log(Level.WARNING, "Failed to read properties from " + url, e);
        }
    }

    @Override
    public int getOrdinal(){
        return priority;
    }

    @Override
    public String getName() {
        return url.toExternalForm();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

}
