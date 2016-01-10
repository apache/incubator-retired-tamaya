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
package org.apache.tamaya.integration.osgi;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.PropertyMatcher;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * Tamaya based implementation of an OSGI {@link Configuration}.
 */
public class TamayaConfigurationImpl implements Configuration {
    private static final Logger LOG = Logger.getLogger(TamayaConfigurationImpl.class.getName());
    private final String pid;
    private final String factoryPid;
    private Map<String, String> properties = new HashMap<>();
    private org.apache.tamaya.Configuration config;

    /**
     * Constructor.
     * @param confPid the OSGI pid
     * @param factoryPid the factory pid
     * @param configRootMapper the mapper that maps the pids to a tamaya root section.
     * @param parent the (optional delegating parent, used as default).
     */
    public TamayaConfigurationImpl(String confPid, String factoryPid, OSGIConfigRootMapper configRootMapper,
                                   ConfigurationAdmin parent) {
        this.pid = confPid;
        this.factoryPid = factoryPid;
        if(parent!=null){
            try {
                Dictionary<String, Object> conf = parent.getConfiguration(confPid, factoryPid).getProperties();
                if(conf!=null) {
                    LOG.info("Configuration: Adding default parameters from parent: " + parent.getClass().getName());
                    Enumeration<String> keys = conf.keys();
                    while (keys.hasMoreElements()) {
                        String key = keys.nextElement();
                        this.properties.put(key, conf.get(key).toString());
                    }
                }
            } catch (IOException e) {
                LOG.log(Level.WARNING, "Error reading parent OSGI config.", e);
            }
        }
        this.config = ConfigurationProvider.getConfiguration();
        final String rootKey = configRootMapper.getTamayaConfigRoot(pid, factoryPid);
        LOG.info("Configuration: Evaluating Tamaya configuration for '" + rootKey + "'.");
        this.properties.putAll(
                config.with(ConfigurationFunctions.section(rootKey, true)).getProperties());
    }

    @Override
    public String getPid() {
        return pid;
    }

    @Override
    public Dictionary<String, Object> getProperties() {
        return new Hashtable<String, Object>(properties);
    }

    @Override
    public void update(Dictionary<String, ?> properties) throws IOException {
        throw new UnsupportedOperationException("Nuatability not yet supported.");
         // ConfigChangeProvider.createChangeRequest(this.config)
    }

    @Override
    public void delete() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFactoryPid() {
        return factoryPid;
    }

    @Override
    public void update() throws IOException {
        this.config = ConfigurationProvider.getConfiguration();
        this.properties = config.with(ConfigurationFunctions.filter(new PropertyMatcher() {
            @Override
            public boolean test(String key, String value) {
// TODO define name space / SPI
                return false;
            }
        })).getProperties();
    }

    @Override
    public void setBundleLocation(String location) {
    }

    @Override
    public String getBundleLocation() {
        return null;
    }

    @Override
    public long getChangeCount() {
        return 0;
    }

}
