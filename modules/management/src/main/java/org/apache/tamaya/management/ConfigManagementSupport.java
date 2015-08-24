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


import org.apache.tamaya.spi.ServiceContextManager;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Accessor singleton for the JMX configuration support module.
 */
public class ConfigManagementSupport {

    /** The logger used. */
    private final static Logger LOG = Logger.getLogger(ConfigManagementSupport.class.getName());

    /**
     * Private singleton constructor.
     */
    private ConfigManagementSupport(){}

    /**
     * Creates a new instance of a {@link ManagedConfigMBean} instance. This method uses the
     * current {@link ServiceContextManager} to resolve the implementation to be used.
     * @return a new ManagedConfigMBean instance, or null
     * @throws org.apache.tamaya.ConfigException if there are multiple service implementations with the
     *          maximum priority.
     */
    private static ManagedConfigMBean createMBean(){
        return ServiceContextManager.getServiceContext()
                .getService(ManagedConfigMBean.class);
    }

    /**
     * Registers a new instance of {@link ManagedConfigMBean} mbean for accessing config documentation into the local platform
     * mbean server.
     * @return the registered ObjectName, or null, if no bean could be created.
     */
    public static ObjectName registerMBean() {
        return registerMBean(null);
    }

    /**
     * Registers the {@link ManagedConfigMBean} mbean for accessing config documentation into the local platform
     * mbean server.
     * @param context An optional context parameter to be added to the object name.
     * @return the registered ObjectName, or null, if no bean could be created.
     */
    public static ObjectName registerMBean(String context) {
        try{
            ManagedConfigMBean configMbean = createMBean();
            if(configMbean==null){
                return null;
            }
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName on = context==null?new ObjectName("org.apache.tamaya.managemet:type=ManagedConfigMBean"):
                    new ObjectName("org.apache.tamaya.management:type=ManagedConfigMBean,context="+context);
            try{
                mbs.getMBeanInfo(on);
                LOG.info("Cannot register mbean " + on + ": already existing.");
                return on;
            } catch(InstanceNotFoundException e) {
                LOG.info("Registering mbean " + on + "...");
                mbs.registerMBean(configMbean, on);
                return on;
            }
        } catch(Exception e){
            LOG.log(Level.WARNING, "Failed to register ManagedConfigMBean.", e);
        }
        return null;
    }

    /**
     * Unregisters a new instance of {@link ManagedConfigMBean} mbean for accessing config documentation
     * into the local platform mbean server.
     * @return the unregistered ObjectName, or null, if no bean could be found.
     */
    public static ObjectName unregisterMBean() {
        return unregisterMBean(null);
    }

    /**
     * Unegisters the {@link ManagedConfigMBean} mbean for accessing config documentation into the local
     * platform mbean server.
     * @param context An optional context parameter to be added to the object name.
     * @return the unregistered ObjectName, or null, if no bean could be created.
     */
    public static ObjectName unregisterMBean(String context) {
        try{
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName on = context==null?new ObjectName("org.apache.tamaya.managemet:type=ManagedConfigMBean"):
                    new ObjectName("org.apache.tamaya.management:type=ManagedConfigMBean,context="+context);
            try{
                mbs.unregisterMBean(on);
                LOG.info("Unregistered mbean " + on + ".");
                return on;
            } catch(InstanceNotFoundException e) {
                LOG.log(Level.INFO, "Unregistering mbean " + on + " failed.", e);
            }
        } catch(Exception e){
            LOG.log(Level.WARNING, "Failed to unregister ManagedConfigMBean.", e);
        }
        return null;
    }
}

