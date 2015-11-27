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
package org.apache.tamaya.integration.osgi.base;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Activator that registers the Tamaya based Service Class for {@link ConfigurationAdmin},
 * using a default service priority of {@code 0}. This behaviour is configurable based on OSGI properties:
 * <ul>
 *     <li><b>org.tamaya.integration.osgi.cm.ranking, type: int</b> allows to configure the OSGI service ranking for
 *     Tamaya based ConfigurationAdmin instance. The default ranking used is 10.</li>
 *     <li><b>org.tamaya.integration.osgi.cm.override, type: boolean</b></li> allows to configure if Tamaya should
 *     register its ConfigAdmin service. Default is true.
 * </ul>
 */
public class Activator implements BundleActivator {

    private static final String SERVICE_RANKING_PROP = "org.tamaya.integration.osgi.cm.ranking";

    private static final String SERVICE_OVERRIDE_PROP = "org.tamaya.integration.osgi.cm.override";

    private static final Integer DEFAULT_RANKING = 10;

    private BundleContext context;

    ServiceRegistration<ConfigurationAdmin> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        String val = context.getProperty(SERVICE_OVERRIDE_PROP);
        if(val==null?true:Boolean.parseBoolean(val)){
            Dictionary<String, Object> props = new Hashtable<>();
            String ranking = context.getProperty(SERVICE_RANKING_PROP);
            if (ranking == null) {
                props.put(Constants.SERVICE_RANKING, DEFAULT_RANKING);
            } else {
                props.put(Constants.SERVICE_RANKING, Integer.valueOf(ranking));
            }
            this.context = context;
            TamayaConfigAdminImpl cm = new TamayaConfigAdminImpl(context);
            registration = context.registerService(ConfigurationAdmin.class, cm, props);
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
        }
    }

}
