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
package org.apache.tamaya.metamodel.environment.internal;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.core.env.ConfiguredSystemProperties;
import org.apache.tamaya.environment.spi.ContextDataProvider;
import org.apache.tamaya.metamodel.environment.RuntimeContextBuilder;
import org.apache.tamaya.metamodel.environment.spi.EnvironmentProvider;

/**
 * Default {@link org.apache.tamaya.metamodel.environment.RuntimeContext}.
 */
public final class InitialEnvironmentProvider implements ContextDataProvider{

	private Map<String,String> contextData = new HashMap<>();

	public InitialEnvironmentProvider() {
        Properties props = System.getProperties();
        if(props instanceof ConfiguredSystemProperties){
            props = ((ConfiguredSystemProperties)props).getInitialProperties();
        }
        String stageValue =  props.getProperty(RuntimeContextBuilder.STAGE_PROP);
        contextData.put(RuntimeContextBuilder.STAGE_PROP, stageValue);
        contextData.put("timezone", TimeZone.getDefault().getID());
        contextData.put("locale", Locale.getDefault().toString());
        try {
            contextData.put("host", InetAddress.getLocalHost().toString());
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, e, () -> "Failed to evaluate hostname.");
        }
        // Copy env properties....
        for (Entry<String, String> en : System.getenv().entrySet()) {
            contextData.put(en.getKey(), en.getValue());
        }
        contextData = Collections.unmodifiableMap(contextData);
	}

    @Override
    public boolean isActive(){
        return true;
    }

    @Override
	public Map<String,String> getContextData() {
        return contextData;
	}

}
