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
package org.apache.tamaya.core.internal.config;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.internal.el.DefaultExpressionEvaluator;
import org.apache.tamaya.core.internal.inject.ConfigTemplateInvocationHandler;
import org.apache.tamaya.core.internal.inject.ConfigurationInjector;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;
import org.apache.tamaya.core.spi.ExpressionEvaluator;
import org.apache.tamaya.spi.ConfigurationSpi;
import org.apache.tamaya.spi.ServiceContext;


/**
 * Default SPI that implements the behaviour of {@link org.apache.tamaya.spi.ConfigurationSpi}.
 */
@SuppressWarnings("unchecked")
public class DefaultConfigurationSpi implements ConfigurationSpi {

    private static final String DEFAULT_CONFIG_NAME = "default";

    private Map<String, ConfigurationProviderSpi> configProviders = new ConcurrentHashMap<>();

    private ExpressionEvaluator expressionEvaluator = loadEvaluator();

    private ExpressionEvaluator loadEvaluator() {
        ExpressionEvaluator eval = ServiceContext.getInstance().getService(ExpressionEvaluator.class).orElse(null);
        if (eval == null) {
            eval = new DefaultExpressionEvaluator();
        }
        return eval;
    }

    public DefaultConfigurationSpi() {
        if(configProviders.isEmpty()) {
            for (ConfigurationProviderSpi spi : ServiceContext.getInstance().getServices(ConfigurationProviderSpi.class, Collections.emptyList())) {
                configProviders.put(spi.getConfigName(), spi);
            }
        }
    }

    @Override
    public <T> T createTemplate(Class<T> type, Configuration... configurations) {
        ClassLoader cl = Optional.ofNullable(Thread.currentThread()
                .getContextClassLoader()).orElse(getClass().getClassLoader());
        return (T) Proxy.newProxyInstance(cl, new Class[]{type}, new ConfigTemplateInvocationHandler(type, configurations));
    }

    /**
     *
     * @param instance the instance with configuration annotations, not null.
     * @param configurations the configurations to be used for evaluating the values for injection into {@code instance}.
     *                If no items are passed, the default configuration is used.
     */
    @Override
    public void configure(Object instance, Configuration... configurations) {
        ConfigurationInjector.configure(instance, configurations);
    }


    @Override
    public String evaluateValue(String expression, Configuration... configurations) {
        return expressionEvaluator.evaluate(expression, configurations);
    }

    @Override
    public boolean isConfigurationAvailable(String name) {
        ConfigurationProviderSpi spi = this.configProviders.get(name);
        return spi != null;
    }

    @Override
    public Configuration getConfiguration(String name) {
        ConfigurationProviderSpi provider = configProviders.get(name);
        if (provider == null) {
            if (DEFAULT_CONFIG_NAME.equals(name)) {
                provider = new FallbackSimpleConfigProvider();
                configProviders.put(DEFAULT_CONFIG_NAME, provider);
            } else {
                throw new ConfigException("No such config: " + name);
            }
        }
        Configuration config = provider.getConfiguration();
        if (config == null) {
            throw new ConfigException("No such config: " + name);
        }
        return config;
    }



}
