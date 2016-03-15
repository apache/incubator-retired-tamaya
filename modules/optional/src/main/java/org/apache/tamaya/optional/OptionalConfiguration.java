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
package org.apache.tamaya.optional;


import java.util.Objects;

import org.apache.tamaya.ConfigurationProvider;

/**
 * Simplified configuration API, that can be used by code that only wants Tamaya to optionally enhance its configuration
 * mechanism, but by default uses its own configuration by default.
 */
public final class OptionalConfiguration {

    /**
     * Flag only true, if Tamaya is on the classpath.
     */
    private static final boolean TAMAYA_LOADED = initTamayaLoaded();

    /**
     * Configuration API to be loaded.
     */
    private static final String TAMAYA_CONFIGURATION = "org.apache.tamaya.Configuration";

    /**
     * Tries to load the Tamaya Configuration interface from the classpath.
     *
     * @return true, if the interface is available.
     */
    private static boolean initTamayaLoaded() {
        try {
            Class.forName(TAMAYA_CONFIGURATION);
            return true;
        } catch (final Exception e) {
            return false;
        }
    }

    /**
     * Default value provider returning Strings from system properties and the system environment.
     * In all other cases {@code null} is returned.
     */
    public static final ValueProvider DEFAULT_PROVIDER = new ValueProvider() {
        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(String key, Class<T> type) {
            if (String.class == type) {
                String value = System.getProperty(key);
                if (value == null) {
                    value = System.getenv(key);
                }
                return (T) value;
            }
            return null;
        }
    };

    /**
     * Default value provider that always returns {@code null}.
     */
    public static final ValueProvider NULLPROVIDER = new ValueProvider() {
        @Override
        public <T> T get(String key, Class<T> type) {
            return null;
        }
    };

    /**
     * Delegating value getter used to evaluate values, depending on the fallback policy.
     */
    private final ValueProvider provider;

    /**
     * Evaluation policy that determines if local configuration or Tamaya configuration values override.
     */
    private final EvaluationPolicy policy;

    /**
     * Creates a new instance.
     *
     * @param policy   the policy how a value should be evaluated depending if Tamaya is available or not.
     * @param provider the non Tamaya-based provider to be used to evaluate values.
     */
    private OptionalConfiguration(EvaluationPolicy policy, ValueProvider provider) {
        this.provider = Objects.requireNonNull(provider);
        this.policy = Objects.requireNonNull(policy);
    }

    /**
     * Returns an instance of OptionalConfiguration, which uses the given provider and policy for evaluating the values.
     *
     * @param policy   the policy how a value should be evaluated depending if Tamaya is available or not.
     * @param provider the non Tamaya-based provider to be used to evaluate values.
     * @return a default OptionalConfiguration instance, never null.
     */
    public static OptionalConfiguration of(EvaluationPolicy policy, ValueProvider provider) {
        return new OptionalConfiguration(policy, provider);
    }

    /**
     * Returns a default instance, which uses a default provider returning values from system properties and environment
     * only.
     *
     * @param policy the policy how a value should be evaluated depending if Tamaya is available or not.
     * @return a default OptionalConfiguration instance, never null.
     */
    public static OptionalConfiguration of(EvaluationPolicy policy) {
        return new OptionalConfiguration(policy, DEFAULT_PROVIDER);
    }

    /**
     * Access a String value.
     *
     * @param key the key, not null.
     * @return the value found, or null.
     */
    public String get(String key) {
        return get(key, String.class);
    }

    /**
     * Access a String value.
     *
     * @param key          the key, not null.
     * @param defaultValue the default value, returned if no such key is found in the configuration.
     * @return the value found, or null.
     */
    public String getOrDefault(String key, String defaultValue) {
        final String value = get(key, String.class);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Method that returns the corresponding value, depending on the availability of Tamaya, the
     * registered provider and evaluation policy.
     *
     * @param key  the key, not null.
     * @param type the target type, not null.
     * @param <T>  the type param.
     * @return the value, or null.
     */
    public <T> T get(String key, Class<T> type) {
        final T value = provider.get(key, type);
        final T tamayaValue = getTamaya(key, type);
        switch (policy) {
            case OTHER_OVERRIDES_TAMAYA:
                return value != null ? value : tamayaValue;
            case TAMAYA_OVERRIDES_OTHER:
                return tamayaValue != null ? tamayaValue : value;
            case THROWS_EXCEPTION:
                if (tamayaValue != value) {
                    if ((tamayaValue != null && !tamayaValue.equals(value)) ||
                            (value != null && TAMAYA_LOADED && !value.equals(tamayaValue))) {
                        throw new IllegalStateException("Incompatible configuration values: key=" + key +
                                "=" + value + "(provider)/" + tamayaValue + "(Tamaya");
                    }
                }
            default:
        }
        return value;
    }

    /**
     * Access a String value.
     *
     * @param key          the key, not null.
     * @param type         the target type, not null.
     * @param <T>          the type param.
     * @param defaultValue the default value, returned if no such key is found in the configuration.
     * @return the value found, or null.
     */
    public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
        final T value = get(key, type);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * Internal method that evaluates a value from Tamaya, when Tamaya is loaded.
     *
     * @param key  the key, not null.
     * @param type the target type, not null.
     * @param <T>  The type param
     * @return the corresponding value from Tamaya, or null.
     */
    private <T> T getTamaya(String key, Class<T> type) {
        if (TAMAYA_LOADED) {
            return ConfigurationProvider.getConfiguration().get(key, type);
        }
        return null;
    }
}
