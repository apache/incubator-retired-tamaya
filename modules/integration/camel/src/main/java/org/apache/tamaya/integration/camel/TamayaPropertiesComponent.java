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
package org.apache.tamaya.integration.camel;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Properties;

/**
 * Default Camel PropertiesComponent that additionally has cfg and tamaya prefixes configured for resolution of
 * entries from tamaya.
 */
public class TamayaPropertiesComponent extends PropertiesComponent{

    /**
     * Constructor similar to parent.
     */
    public TamayaPropertiesComponent(){
        super();
        addFunction(new TamayaPropertyResolver("tamaya"));
        addFunction(new TamayaPropertyResolver("cfg"));
        setTamayaOverrides(true);
    }

    /**
     * Constructor similar to parent.
     */
    public TamayaPropertiesComponent(String ... locations){
        super(locations);
        addFunction(new TamayaPropertyResolver("tamaya"));
        addFunction(new TamayaPropertyResolver("cfg"));
        setTamayaOverrides(true);
    }

    /**
     * Constructor similar to parent.
     */
    public TamayaPropertiesComponent(String location){
        super(location);
        addFunction(new TamayaPropertyResolver("tamaya"));
        addFunction(new TamayaPropertyResolver("cfg"));
        setTamayaOverrides(true);
    }

    /**
     * Apply the current Tamaya properties (configuration) as override properties evaluated first by camel before
     * evaluating other uris.
     * @param enabled flag to define if tamaya values override everything else.
     */
    public void setTamayaOverrides(boolean enabled){
        if(enabled){
            Properties props = new Properties();
            props.putAll(ConfigurationProvider.getConfiguration().getProperties());
            setOverrideProperties(props);
        } else{
            setOverrideProperties(null);
        }
    }
}
