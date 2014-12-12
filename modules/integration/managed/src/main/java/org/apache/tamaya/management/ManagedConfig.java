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
package org.apache.tamaya.management;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.AggregationPolicy;

import java.util.Map;
import java.util.Set;

/**
 * Created by Anatole on 24.11.2014.
 */
public class ManagedConfig implements ManagedConfigMBean{
    @Override
    public Set<String> getConfigurationNames() {
        return null;
    }

    @Override
    public String getConfigurationInfo(String configName) {
        return null;
    }

    @Override
    public boolean isConfigurationAvailable(String configName, String envType, String context) {
        return false;
    }

    @Override
    public boolean isConfigurationLoaded(String configName, String envType, String context) {
        return false;
    }

    @Override
    public Map<String, String> getConfiguration(String configName, String envType, String context) throws ConfigException {
        return null;
    }

    @Override
    public Map<String, String> getRecursiveConfigValues(String area, String configName, String envType, String context) throws ConfigException {
        return null;
    }

    @Override
    public Map<String, String> getConfigValues(String area, String configName, String envType, String context) throws ConfigException {
        return null;
    }

    @Override
    public Map<String, String> updateConfiguration(String configName, String envType, String context, Map<String, String> values, AggregationPolicy aggregationPolicy) throws ConfigException {
        return null;
    }

    @Override
    public String getConfigurationInfo(String configName, String envType, String context) {
        return null;
    }

    @Override
    public Set<String> getAreas(String configName, String envType, String context) {
        return null;
    }

    @Override
    public Set<String> getTransitiveAreas(String configName, String envType, String context) {
        return null;
    }

    @Override
    public boolean isAreaExisting(String area, String configName, String envType, String context) {
        return false;
    }

    @Override
    public boolean isAreaEmpty(String area, String configName, String envType, String context) {
        return false;
    }
}
