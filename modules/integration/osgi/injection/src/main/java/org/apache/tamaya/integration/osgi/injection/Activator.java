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
package org.apache.tamaya.integration.osgi.general;

import org.apache.tamaya.inject.ConfigurationInjection;
import org.osgi.framework.*;
import org.osgi.service.cm.ConfigurationAdmin;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Activator that registers the Tamaya based Service Class for {@link ConfigurationAdmin},
 * using a default service priority of {@code 0}.
 */
public class Activator implements ServiceListener {

    private BundleContext context;

    ServiceRegistration<ConfigurationAdmin> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        TamayaConfigAdminImpl cm = new TamayaConfigAdminImpl(context);
        registration = context.registerService(ConfigurationAdmin.class, cm, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
        }
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
