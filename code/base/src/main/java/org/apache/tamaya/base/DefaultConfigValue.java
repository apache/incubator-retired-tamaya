/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.base;

import org.apache.tamaya.base.convert.CompoundConverter;

import javax.config.Config;
import javax.config.ConfigValue;
import javax.config.spi.Converter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements the builder styled {@link ConfigValue}.
 * @param <T> the item type
 */
public class DefaultConfigValue<T> implements ConfigValue<T> {

    private static final Logger LOG = Logger.getLogger(DefaultConfigValue.class.getName());

    private final Config config;
    private final ConfigContextSupplier contextSupplier;
    private final String key;
    private String resolvedKey;
    protected String defaultTextValue;
    protected String textValue;
    protected T defaultValue;
    private T lastValue;
    private ConfigChanged configChangeListener;
    protected Class targetClass;
    private Converter<T> converter;
    private boolean evaluateVariables;
    private TimeUnit cacheDurationTimeUnit;
    private long cacheDuration;
    private String[] lookupChain;


    /**
     * Creates a new instance.
     * @param config the underlying configuration, not null.
     * @param key
     * @param targetClass
     */
    public DefaultConfigValue(Config config, ConfigContextSupplier contextSupplier, String key, Class<T> targetClass) {
        this.config = Objects.requireNonNull(config);
        this.contextSupplier = Objects.requireNonNull(contextSupplier);
        this.key = Objects.requireNonNull(key);
        this.targetClass = Objects.requireNonNull(targetClass);
        if(lastValue!=null && !lastValue.getClass().isAssignableFrom(targetClass)){
            lastValue = null;
        }
        if(defaultValue!=null && !defaultValue.getClass().isAssignableFrom(targetClass)){
            defaultValue = null;
        }
        loadValue();
    }

    /**
     * Constructor used internally and by {@link DefaultCollectionConfigValue}, when a custom converter has been
     * applied.
     * @param configValue the base instance, not null.
     * @param converter the custom converter, not null.
     */
    DefaultConfigValue(DefaultConfigValue configValue, Converter<T> converter) {
        this(configValue);
        this.converter = Objects.requireNonNull(converter);
        this.lastValue=null;
        loadValue();
    }

    /**
     * Constructor used internally and by {@link DefaultCollectionConfigValue}, when a new target type has been
     * applied.
     * @param configValue the base instance, not null.
     * @param targetClass the target class, not null.
     */
    DefaultConfigValue(DefaultConfigValue configValue, Class<T> targetClass) {
        this(configValue);
        this.targetClass = Objects.requireNonNull(targetClass);
    }

    /**
     * Constructor to copy the instance variables from the other instance.
     * @param configValue the other instance.
     */
    private DefaultConfigValue(DefaultConfigValue configValue) {
        this.config = configValue.config;
        this.contextSupplier = configValue.contextSupplier;
        this.key = configValue.key;
        this.evaluateVariables = configValue.evaluateVariables;
        this.converter = configValue.converter;
        this.cacheDuration = configValue.cacheDuration;
        this.cacheDurationTimeUnit = configValue.cacheDurationTimeUnit;
        this.defaultTextValue = configValue.defaultTextValue;
        this.textValue = configValue.textValue;
        this.configChangeListener = configValue.configChangeListener;
        this.converter = configValue.converter;
        this.targetClass = configValue.targetClass;
        if(configValue.lastValue!=null && !targetClass.getClass().isAssignableFrom(configValue.lastValue.getClass())){
            lastValue = (T)configValue.lastValue;
        }
        if(configValue.defaultValue!=null && !targetClass.getClass().isAssignableFrom(configValue.defaultValue.getClass())){
            defaultValue = (T)configValue.defaultValue;
        }
    }

    @Override
    public <N> ConfigValue<N> as(Class<N> type) {
        return new DefaultConfigValue<>(this, type);
    }

    @Override
    public ConfigValue<List<T>> asList() {
        return new DefaultCollectionConfigValue(this, targetClass, ArrayList::new);
    }

    @Override
    public ConfigValue<Set<T>> asSet() {
        return new DefaultCollectionConfigValue(this, targetClass, HashSet::new);
    }

    /**
     * Creates a new converter based on the converters available in the current config.
     * @param targetClass the target class, not null
     * @param <N> the target type, not unll
     * @return a compund converter (may be empty), never null.
     */
    protected <N> Converter<N> getConverter(Class targetClass) {
        if(String.class.equals(targetClass)){
            return (s) -> (N)s;
        }
        return new CompoundConverter(contextSupplier.getConfigContext().getConverters(targetClass));
    }


    @Override
    public <N> ConfigValue<N> useConverter(Converter<N> converter) {
        return new DefaultConfigValue<>(this, converter);
    }

    @Override
    public ConfigValue<T> withDefault(T defaultValue) {
        DefaultConfigValue<T> newVal = new DefaultConfigValue<>(this, targetClass);
        newVal.defaultValue = Objects.requireNonNull(defaultValue);
        return newVal;
    }

    @Override
    public ConfigValue<T> withStringDefault(String defaultValue) {
        DefaultConfigValue<T> newVal = new DefaultConfigValue<>(this, targetClass);
        newVal.defaultTextValue = Objects.requireNonNull(defaultValue);
        return newVal;
    }

    @Override
    public ConfigValue<T> cacheFor(long duration, TimeUnit timeUnit) {
        DefaultConfigValue<T> newVal = new DefaultConfigValue<>(this, targetClass);
        newVal.cacheDurationTimeUnit = Objects.requireNonNull(timeUnit);
        newVal.cacheDuration = duration;
        return newVal;
    }

    @Override
    public ConfigValue<T> evaluateVariables(boolean b) {
        DefaultConfigValue<T> newVal = new DefaultConfigValue<>(this, targetClass);
        newVal.evaluateVariables = Objects.requireNonNull(evaluateVariables);
        return newVal;
    }

    @Override
    public ConfigValue<T> withLookupChain(String... lookupChain) {
        DefaultConfigValue<T> newVal = new DefaultConfigValue<>(this, targetClass);
        newVal.lookupChain = Objects.requireNonNull(lookupChain.clone());
        return newVal;
    }

    @Override
    public ConfigValue<T> onChange(ConfigChanged configChangeListener) {
        DefaultConfigValue<T> newVal = new DefaultConfigValue<>(this, targetClass);
        newVal.configChangeListener = Objects.requireNonNull(configChangeListener);
        return newVal;
    }

    @Override
    public T getValue() {
        return getOptionalValue().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Optional<T> getOptionalValue() {
        loadValue();
        String value = textValue;
        T result = null;
        if(value==null){
            if(defaultValue!=null){
                result = defaultValue;
            }
            value = defaultTextValue;
        }
        if(result==null) {
            if (value != null) {
                if (converter != null) {
                    result = converter.convert(value);
                } else {
                    result = (T) getConverter(targetClass).convert(value);
                }
            }
        }
        if (!Objects.equals(this.lastValue, result)) {
            try{
                if(configChangeListener!=null) {
                    configChangeListener.onValueChange(key, this.lastValue, result);
                }
            }catch(Exception e){
                LOG.log(Level.SEVERE, "Error calling config change listener: " + configChangeListener, e);
            }
            this.lastValue = result;
        }
        return Optional.ofNullable(result);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getResolvedKey() {
        return resolvedKey;
    }

    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    protected final void loadValue(){
        List<String> keys = evaluateKeys();
        Optional<String> currentValue = null;
        String keyFound = null;
        for(String key:keys){
            currentValue = config.getOptionalValue(key, String.class);
            if(currentValue.isPresent()){
                keyFound = key;
                break;
            }
        }
        if(currentValue!=null){
             this.textValue = currentValue.orElse(null);
        }
        this.resolvedKey = keyFound;
    }


    /*
    some.server.url.myComp.Production</li>
         *     <li>"some.server.url.myComp</li>
         *     <li>"some.server.url.Production</li>
         *     <li>"some.server.url
     */
    List<String> evaluateKeys() {
        if(lookupChain==null){
            return Collections.singletonList(key);
        }
        List<String> keys = new ArrayList<>(lookupChain.length * (lookupChain.length-1)/2);
        for(int i=0;i<lookupChain.length;i++){
            keys.add(key+'.'+lookupChain[i]);
            for(int j=0;i<lookupChain.length;j++){
                keys.add(key+'.'+lookupChain[i]+'.' + lookupChain[j]);
            }
        }
        keys.add(key);
        return keys;
    }

    @Override
    public String toString() {
        return "DefaultConfigValue{" +
                "config=" + config +
                ", key=" + key +
                ", targetClass=" + targetClass +
                ", textValue='" + textValue + '\'' +
                ", defaultTextValue='" + defaultTextValue + '\'' +
                ", lastValue='" + textValue + '\'' +
                ", defaultValue=" + defaultValue +
                ", configChangeListener=" + configChangeListener +
                ", converter=" + converter +
                '}';
    }

}
