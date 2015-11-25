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

import org.apache.felix.cm.PersistenceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

/**
 * Activator that registers the Tamaya implementation of {@link org.apache.felix.cm.PersistenceManager},
 * hereby overriding the version registered by felix CM by default.
 */
public class Activator implements BundleActivator{

    /**
     * Our registration, used on stop.
     */
    private ServiceRegistration<PersistenceManager> pmRegistration;


    @Override
    public void start(BundleContext bundleContext) throws Exception {
        TamayaPersistenceManager tpm = new TamayaPersistenceManager(bundleContext);
        Hashtable props = new Hashtable();
        props.put( Constants.SERVICE_PID, tpm.getClass().getName() );
        props.put( Constants.SERVICE_DESCRIPTION, "Apache Tamaya Persistence Manager" );
        props.put( Constants.SERVICE_VENDOR, "Apache Software Foundation" );
        Integer ranking = 10;
        // TODO Make ranking configurable...
        props.put( Constants.SERVICE_RANKING, ranking );
        pmRegistration = bundleContext.registerService( PersistenceManager.class, tpm, props );
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        if(pmRegistration!=null){
            pmRegistration.unregister();
            pmRegistration = null;
        }
    }
}
