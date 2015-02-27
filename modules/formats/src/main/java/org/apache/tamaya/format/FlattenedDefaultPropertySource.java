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
package org.apache.tamaya.format;

import org.apache.tamaya.spi.PropertySource;
import sun.reflect.generics.reflectiveObjects.LazyReflectiveObjectGenerator;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Flattened default PropertySource that uses the flattened config data read from an URL by a
 * ${@link org.apache.tamaya.format.ConfigurationFormat}.
 */
public class FlattenedDefaultPropertySource implements PropertySource {
    private static final Logger LOG = Logger.getLogger(FlattenedDefaultPropertySource.class.getName());
    private Map<String, String> defaultSection;
    private ConfigurationData data;

    /*
     * Constructor, uses hereby the flattened config data read from an URL by a
     * ${@link org.apache.tamaya.format.ConfigurationFormat}, and if not present falls back to the default section.
     */
    public FlattenedDefaultPropertySource(ConfigurationData data) {
        this.defaultSection = data.getSection(ConfigurationData.FLATTENED_SECTION_NAME);
        if (this.defaultSection == null) {
            this.defaultSection = data.getDefaultSection();
        }
        this.defaultSection = Collections.unmodifiableMap(this.defaultSection);
        this.data = data;
    }

    @Override
    public String getName(){
        String name = this.defaultSection.get("[meta].name");
        if(name==null){
            name = this.data.getResource();
        }
        if(name==null){
            name = getClass().getSimpleName();
        }
        return name;
    }

    @Override
    public int getOrdinal() {
        String ordinalValue = this.defaultSection.get(PropertySource.TAMAYA_ORDINAL);
        if(ordinalValue!=null){
            try{
                return Integer.parseInt(ordinalValue.trim());
            }
            catch(Exception e){
                LOG.log(Level.WARNING, e, () -> "Failed to parse Tamaya ordinal from " + data.getResource());
            }
        }
        return 0;
    }

    @Override
    public String get(String key) {
        return defaultSection.get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        return defaultSection;
    }
}
