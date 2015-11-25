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
package org.apache.tamaya.integration.osgi.injection;

import org.apache.tamaya.inject.ConfigurationInjection;
import org.osgi.framework.*;
import org.osgi.util.tracker.ServiceTracker;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Activator that injects Tamaya configuration into OSGI Services.
 */
public class Activator implements ServiceListener, BundleActivator {

    private static final Logger LOG = Logger.getLogger(Activator.class.getName());

    private BundleContext context;
    private ServiceTracker<Object, Object> serviceTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        serviceTracker = new ServiceTracker<Object, Object>(context, Object.class, null) {
            @Override
            public Object addingService(ServiceReference<Object> reference) {
                Object service = context.getService(reference);
                Object pidObj = reference.getProperty(Constants.SERVICE_PID);
                if (pidObj instanceof String) {
                    String pid = (String) pidObj;
                    try {
                        ConfigurationInjection.getConfigurationInjector().configure(service);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Error configuring Service: " + service, e);
                    }
                } else {
                    LOG.log(Level.SEVERE, "Unsupported pid: " + pidObj);
                }
                return service;
            }

            @Override
            public void removedService(ServiceReference<Object> reference, Object service) {
                context.ungetService(reference);
            }
        };
        serviceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        this.serviceTracker.close();
        this.serviceTracker = null;
        this.context = null;
    }

    @Override
    public void serviceChanged(ServiceEvent serviceEvent) {
        if(ServiceEvent.MODIFIED==serviceEvent.getType() ||
                ServiceEvent.REGISTERED==serviceEvent.getType()){
            ServiceReference ref = serviceEvent.getServiceReference();
            Object service = context.getService(ref);
            ConfigurationInjection.getConfigurationInjector().configure(service);
        }
    }
}
