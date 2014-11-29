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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.core.internal.inject.ConfigurationInjector;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;
import org.apache.tamaya.core.spi.ExpressionEvaluator;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.spi.ConfigurationManagerSingletonSpi;

import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public class DefaultConfigurationManagerSingletonSpi implements ConfigurationManagerSingletonSpi {

    private Map<String, ConfigurationProviderSpi> configProviders = new ConcurrentHashMap<>();

    private ExpressionEvaluator expressionEvaluator = loadEvaluator();

    private ExpressionEvaluator loadEvaluator() {
        ExpressionEvaluator eval = Bootstrap.getService(ExpressionEvaluator.class, null);
        if (eval == null) {
            eval = new DefaultExpressionEvaluator();
        }
        return eval;
    }

    public DefaultConfigurationManagerSingletonSpi() {
        for (ConfigurationProviderSpi spi : Bootstrap.getServices(ConfigurationProviderSpi.class)) {
            configProviders.put(spi.getConfigName(), spi);
        }
    }

    @Override
    public <T> T getConfiguration(String name, Class<T> type) {
        ConfigurationProviderSpi provider = configProviders.get(name);
        if (provider == null) {
            throw new ConfigException("No such config: " + name);
        }
        Configuration config = provider.getConfiguration();
        if (config == null) {
            throw new ConfigException("No such config: " + name);
        }
        if (Configuration.class.equals(type)) {
            return (T) config;
        }
        return createAdapterProxy(config, type);
    }

    /**
     * Creates a proxy implementing the given target interface.
     *
     * @param config the configuration to be used for providing values.
     * @param type   the target interface.
     * @param <T>    the target interface type.
     * @return the corresponding implementing proxy, never null.
     */
    private <T> T createAdapterProxy(Configuration config, Class<T> type) {
        ClassLoader cl = Optional.ofNullable(Thread.currentThread()
                .getContextClassLoader()).orElse(getClass().getClassLoader());
        return (T)Proxy.newProxyInstance(cl,new Class[]{type}, new ConfigurationInvocationHandler(type, config));
    }

    @Override
    public void configure(Object instance) {
        ConfigurationInjector.configure(instance);
    }

    private String getConfigId(Annotation... qualifiers) {
        if (qualifiers == null || qualifiers.length == 0) {
            return "";
        }
        StringBuilder b = new StringBuilder();
        for (Annotation annot : qualifiers) {
            b.append('[');
            b.append(annot.annotationType().getName());
            b.append(':');
            b.append(annot.toString());
            b.append(']');
        }
        return b.toString();
    }

    @Override
    public String evaluateValue(Configuration config, String expression) {
        return expressionEvaluator.evaluate(expression);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        Configuration.of().addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        Configuration.of().removePropertyChangeListener(listener);
    }

    @Override
    public boolean isConfigurationDefined(String name) {
        ConfigurationProviderSpi spi = this.configProviders.get(name);
        return spi != null;
    }



}
