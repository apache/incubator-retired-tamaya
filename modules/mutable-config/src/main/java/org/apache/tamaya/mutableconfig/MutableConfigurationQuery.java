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
package org.apache.tamaya.mutableconfig;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.mutableconfig.spi.AbstractMutableConfiguration;
import org.apache.tamaya.mutableconfig.spi.MutableConfigurationBackendSpi;
import org.apache.tamaya.mutableconfig.spi.MutableConfigurationBackendProviderSpi;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.ServiceContextManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Accessor for creating {@link MutableConfiguration} instances to change configuration and commit changes.
 */
public final class MutableConfigurationQuery implements ConfigQuery<MutableConfiguration> {

    /**
     * URIs used by this query instance to identify the backends to use for write operations.
     */
    private final MutableConfigurationBackendSpi target;

    private ValueVisibilityPolicy valueVisibilityPolicy;

    /** Singleton constructor. */
    private MutableConfigurationQuery(MutableConfigurationBackendSpi target, ValueVisibilityPolicy valueVisibilityPolicy){
        this.target = Objects.requireNonNull(target);
        this.valueVisibilityPolicy = valueVisibilityPolicy;
    }

    @Override
    public MutableConfiguration query(Configuration config) {
        return new DefaultMutableConfiguration(target, valueVisibilityPolicy, config);
    }

    /**
     * Creates a new {@link MutableConfigurationQuery} for the given configuration target.
     *
     * @param configurationTarget the configuration targets (String to create URIs) to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfigurationQuery of(String configurationTarget){
        return of(configurationTarget, ValueVisibilityPolicy.CONFIG);
    }

    /**
     * Creates a new {@link MutableConfigurationQuery} for the given configuration target and visibility policy.
     *
     * @param configurationTarget the configuration targets (String to create URIs) to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @param valueVisibilityPolicy the policy that defines how values edited, added or removed are reflected in the read
     *                         accesses of the {@link MutableConfiguration} created.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfigurationQuery of(String configurationTarget, ValueVisibilityPolicy valueVisibilityPolicy){
        try {
            URI uri = new URI(configurationTarget);
            return of(uri, valueVisibilityPolicy);
        } catch(URISyntaxException e){
            throw new ConfigException("Invalid URI " + configurationTarget);
        }
    }

    /**
     * Creates a new {@link MutableConfigurationQuery} for the given configuration target.
     *
     * @param configurationTarget the configuration targets to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfigurationQuery of(URI configurationTarget){
        return of(configurationTarget, ValueVisibilityPolicy.CONFIG);
    }
    /**
     * Creates a new {@link MutableConfigurationQuery} for the given configuration target and visibility policy.
     *
     * @param configurationTarget the configuration targets to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @param valueVisibilityPolicy the policy that defines how values edited, added or removed are reflected in the read
     *                         accesses of the {@link MutableConfiguration} created.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfigurationQuery of(URI configurationTarget, ValueVisibilityPolicy valueVisibilityPolicy){
        MutableConfigurationBackendSpi target = null;
        for(MutableConfigurationBackendProviderSpi spi:ServiceContextManager.getServiceContext()
                .getServices(MutableConfigurationBackendProviderSpi.class)){
            MutableConfigurationBackendSpi req = spi.getBackend(Objects.requireNonNull(configurationTarget));
            if (req != null) {
                target = req;
                break;
            }
        }
        if(target==null) {
            throw new ConfigException("Not an editable configuration target: " +
                    configurationTarget);
        }
        return new MutableConfigurationQuery(target, Objects.requireNonNull(valueVisibilityPolicy));
    }



    /**
     * Creates a new {@link MutableConfiguration} for the given configuration target.
     *
     * @param configurationTarget the configuration targets (String to create URIs) to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfiguration createMutableConfiguration(String configurationTarget){
        return createMutableConfiguration(configurationTarget, ValueVisibilityPolicy.CONFIG);
    }

    /**
     * Creates a new {@link MutableConfiguration} for the given configuration target and visibility policy.
     *
     * @param configurationTarget the configuration targets (String to create URIs) to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @param valueVisibilityPolicy the policy that defines how values edited, added or removed are reflected in the read
     *                         accesses of the {@link MutableConfiguration} created.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfiguration createMutableConfiguration(String configurationTarget,
                                                                  ValueVisibilityPolicy valueVisibilityPolicy){
        try {
            URI uri = new URI(configurationTarget);
            return createMutableConfiguration(uri, valueVisibilityPolicy);
        } catch(URISyntaxException e){
            throw new ConfigException("Invalid URI " + configurationTarget);
        }
    }

    /**
     * Creates a new {@link MutableConfiguration} for the given configuration target.
     *
     * @param configurationTarget the configuration targets to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfiguration createMutableConfiguration(URI configurationTarget){
        return createMutableConfiguration(configurationTarget, ValueVisibilityPolicy.CONFIG);
    }
    /**
     * Creates a new {@link MutableConfiguration} for the given configuration target and visibility policy.
     *
     * @param configurationTarget the configuration targets to use to write the changes/config. By passing multiple
     *                             URIs you can write back changes into multiple configuration backends, e.g.
     *                             one for redistributing changes using multicast mechanism, a local property file
     *                             for failover as well as the shared etcd server.
     * @param valueVisibilityPolicy the policy that defines how values edited, added or removed are reflected in the read
     *                         accesses of the {@link MutableConfiguration} created.
     * @return a new ChangeRequest
     * @throws org.apache.tamaya.ConfigException if the given configurationSource cannot be edited.
     */
    public static MutableConfiguration createMutableConfiguration(URI configurationTarget,
                                                                  ValueVisibilityPolicy valueVisibilityPolicy){
        return Configuration.EMPTY.query(of(configurationTarget, valueVisibilityPolicy));
    }


    /**
     * Compound request that contains internally multiple change requests. Changes are committed to all members.
     */
    private static final class DefaultMutableConfiguration extends AbstractMutableConfiguration
            implements MutableConfiguration {

        private final MutableConfigurationBackendSpi target;
        private final Configuration config;
        private ValueVisibilityPolicy valueVisibilityPolicy;

        DefaultMutableConfiguration(MutableConfigurationBackendSpi target, ValueVisibilityPolicy valueVisibilityPolicy, Configuration config){
            this.target = Objects.requireNonNull(target);
            this.config = Objects.requireNonNull(config);
            this.valueVisibilityPolicy = valueVisibilityPolicy;
        }

        @Override
        public URI getBackendURI() {
            return target.getBackendURI();
        }

        @Override
        public boolean isWritable(String keyExpression) {
            return target.isWritable(keyExpression);
        }

        @Override
        public boolean isRemovable(String keyExpression) {
            return target.isRemovable(keyExpression);
        }

        @Override
        public boolean isExisting(String keyExpression) {
            return target.isExisting(keyExpression);
        }

        @Override
        public MutableConfiguration put(String key, String value) {
            if(target.isWritable(key)){
                target.put(key, value);
            }
            return this;
        }

        @Override
        public MutableConfiguration putAll(Map<String, String> properties) {
            for(Map.Entry<String,String> en:properties.entrySet()) {
                if (target.isWritable(en.getKey())) {
                    target.put(en.getKey(), en.getValue());
                }
            }
            return super.putAll(properties);
        }

        @Override
        public MutableConfiguration remove(String... keys) {
            for(String key:keys){
                if (target.isRemovable(key)) {
                    target.remove(key);
                }
            }
            return super.remove(keys);
        }

        @Override
        public MutableConfiguration remove(Collection<String> keys) {
            for(String key:keys){
                if (target.isRemovable(key)) {
                    target.remove(key);
                }
            }
            return super.remove(keys);
        }

        @Override
        protected void commitInternal() {
            target.commit();
        }

        @Override
        public String get(String key) {
            String addedOrUpdated = this.addedProperties.get(key);
            switch(valueVisibilityPolicy){
                case CHANGES:
                    boolean removed = this.removedProperties.contains(key);
                    if(removed){
                        return null;
                    }
                    return addedOrUpdated!=null?addedOrUpdated:getInternal(key);
                case CONFIG:
                default:
                    String val = getInternal(key);
                    return val == null?addedOrUpdated:val;
            }
        }

        private String getInternal(String key) {
           Map<String,String> props = this.config.getProperties();
            if(props.isEmpty()){
                PropertyValue val = this.target.getBackendPropertySource().get(key);
                if(val!=null){
                    return val.getValue();
                }
            }
            return this.config.get(key);
        }

        @Override
        public String getOrDefault(String key, String defaultValue) {
            String val = get(key);
            return val == null? defaultValue: val;
        }

        @Override
        public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
            return (T)getOrDefault(key, TypeLiteral.of(type), defaultValue);
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            return getOrDefault(key, type, (T)null);
        }

        @Override
        public <T> T get(String key, TypeLiteral<T> type) {
            return getOrDefault(key, type, (T)null);
        }

        @Override
        public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
            String val = get(key);
            if(val==null) {
                return defaultValue;
            }
            for(PropertyConverter conv: ConfigurationProvider.getConfigurationContext().getPropertyConverters(type)){
                Object o = conv.convert(val, new ConversionContext.Builder(key, type).setConfiguration(config).build());
                if(o!=null){
                    return (T) o;
                }
            }
            return defaultValue;
        }

        @Override
        public Map<String, String> getProperties() {
            Map<String, String> configProps = new HashMap<>();
            if(config.getProperties().isEmpty()) {
                configProps.putAll(target.getBackendPropertySource().getProperties());
            }else{
                configProps.putAll(config.getProperties());
            }
            switch(valueVisibilityPolicy){
                case CHANGES:
                    for(String key:removedProperties){
                        configProps.remove(key);
                    }
                    configProps.putAll(addedProperties);
                    return configProps;
                case CONFIG:
                default:
                    Map<String, String> props = new HashMap<>(addedProperties);
                    for(String key:removedProperties){
                        props.remove(key);
                    }
                    props.putAll(configProps);
                    return props;
            }
        }

        @Override
        public Configuration with(ConfigOperator operator) {
            return operator.operate(this);
        }

        @Override
        public <T> T query(ConfigQuery<T> query) {
            if(query instanceof MutableConfigurationQuery){
                throw new ConfigException("Cannot query a mutable configuration, already is one!");
            }
            return query.query(this);
        }

        @Override
        public String toString() {
            return "DefaultMutableConfiguration{" +
                    "config=" + config +
                    ", target=" + target +
                    '}';
        }
    }

}
