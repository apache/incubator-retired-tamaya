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
package org.apache.tamaya.builder;

import org.apache.tamaya.spi.PropertySource;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
* Simple property source implementation using a map.
*/
public class SimplePropertySource implements PropertySource {
    /** The properties. */
    private Map<String, String> properties;
    /** The source's name. */
    private String name;

    public SimplePropertySource(String name, Map<String, String> properties){
        this.properties = new HashMap<>(properties);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public int getOrdinal(){
        String configuredOrdinal = get(TAMAYA_ORDINAL);
        if(configuredOrdinal!=null){
            try{
                return Integer.parseInt(configuredOrdinal);
            } catch(Exception e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Configured Ordinal is not an int number: " + configuredOrdinal, e);
            }
        }
        return getDefaultOrdinal();
    }

    public int getDefaultOrdinal(){
        return 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Override
    public boolean isScannable() {
        return false;
    }

    @Override
    public String toString(){
        return "SimplePropertySource(name="+name+", numProps="+properties.size()+")";
    }
}
