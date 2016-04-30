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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Tamaya based implementation of an OSGI {@link ConfigurationAdmin}.
 */
public class TamayaConfigAdminImpl implements ConfigurationAdmin {
    /** the logger. */
    private static final Logger LOG = Logger.getLogger(TamayaConfigAdminImpl.class.getName());

    /** The OSGI context. */
    private final BundleContext context;
    /** THe optional OSGI parent service. */
    private ConfigurationAdmin parent;
    /** The cached configurations. */
    private Map<String,Configuration> configs = new ConcurrentHashMap<>();
    /** The configuration section mapper. */
    private OSGIConfigRootMapper configRootMapper;

    /**
     * Create a new config.
     * @param context the OSGI context
     */
    TamayaConfigAdminImpl(BundleContext context) {
        this.context = context;
        this.configRootMapper = loadConfigRootMapper();
        ServiceReference<ConfigurationAdmin> ref = context.getServiceReference(ConfigurationAdmin.class);
        this.parent = ref!=null?context.getService(ref):null;
        ServiceTracker<ManagedService, ManagedService> serviceTracker = new ServiceTracker<ManagedService,
                ManagedService>(context, ManagedService.class, null) {
            @Override
            public ManagedService addingService(ServiceReference<ManagedService> reference) {
                ManagedService service = context.getService(reference);
                Object pidObj = reference.getProperty(Constants.SERVICE_PID);
                if (pidObj instanceof String) {
                    String pid = (String) pidObj;
                    try {
                        Configuration config = getConfiguration(pid);
                        if(config==null){
                            service.updated(null);
                        } else{
                            service.updated(config.getProperties());
                        }
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, "Error configuring ManagedService: " + service, e);
                    }
                } else {
                    LOG.log(Level.SEVERE, "Unsupported pid: " + pidObj);
                }
                return service;
            }

            @Override
            public void removedService(ServiceReference<ManagedService> reference, ManagedService service) {
                context.ungetService(reference);
            }
        };
        serviceTracker.open();

        ServiceTracker<ServiceFactory, ServiceFactory> factoryTracker
                = new ServiceTracker<ServiceFactory, ServiceFactory>(context, ServiceFactory.class, null) {
            @Override
            public ServiceFactory addingService(ServiceReference<ServiceFactory> reference) {
                ServiceFactory factory = context.getService(reference);
                if(factory instanceof ManagedServiceFactory) {
                    Object pidObj = reference.getProperty(Constants.SERVICE_PID);
                    if (pidObj instanceof String) {
                        String pid = (String) pidObj;
                        try {
                            Configuration config = getConfiguration(pid);
                            if (config != null) {
                                ((ManagedServiceFactory) factory).updated(config.getFactoryPid(), config.getProperties());
                            }
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, "Error configuring ManagedServiceFactory: " + factory, e);
                        }
                    } else {
                        LOG.log(Level.SEVERE, "Unsupported pid: " + pidObj);
                    }
                }
                return factory;
            }

            @Override
            public void removedService(ServiceReference<ServiceFactory> reference, ServiceFactory service) {
                super.removedService(reference, service);
            }
        };
        factoryTracker.open();
    }

    @Override
    public Configuration createFactoryConfiguration(String factoryPid) throws IOException {
        return createFactoryConfiguration(factoryPid, null);
    }

    @Override
    public Configuration createFactoryConfiguration(String factoryPid, String location) throws IOException {
        return new TamayaConfigurationImpl(factoryPid, null, configRootMapper, this.parent);
    }

    @Override
    public Configuration getConfiguration(String pid, String location) throws IOException {
        return getConfiguration(pid);
    }

    @Override
    public Configuration getConfiguration(String pid) throws IOException {
        return new TamayaConfigurationImpl(pid, null, configRootMapper, this.parent);
    }

    @Override
    public Configuration[] listConfigurations(String filter) throws IOException, InvalidSyntaxException {
        Collection<Configuration> result;
        if (filter == null) {
            result = this.configs.values();
        } else {
            result = new ArrayList<>();
            Filter flt = context.createFilter(filter);
            for (Configuration config : this.configs.values()) {
                if (flt.match(config.getProperties())) {
                    result.add(config);
                }
            }
        }
        return result.isEmpty() ? null : result.toArray(new Configuration[configs.size()]);
    }

    /**
     * Loads the configuration toor mapper using the OSGIConfigRootMapper OSGI service resolving mechanism. If no
     * such service is available it loads the default mapper.
     * @return the mapper to be used, bever null.
     */
    private OSGIConfigRootMapper loadConfigRootMapper() {
        OSGIConfigRootMapper mapper = null;
        ServiceReference<OSGIConfigRootMapper> ref = context.getServiceReference(OSGIConfigRootMapper.class);
        if(ref!=null){
            mapper = context.getService(ref);
        }
        if(mapper==null){
            mapper = new OSGIConfigRootMapper() {
                @Override
                public String getTamayaConfigRoot(String pid, String factoryPid) {
                    if(pid!=null) {
                        return "[bundle:" + pid +']';
                    } else{
                        return "[bundle:" + factoryPid +']';
                    }
                }
                @Override
                public String toString(){
                    return "Default OSGIConfigRootMapper(pid -> [bundle:pid], factoryPid -> [bundle:factoryPid]";
                }
            };
        }
        return mapper;
    }

}
