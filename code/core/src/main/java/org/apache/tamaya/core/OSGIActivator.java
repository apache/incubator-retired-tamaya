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
package org.apache.tamaya.core;



import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.internal.CoreConfigurationBuilder;
import org.apache.tamaya.core.internal.OSGIServiceContext;
import org.apache.tamaya.core.internal.OSGIServiceLoader;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.spisupport.PropertyFilterComparator;
import org.apache.tamaya.spisupport.PropertySourceComparator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.logging.Logger;

/**
 * A bundle activator that registers the {@link OSGIServiceLoader}.
 */
public class OSGIActivator implements BundleActivator {

    private static final Logger LOG = Logger.getLogger(OSGIActivator.class.getName());

    private OSGIServiceLoader serviceLoader;

    @Override
    public void start(BundleContext context) {
        // Register marker service
        this.serviceLoader = new OSGIServiceLoader(context);
        context.addBundleListener(serviceLoader);
        ServiceContextManager.setToStaticClassLoader(new OSGIServiceContext(serviceLoader));
        LOG.info("Registered Tamaya OSGI ServiceContext...");
        Configuration.setCurrent(
                       new CoreConfigurationBuilder()
                        .addDefaultPropertyConverters()
                        .addDefaultPropertyFilters()
                        .addDefaultPropertySources()
                        .sortPropertyFilter(PropertyFilterComparator.getInstance())
                        .sortPropertySources(PropertySourceComparator.getInstance())
                        .build()
        );
        LOG.info("Loaded default configuration from OSGI.");
    }

    @Override
    public void stop(BundleContext context) {
        if(serviceLoader!=null) {
            context.removeBundleListener(serviceLoader);
        }
    }
}
