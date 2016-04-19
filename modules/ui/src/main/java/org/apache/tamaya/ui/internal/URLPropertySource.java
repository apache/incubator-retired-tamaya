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
package org.apache.tamaya.ui.internal;

import org.apache.tamaya.spisupport.BasePropertySource;
import org.apache.tamaya.spisupport.MapPropertySource;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple property source, used for internationalization.
 */
final class URLPropertySource extends BasePropertySource{

    private static final Logger LOG = Logger.getLogger(URLPropertySource.class.getName());
    private URL url;
    private Map<String, String> properties;

    public URLPropertySource(URL url){
        this.url = Objects.requireNonNull(url);
        load();
    }

    /**
     * Loads/reloads the properties from the URL. If loading of the properties failed the previus state is preserved,
     * unless there is no such state. In this case an empty map is assigned.
     */
    public void load(){
        try(InputStream is = url.openStream()) {
            Properties props = new Properties();
            if (url.getFile().endsWith(".xml")) {
                props.loadFromXML(is);
            } else {
                props.load(is);
            }
            properties = Collections.unmodifiableMap(MapPropertySource.getMap(props));
        }
        catch(Exception e){
            LOG.log(Level.WARNING, "Failed to read config from "+url,e);
            if(properties==null) {
                properties = Collections.emptyMap();
            }
        }
    }

    @Override
    public String getName() {
        return url.toString();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
