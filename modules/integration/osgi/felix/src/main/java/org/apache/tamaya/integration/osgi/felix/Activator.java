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
package org.apache.tamaya.integration.osgi.felix;

import org.apache.felix.cm.PersistenceManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import java.util.Hashtable;

/**
 * Created by atsticks on 27.10.15.
 */
public class Activator {

    private ServiceRegistration<PersistenceManager> pmRegistration;

    public Activator(BundleContext bundleContext){
        TamayaPersistenceManager tpm = new TamayaPersistenceManager(bundleContext);
        Hashtable props = new Hashtable();
        props.put( Constants.SERVICE_PID, tpm.getClass().getName() );
        props.put( Constants.SERVICE_DESCRIPTION, "Apache Tamaya Persistence Manager" );
        props.put( Constants.SERVICE_VENDOR, "Apache Software Foundation" );
        Integer ranking = Integer.valueOf(Integer.MIN_VALUE + 10);
        // TODO Make ranking configurable...
        props.put( Constants.SERVICE_RANKING, ranking );
        pmRegistration = bundleContext.registerService( PersistenceManager.class.getName(), tpm, props );
    }
}
