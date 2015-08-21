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


import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Anatole on 24.11.2014.
 */
public class ManagedConfig implements ManagedConfigMBean{

    /** The logger used. */
    private final static Logger LOG = Logger.getLogger(ManagedConfig.class.getName());

    @Override
    public String getConfigurationInfo() {
        return ConfigurationProvider.getConfiguration().query(ConfigurationFunctions.jsonInfo());
    }

    @Override
    public Map<String, String> getConfiguration() {
        return ConfigurationProvider.getConfiguration().getProperties();
    }

    @Override
    public Map<String, String> getConfigurationArea(String area, boolean recursive) {
        return ConfigurationProvider.getConfiguration().with(ConfigurationFunctions.section(area, recursive)).getProperties();
    }

    @Override
    public Set<String> getAreas() {
        return ConfigurationProvider.getConfiguration().query(ConfigurationFunctions.sections());
    }

    @Override
    public Set<String> getTransitiveAreas() {
        return ConfigurationProvider.getConfiguration().query(ConfigurationFunctions.transitiveSections());
    }

    @Override
    public boolean isAreaExisting(String area) {
        return !ConfigurationProvider.getConfiguration().with(
                  ConfigurationFunctions.section(area)).getProperties().isEmpty();
    }

    /**
     * Registers the {@link ManagedConfigMBean} mbean for accessing config documentation into the local platform
     * mbean server.
     */
    public static void registerMBean() {
        registerMBean(null);
    }

    /**
     * Registers the {@link ManagedConfigMBean} mbean for accessing config documentation into the local platform
     * mbean server.
     */
    public static void registerMBean(String context) {
        try{
            ManagedConfigMBean configMbean = ServiceContextManager.getServiceContext()
                    .getService(ManagedConfigMBean.class);
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName on = context==null?new ObjectName("org.apache.tamaya.managemet:type=ManagedConfigMBean"):
                    new ObjectName("org.apache.tamaya.management:type=ManagedConfigMBean,context="+context);
            try{
                mbs.getMBeanInfo(on);
                LOG.warning("Cannot register mbean " + on + ": already existing.");
            } catch(InstanceNotFoundException e) {
                LOG.info("Registering mbean " + on + "...");
                mbs.registerMBean(configMbean, on);
            }
        } catch(Exception e){
            Logger.getLogger(ManagedConfig.class.getName()).log(Level.WARNING,
                    "Failed to register ManagedConfigMBean.", e);
        }
    }
}

