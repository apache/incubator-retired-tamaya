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
package org.apache.tamaya.clsupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Anatole on 08.09.2015.
 */
class ServiceContainer {

    private static final Logger LOG = Logger.getLogger(ServiceContainer.class.getName());

    private static final String PREFIX = "META-INF/services/";

    // The access control context taken when the ServiceLoader is created
    private final AccessControlContext acc;

    private WeakReference<ClassLoader> classLoaderRef;

    /**
     * List current services loaded using this classloader, per class.
     */
    private final Map<Class<?>, Map<String, Object>> servicesLoaded = new ConcurrentHashMap<>();
    /**
     * The cached singletons for the given classloader.
     */
    private final Map<Class, Object> singletons = new ConcurrentHashMap<>();

    /**
     * List current services loaded using this classloader, per class.
     */
    private final Map<Class, List<URL>> configsLoaded = new ConcurrentHashMap<>();

    ServiceContainer(ClassLoader classLoader) {
        acc = (System.getSecurityManager() != null) ? AccessController.getContext() : null;
        this.classLoaderRef = new WeakReference<>(classLoader);
    }

    public ClassLoader getClassLoader() {
        ClassLoader cl = classLoaderRef.get();
        if (cl == null) {
            throw new IllegalStateException("Classloader reference removed, not active anynire.");
        }
        return cl;
    }


    public <T> void loadServices(Class<?> type,
                                 Collection<ServiceContainer> preceedingContainers) {
        Map<String, Object> services = (Map<String, Object>) this.servicesLoaded.get(type);
        if (services == null) {
            services = new LinkedHashMap<>();
            this.servicesLoaded.put(type, services);
        }
        loop:
        for (URL config : getConfigs(type)) {
            for (ServiceContainer cont : preceedingContainers) {
                if (cont.getConfigs(type).contains(config)) {
                    LOG.finer("Ignoring already loaded config: " + config);
                    continue loop;
                }
            }
            Collection<String> serviceNames = parse(type, config);
            for (String s : serviceNames) {
                for (ServiceContainer cont : preceedingContainers) {
                    if (cont.containsService(type, s)) {
                        LOG.finest("Ignoring duplicate service: " + s);
                        continue;
                    }
                }
                LOG.info("Loading component: " + s);
                services.put(s, create(type, s));
            }
        }
    }

    private Collection<URL> getConfigs(Class<?> type) {
        List<URL> result = this.configsLoaded.get(type);
        if (result == null) {
            ClassLoader cl = this.classLoaderRef.get();
            if (cl == null) {
                throw new IllegalStateException("CLassLoader dereferenced already.");
            }
            result = new ArrayList<>();
            try {
                Enumeration<URL> resources = cl.getResources(PREFIX + type.getName());
                while (resources.hasMoreElements()) {
                    result.add(resources.nextElement());
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Failed to read service config for " + type.getName() + " from " + cl, e);
            }
            this.configsLoaded.put(type, result);
            LOG.log(Level.FINE, "Found service config for " + type.getName() + ": " + result);
        }
        return result;
    }

    private boolean containsService(Class<?> type, String serviceClassName) {
        Map<String, Object> services = servicesLoaded.get(type);
        if (services == null) {
            return false;
        }
        return services.containsKey(serviceClassName);
    }


    private <S> S create(Class<S> serviceType, String className) {
        Class<?> c = null;
        ClassLoader classLoader = getClassLoader();
        try {
            c = Class.forName(className, false, classLoader);
        } catch (ClassNotFoundException x) {
            fail(serviceType,
                    "Provider " + className + " not found");
        }
        if (!serviceType.isAssignableFrom(c)) {
            fail(serviceType,
                    "Provider " + className + " not a subtype");
        }
        try {
            S p = serviceType.cast(c.newInstance());
            return p;
        } catch (Throwable x) {
            fail(serviceType,
                    "Provider " + className + " could not be instantiated",
                    x);
        }
        throw new Error();          // This cannot happen
    }

    public <T> Collection<T> getServices(Class<T> serviceType) {
        Map<String, Object> services = this.servicesLoaded.get(serviceType);
        if (services != null) {
            return (Collection<T>) services.values();
        }
        return Collections.emptySet();
    }

    public boolean isTypeLoaded(Class<?> serviceType) {
        return this.servicesLoaded.containsKey(serviceType);
    }

    public Collection<URL> load(Class<?> serviceType) {
        return load(serviceType, Collection.class.cast(Collections.emptySet()));
    }

    public Collection<URL> load(Class<?> serviceType, Collection<URL> configsLoaded) {
        List<URL> result = new ArrayList<>();
        try {
            Enumeration<URL> resources = getClassLoader().getResources(PREFIX + serviceType.getName());
            while (resources.hasMoreElements()) {
                URL res = resources.nextElement();
                if (!configsLoaded.contains(res)) {
                    result.add(res);
                }
            }
            return result;
        } catch (Exception e) {
            fail(serviceType, "Failed to load service config: " + PREFIX + serviceType.getName(), e);
        }
        return result;
    }


    // Parse a single line from the given configuration file, adding the name
    // on the line to the names list.
    //
    private int parseLine(Class<?> serviceType, URL u, BufferedReader r, int lc,
                          List<String> names)
            throws IOException, ServiceConfigurationError {
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                fail(serviceType, u, lc, "Illegal configuration-file syntax");
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(serviceType, u, lc, "Illegal provider-class name: " + ln);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                    fail(serviceType, u, lc, "Illegal provider-class name: " + ln);
                }
            }
            Map<String, Object> services = this.servicesLoaded.get(serviceType);
            if (services == null || !services.containsKey(ln) && !names.contains(ln)) {
                names.add(ln);
            }
        }
        return lc + 1;
    }


    // Parse the content of the given URL as a provider-configuration file.
    //
    // @param  service
    //         The service type for which providers are being sought;
    //         used to construct error detail strings
    //
    // @param  u
    //         The URL naming the configuration file to be parsed
    //
    // @return A (possibly empty) iterator that will yield the provider-class
    //         names in the given configuration file that are not yet members
    //         of the returned set
    //
    // @throws ServiceConfigurationError
    //         If an I/O error occurs while reading from the given URL, or
    //         if a configuration-file format error is detected
    //
    private Collection<String> parse(Class<?> service, URL u)
            throws ServiceConfigurationError {
        InputStream in = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<>();
        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(service, u, r, lc, names)) >= 0) {
                // go ahead
            }
        } catch (IOException x) {
            fail(service, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException y) {
                fail(service, "Error closing configuration file", y);
            }
        }
        return names;
    }


    private static void fail(Class<?> service, String msg, Throwable cause)
            throws ServiceConfigurationError {
        LOG.log(Level.SEVERE, "Failed to load: " + service.getName() + ": " + msg, cause);
    }

    private static void fail(Class<?> service, String msg)
            throws ServiceConfigurationError {
        LOG.log(Level.SEVERE, "Failed to load: " + service.getName() + ": " + msg);
    }

    private static void fail(Class<?> service, URL u, int line, String msg)
            throws ServiceConfigurationError {
        fail(service, u + ":" + line + ": " + msg);
    }

    public <T> T getSingleton(Class<T> serviceType) {
        return (T) this.singletons.get(serviceType);
    }

    <T> void setSingleton(Class<T> type, T instance) {
        LOG.info("Caching singleton for " + type.getName() + " and classloader: " +
                getClassLoader().toString() + ": " + instance);
        this.singletons.put(type, instance);
    }
}
