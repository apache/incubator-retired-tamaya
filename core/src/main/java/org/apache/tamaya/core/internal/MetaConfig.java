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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.core.config.ConfigurationFormats;
import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.core.spi.ConfigurationFormat;
import org.apache.tamaya.core.spi.ResourceLoader;


import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton to read the configuration for the configuration system
 * fromMap {@code META-INF/config.properties}.
 * Created by Anatole on 17.10.2014.
 */
public final class MetaConfig {

    private static  final Logger LOG = Logger.getLogger(MetaConfig.class.getName());

    private static final MetaConfig INSTANCE = new MetaConfig();

    private Map<String,String> properties = new HashMap<>();

    private MetaConfig(){
        List<URI> propertyUris = Bootstrap.getService(ResourceLoader.class).getResources(MetaConfig.class.getClassLoader(),
                "classpath*:META-INF/config.properties");
        for(URI uri:propertyUris){
            try{
                ConfigurationFormat format = ConfigurationFormats.getFormat(uri);
                Map<String,String> read = format.readConfiguration(uri);
                properties.putAll(read);
            }
            catch(Exception e){
                LOG.log(Level.SEVERE, e, () -> "Error reading meta configuration fromMap " + uri);
            }
        }
    }

    public static String getKey(String key){
        return INSTANCE.properties.get(key);
    }

    public static String getOrDefault(String key, String defaultValue){
        return INSTANCE.properties.getOrDefault(key, defaultValue);
    }
}
