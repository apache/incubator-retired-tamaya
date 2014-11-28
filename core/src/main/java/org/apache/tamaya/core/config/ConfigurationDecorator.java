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
package org.apache.tamaya.core.config;

import org.apache.tamaya.*;
import org.apache.tamaya.core.spi.AdapterProviderSpi;
import org.apache.tamaya.spi.Bootstrap;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Decorator that adds {@link org.apache.tamaya.Configuration} capabilities to
 * a given {@link org.apache.tamaya.PropertyProvider} instance.
 */
public class ConfigurationDecorator extends AbstractConfiguration{

    private static final long serialVersionUID = 503764580971917964L;

    /** The underlying property provider. */
    private PropertyProvider provider;
    /** Lock for applying changes. */
    private final Object LOCK = new Object();

    /**
     * Constructor.
     * @param provider  The underlying property provider, not null.
     */
    protected ConfigurationDecorator(PropertyProvider provider){
        super(provider.getMetaInfo());
        this.provider = Objects.requireNonNull(provider);
    }


    @Override
    public <T> Optional<T> get(String key, Class<T> type){
        AdapterProviderSpi as = Bootstrap.getService(AdapterProviderSpi.class);
        PropertyAdapter<T> adapter = as.getAdapter(type);
        if(adapter == null){
            throw new ConfigException(
                    "Can not adapt config property '" + key + "' to " + type.getName() + ": no such " +
                            "adapter.");
        }
        return getAdapted(key, adapter);
    }

    @Override
    public Optional<String> get(String key) {
        return provider.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return provider.containsKey(key);
    }

    @Override
    public Map<String, String> toMap() {
        return provider.toMap();
    }

    @Override
    public MetaInfo getMetaInfo() {
        return provider.getMetaInfo();
    }

    @Override
    protected ConfigChangeSet reload() {
        return provider.load();
    }

    @Override
    public void apply(ConfigChangeSet change) {
        Configuration oldState;
        Configuration newState;
        synchronized(LOCK) {
            oldState = FreezedConfiguration.of(this);
            provider.apply(change);
            newState = FreezedConfiguration.of(this);
            if(oldState.hasSameProperties(newState)){
                return;
            }
        }
        publishPropertyChangeEvents(ConfigChangeSetBuilder.of(oldState).addChanges(newState).build().getEvents());
    }

    @Override
    public String toString(){
        return "ConfigurationDecorator(provider="+this.provider+")";
    }

}
