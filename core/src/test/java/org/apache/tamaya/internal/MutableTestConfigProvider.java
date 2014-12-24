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
package org.apache.tamaya.internal;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.core.config.AbstractConfiguration;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

/**
 * Simple test provider that creates a mutable instance of a configuration, just using a simple map instance.
 */
public class MutableTestConfigProvider implements ConfigurationProviderSpi{
    /** The config name. */
    private static final String CONFIG_NAME = "mutableTestConfig";
    /** The config provided. */
    private MutableConfiguration testConfig;

    /**
     * COnsatructor.
     */
    public MutableTestConfigProvider(){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("dad", "Anatole");
        dataMap.put("mom", "Sabine");
        dataMap.put("sons.1", "Robin");
        dataMap.put("sons.2", "Luke");
        dataMap.put("sons.3", "Benjamin");
//        PropertySource provider = PropertySourceBuilder.of(CONFIG_NAME).addMap(dataMap).build();
        testConfig = new MutableConfiguration(dataMap, CONFIG_NAME);
    }

    @Override
    public String getConfigName(){
        return CONFIG_NAME;
    }

    @Override
    public Configuration getConfiguration(){
        return testConfig;
    }

    @Override
    public void reload() {

    }

    /**
     * Implements a simple mutable config based on a Mao instance.
     */
    private final class MutableConfiguration extends AbstractConfiguration{

		private static final long serialVersionUID = 8811989470609598218L;
		private final Map<String,String> data = new ConcurrentHashMap<>();

        MutableConfiguration(Map<String,String> data, String name){
            super(name);
            this.data.putAll(data);
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.unmodifiableMap(data);
        }

        @Override
        public boolean isMutable() {
            return true;
        }

        @Override
        public void applyChanges(ConfigChangeSet changeSet) {
            for(PropertyChangeEvent change: changeSet.getEvents()){
                if(change.getNewValue()==null){
                    this.data.remove(change.getPropertyName());
                }
                else{
                    this.data.put(change.getPropertyName(), (String) change.getNewValue());
                }
            }
            Configuration.publishChange(changeSet);
        }

        @Override
        public Configuration toConfiguration(){
            return this;
        }
    }
}
