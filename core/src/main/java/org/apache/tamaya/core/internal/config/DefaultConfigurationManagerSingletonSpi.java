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

import org.apache.tamaya.*;
import org.apache.tamaya.core.internal.el.DefaultExpressionEvaluator;
import org.apache.tamaya.core.internal.inject.ConfigurationInjector;
import org.apache.tamaya.core.properties.PropertySourceBuilder;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;
import org.apache.tamaya.core.spi.ExpressionEvaluator;

import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ConfigurationManagerSingletonSpi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Default SPI that implements the behaviour of {@link org.apache.tamaya.spi.ConfigurationManagerSingletonSpi}.
 */
public class DefaultConfigurationManagerSingletonSpi implements ConfigurationManagerSingletonSpi {

    private static final String DEFAULT_CONFIG_NAME = "default";

    private Map<String, ConfigurationProviderSpi> configProviders = new ConcurrentHashMap<>();

    private Map<Consumer<ConfigChangeSet>, List<Predicate<PropertySource>>> listenerMap = new ConcurrentHashMap<>();

    private ExpressionEvaluator expressionEvaluator = loadEvaluator();

    private ExpressionEvaluator loadEvaluator() {
        ExpressionEvaluator eval = ServiceContext.getInstance().getService(ExpressionEvaluator.class).orElse(null);
        if (eval == null) {
            eval = new DefaultExpressionEvaluator();
        }
        return eval;
    }

    public DefaultConfigurationManagerSingletonSpi() {
        for (ConfigurationProviderSpi spi : ServiceContext.getInstance().getServices(ConfigurationProviderSpi.class, Collections.emptyList())) {
            configProviders.put(spi.getConfigName(), spi);
        }
    }

    @Override
    public <T> T getConfiguration(String name, Class<T> type) {
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
        return (T) Proxy.newProxyInstance(cl, new Class[]{type}, new ConfigTemplateInvocationHandler(type, config));
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
    public void addChangeListener(Predicate<PropertySource> predicate, Consumer<ConfigChangeSet> l) {
        List<Predicate<PropertySource>> predicates = listenerMap.computeIfAbsent(l,
                cs -> Collections.synchronizedList(new ArrayList<>()));
        if (predicate == null) {
            predicates.add(p -> true); // select all events!
        } else {
            predicates.add(predicate);
        }
    }

    @Override
    public void removeChangeListener(Predicate<PropertySource> predicate, Consumer<ConfigChangeSet> l) {
        List<Predicate<PropertySource>> predicates = listenerMap.get(l);
        if (predicate == null) {
            listenerMap.remove(l); // select all events!
        } else {
            predicates.add(predicate);
        }
    }

    @Override
    public void publishChange(ConfigChangeSet configChangeSet) {
        listenerMap.entrySet().forEach(
                (en) -> {
                    if (en.getValue().stream()
                            .filter(v -> v.test(configChangeSet.getPropertySource())).findAny().isPresent()) {
                        en.getKey().accept(configChangeSet);
                    }
                }
        );
    }

    @Override
    public boolean isConfigurationDefined(String name) {
        ConfigurationProviderSpi spi = this.configProviders.get(name);
        return spi != null;
    }

    /**
     * Implementation of a default config provider used as fallback, if no {@link org.apache.tamaya.core.spi.ConfigurationProviderSpi}
     * instance is registered for providing the {@code default} {@link org.apache.tamaya.Configuration}.
     */
    private static final class FallbackSimpleConfigProvider implements ConfigurationProviderSpi {
        /**
         * The loaded configuration instance.
         */
        private volatile Configuration configuration;

        @Override
        public String getConfigName() {
            return DEFAULT_CONFIG_NAME;
        }

        @Override
        public Configuration getConfiguration() {
            Configuration cfg = configuration;
            if (cfg == null) {
                reload();
                cfg = configuration;
            }
            return cfg;
        }


        @Override
        public void reload() {
            this.configuration =
                    PropertySourceBuilder.of(DEFAULT_CONFIG_NAME)
                            .addProviders(PropertySourceBuilder.of("CL default")
                                    .withAggregationPolicy(AggregationPolicy.LOG_ERROR)
                                    .addPaths("META-INF/cfg/default/**/*.xml", "META-INF/cfg/default/**/*.properties", "META-INF/cfg/default/**/*.ini")
                                    .build())
                            .addProviders(PropertySourceBuilder.of("CL default")
                                    .withAggregationPolicy(AggregationPolicy.LOG_ERROR)
                                    .addPaths("META-INF/cfg/config/**/*.xml", "META-INF/cfg/config/**/*.properties", "META-INF/cfg/config/**/*.ini")
                                    .build())
                            .addSystemProperties()
                            .addEnvironmentProperties()
                            .build().toConfiguration();
        }
    }

}
