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
package org.apache.tamaya.core.internal.env;

import org.apache.tamaya.core.env.ConfiguredSystemProperties;
import org.apache.tamaya.core.env.EnvironmentBuilder;

import java.net.InetAddress;
import java.util.*;
import java.util.Map.Entry;

import org.apache.tamaya.Environment;
import org.apache.tamaya.Stage;
import org.apache.tamaya.core.spi.EnvironmentProvider;

/**
 * Default {@link org.apache.tamaya.Environment}.
 */
public final class InitialEnvironmentProvider implements EnvironmentProvider{

	public static final String STAGE_PROP = "org.apache.tamaya.stage";
    public static final Stage DEFAULT_STAGE = Stage.DEVELOPMENT;
    private Map<String,Environment> environments = new HashMap<>();

	public InitialEnvironmentProvider() {
        EnvironmentBuilder builder = EnvironmentBuilder.of(getEnvironmentType(), getEnvironmentType());
        Properties props = System.getProperties();
        if(props instanceof ConfiguredSystemProperties){
            props = ((ConfiguredSystemProperties)props).getInitialProperties();
        }
        String stageValue =  props.getProperty(STAGE_PROP);
        Stage stage = DEFAULT_STAGE;
        if (stageValue != null) {
            stage = Stage.valueOf(stageValue);
        }
        builder.setStage(stage);
        // Copy system properties....
        // TODO filter properties
        for (Entry<Object, Object> en : props.entrySet()) {
            builder.set(en.getKey().toString(), en.getValue().toString());
        }
        builder.set("timezone", TimeZone.getDefault().getID());
        builder.set("locale", Locale.getDefault().toString());
        try {
            builder.set("host", InetAddress.getLocalHost().toString());
        } catch (Exception e) {
// log warning
        }
        // Copy env properties....
        for (Entry<String, String> en : System.getenv().entrySet()) {
            builder.set(en.getKey(), en.getValue());
        }
        environments.put("root", builder.build());
	}

    @Override
	public Environment getEnvironment(Environment env) {
        return environments.get("root");
	}

    @Override
    public Set<String> getEnvironmentContexts() {
        return new HashSet<>(environments.keySet());
    }

    @Override
    public String getEnvironmentType() {
        return "root";
    }

    @Override
    public boolean isEnvironmentActive() {
        return true;
    }

}
