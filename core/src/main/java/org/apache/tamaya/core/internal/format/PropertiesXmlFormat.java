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
package org.apache.tamaya.core.internal.format;

import org.apache.tamaya.core.spi.ConfigurationFormat;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;


public class PropertiesXmlFormat implements ConfigurationFormat{

    @Override
    public String getFormatName(){
        return "xml-properties";
    }

    @Override
    public boolean isAccepted(URI resource){
        String path = resource.getPath();
        return path != null && path.endsWith(".xml");
    }

    @Override
    public Map<String,String> readConfiguration(URI resource) {
        if (isAccepted(resource)) {
            try (InputStream is = resource.toURL().openStream()) {
                Properties p = new Properties();
                p.loadFromXML(is);
                return Map.class.cast(p);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return Collections.emptyMap();
    }

}
