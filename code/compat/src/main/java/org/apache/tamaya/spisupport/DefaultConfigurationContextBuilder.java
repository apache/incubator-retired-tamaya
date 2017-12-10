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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.TypeLiteral;
import org.apache.tamaya.spi.*;
import org.apache.tamaya.spisupport.propertysource.CLIPropertySource;
import org.apache.tamaya.spisupport.propertysource.EnvironmentPropertySource;
import org.apache.tamaya.spisupport.propertysource.JavaConfigurationPropertySource;
import org.apache.tamaya.spisupport.propertysource.SystemPropertySource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

/**
 * Default implementation of {@link ConfigurationContextBuilder}.
 */
public class DefaultConfigurationContextBuilder implements ConfigurationContextBuilder {

    private static final Logger LOG = Logger.getLogger(DefaultConfigurationContextBuilder.class.getName());

    protected List<PropertyFilter> propertyFilters = new ArrayList<>();
    protected List<PropertySource> propertySources = new ArrayList<>();
    protected PropertyValueCombinationPolicy combinationPolicy = PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;
    protected Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> propertyConverters = new HashMap<>();

    /**
     * Flag if the config has already been built.
     * Configuration can be built only once
     */
    private boolean built;



    /**
     * Creates a new builder instance.
     */
    public DefaultConfigurationContextBuilder() {
    }

    /**
     * Creates a new builder instance initializing it with the given context.
     * @param context the context to be used, not null.
     */
    public DefaultConfigurationContextBuilder(ConfigurationContext context) {
        this.propertyConverters.putAll(context.getPropertyConverters());
        this.propertyFilters.addAll(context.getPropertyFilters());
        for(PropertySource ps:context.getPropertySources()) {
            addPropertySources(ps);
        }
        this.combinationPolicy = context.getPropertyValueCombinationPolicy();
    }

    /**
     * Allows to reset configuration context during unit tests.
     */
    public final ConfigurationContextBuilder resetWithConfigurationContext(ConfigurationContext configurationContext) {
        checkBuilderState();
        //noinspection deprecation
        this.propertyFilters.clear();
        this.propertyFilters.addAll(configurationContext.getPropertyFilters());
        this.propertySources.clear();
        for(PropertySource ps:configurationContext.getPropertySources()) {
            addPropertySources(ps);
        }
        this.propertyConverters.clear();
        this.propertyConverters.putAll(configurationContext.getPropertyConverters());
        this.combinationPolicy = configurationContext.getPropertyValueCombinationPolicy();
        return this;
    }


    @Override
    public ConfigurationContextBuilder setContext(ConfigurationContext context) {
        checkBuilderState();
        this.propertyConverters.putAll(context.getPropertyConverters());
        for(PropertySource ps:context.getPropertySources()){
            this.propertySources.add(ps);
        }
        this.propertyFilters.addAll(context.getPropertyFilters());
        this.combinationPolicy = context.getPropertyValueCombinationPolicy();
        return this;
    }

    @Override
    public final ConfigurationContextBuilder addPropertySources(PropertySource... sources){
        return addPropertySources(Arrays.asList(sources));
    }

    @Override
    public ConfigurationContextBuilder addPropertySources(Collection<PropertySource> sources){
        checkBuilderState();
        for(PropertySource source:sources) {
            if (!this.propertySources.contains(source)) {
                this.propertySources.add(source);
            }
        }
        return this;
    }

    @Override
    public ConfigurationContextBuilder addDefaultPropertySources() {
        checkBuilderState();
        List<PropertySource> propertySources = new ArrayList<>();
        addCorePropertyResources(propertySources);
        for(PropertySource ps: ServiceContextManager.getServiceContext().getServices(PropertySource.class)) {
            if(!propertySources.contains(ps)){
                propertySources.add(ps);
            }
        }
        for(PropertySourceProvider provider:
                ServiceContextManager.getServiceContext().getServices(PropertySourceProvider.class)){
                propertySources.addAll(provider.getPropertySources());
        }
        Collections.sort(propertySources, PropertySourceComparator.getInstance());
        return addPropertySources(propertySources);
    }

    protected void addCorePropertyResources(List<PropertySource> propertySources) {
        for(PropertySource ps: new PropertySource[]{
                new EnvironmentPropertySource(),
                new JavaConfigurationPropertySource(),
                new CLIPropertySource(),
                new SystemPropertySource()
        }){
            if(!propertySources.contains(ps)){
                propertySources.add(ps);
            }
        }
    }

    @Override
    public ConfigurationContextBuilder addDefaultPropertyFilters() {
        checkBuilderState();
        for(PropertyFilter pf:ServiceContextManager.getServiceContext().getServices(PropertyFilter.class)){
            addPropertyFilters(pf);
        }
        return this;
    }

    @Override
    public DefaultConfigurationContextBuilder addDefaultPropertyConverters() {
        checkBuilderState();
        addCorePropertyConverters();
        for(Map.Entry<TypeLiteral, Collection<PropertyConverter>> en:getDefaultPropertyConverters().entrySet()){
            for(PropertyConverter pc: en.getValue()) {
                addPropertyConverters(en.getKey(), pc);
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
	protected void addCorePropertyConverters() {
        // should be overridden by subclasses.
    }

    @Override
    public final ConfigurationContextBuilder removePropertySources(PropertySource... propertySources) {
        return removePropertySources(Arrays.asList(propertySources));
    }

    @Override
    public ConfigurationContextBuilder removePropertySources(Collection<PropertySource> propertySources) {
        checkBuilderState();
        this.propertySources.removeAll(propertySources);
        return this;
    }

    protected PropertySource getPropertySource(String name) {
        for(PropertySource ps:propertySources){
            if(ps.getName().equals(name)){
                return ps;
            }
        }
        throw new IllegalArgumentException("No such PropertySource: "+name);
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return Collections.unmodifiableList(this.propertySources);
    }

    @Override
    public ConfigurationContextBuilder increasePriority(PropertySource propertySource) {
        checkBuilderState();
        int index = propertySources.indexOf(propertySource);
        if(index<0){
            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
        }
        if(index<(propertySources.size()-1)){
            propertySources.remove(propertySource);
            propertySources.add(index+1, propertySource);
        }
        return this;
    }

    @Override
    public ConfigurationContextBuilder decreasePriority(PropertySource propertySource) {
        checkBuilderState();
        int index = propertySources.indexOf(propertySource);
        if(index<0){
            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
        }
        if(index>0){
            propertySources.remove(propertySource);
            propertySources.add(index-1, propertySource);
        }
        return this;
    }

    @Override
    public ConfigurationContextBuilder highestPriority(PropertySource propertySource) {
        checkBuilderState();
        int index = propertySources.indexOf(propertySource);
        if(index<0){
            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
        }
        if(index<(propertySources.size()-1)){
            propertySources.remove(propertySource);
            propertySources.add(propertySource);
        }
        return this;
    }

    @Override
    public ConfigurationContextBuilder lowestPriority(PropertySource propertySource) {
        checkBuilderState();
        int index = propertySources.indexOf(propertySource);
        if(index<0){
            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
        }
        if(index>0){
            propertySources.remove(propertySource);
            propertySources.add(0, propertySource);
        }
        return this;
    }

    @Override
    public final ConfigurationContextBuilder addPropertyFilters(PropertyFilter... filters){
        return addPropertyFilters(Arrays.asList(filters));
    }

    @Override
    public final ConfigurationContextBuilder addPropertyFilters(Collection<PropertyFilter> filters){
        checkBuilderState();
        for(PropertyFilter f:filters) {
            if (!this.propertyFilters.contains(f)) {
                this.propertyFilters.add(f);
            }
        }
        return this;
    }

    @Override
    public final ConfigurationContextBuilder removePropertyFilters(PropertyFilter... filters) {
        return removePropertyFilters(Arrays.asList(filters));
    }

    @Override
    public final ConfigurationContextBuilder removePropertyFilters(Collection<PropertyFilter> filters) {
        checkBuilderState();
        this.propertyFilters.removeAll(filters);
        return this;
    }


    @Override
    public final <T> ConfigurationContextBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                                    @SuppressWarnings("unchecked") PropertyConverter<T>... converters) {
        return removePropertyConverters(typeToConvert, Arrays.asList(converters));
    }

    @Override
    public final <T> ConfigurationContextBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                                    Collection<PropertyConverter<T>> converters) {
        Collection<PropertyConverter<?>> subConverters = this.propertyConverters.get(typeToConvert);
        if(subConverters!=null) {
            subConverters.removeAll(converters);
        }
        return this;
    }

    @Override
    public final ConfigurationContextBuilder removePropertyConverters(TypeLiteral<?> typeToConvert) {
        this.propertyConverters.remove(typeToConvert);
        return this;
    }


    @Override
    public final ConfigurationContextBuilder setPropertyValueCombinationPolicy(PropertyValueCombinationPolicy combinationPolicy){
        checkBuilderState();
        this.combinationPolicy = Objects.requireNonNull(combinationPolicy);
        return this;
    }


    @Override
    public <T> ConfigurationContextBuilder addPropertyConverters(TypeLiteral<T> type, PropertyConverter<T>... propertyConverters){
        checkBuilderState();
        Objects.requireNonNull(type);
        Objects.requireNonNull(propertyConverters);
        Collection<PropertyConverter<?>> converters = this.propertyConverters.get(type);
        if(converters==null){
            converters = new ArrayList<>();
            this.propertyConverters.put(type, converters);
        }
        for(PropertyConverter<T> propertyConverter:propertyConverters) {
            if (!converters.contains(propertyConverter)) {
                converters.add(propertyConverter);
            } else {
                LOG.warning("Converter ignored, already registered: " + propertyConverter);
            }
        }
        return this;
    }

    @Override
    public <T> ConfigurationContextBuilder addPropertyConverters(TypeLiteral<T> type, Collection<PropertyConverter<T>> propertyConverters){
        checkBuilderState();
        Objects.requireNonNull(type);
        Objects.requireNonNull(propertyConverters);
        Collection<PropertyConverter<?>> converters = this.propertyConverters.get(type);
        if(converters==null){
            converters = new ArrayList<>();
            this.propertyConverters.put(type, converters);
        }
        for(PropertyConverter<T> propertyConverter:propertyConverters) {
            if (!converters.contains(propertyConverter)) {
                converters.add(propertyConverter);
            } else {
                LOG.warning("Converter ignored, already registered: " + propertyConverter);
            }
        }
        return this;
    }

    protected ConfigurationContextBuilder loadDefaults() {
        checkBuilderState();
        this.combinationPolicy = PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
        addDefaultPropertySources();
        addDefaultPropertyFilters();
        addDefaultPropertyConverters();
        return this;
    }


    protected Map<TypeLiteral, Collection<PropertyConverter>> getDefaultPropertyConverters() {
        Map<TypeLiteral, Collection<PropertyConverter>> result = new HashMap<>();
        for (PropertyConverter conv : ServiceContextManager.getServiceContext().getServices(
                PropertyConverter.class)) {
            for(Type type:conv.getClass().getGenericInterfaces()){
                if(type instanceof ParameterizedType){
                    ParameterizedType pt = (ParameterizedType)type;
                    if(PropertyConverter.class.equals(((ParameterizedType) type).getRawType())){
                        TypeLiteral target = TypeLiteral.of(pt.getActualTypeArguments()[0]);
                        Collection<PropertyConverter> convList = result.get(target);
                        if (convList == null) {
                            convList = new ArrayList<>();
                            result.put(target, convList);
                        }
                        convList.add(conv);
                    }
                }
            }
        }
        return result;
    }


    /**
     * Builds a new configuration based on the configuration of this builder instance.
     *
     * @return a new {@link org.apache.tamaya.Configuration configuration instance},
     *         never {@code null}.
     */
    @Override
    public ConfigurationContext build() {
        checkBuilderState();
        built = true;
        return new DefaultConfigurationContext(this);
    }

    @Override
    public ConfigurationContextBuilder sortPropertyFilter(Comparator<PropertyFilter> comparator) {
        Collections.sort(propertyFilters, comparator);
        return this;
    }

    @Override
    public ConfigurationContextBuilder sortPropertySources(Comparator<PropertySource> comparator) {
        Collections.sort(propertySources, comparator);
        return this;
    }

    private void checkBuilderState() {
        if (built) {
            throw new IllegalStateException("Configuration has already been build.");
        }
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return propertyFilters;
    }

    @Override
    public Map<TypeLiteral<?>, Collection<PropertyConverter<?>>> getPropertyConverter() {
        return Collections.unmodifiableMap(this.propertyConverters);
    }
}
