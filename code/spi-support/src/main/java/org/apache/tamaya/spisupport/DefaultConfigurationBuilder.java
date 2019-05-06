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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.spisupport.propertysource.CLIPropertySource;
import org.apache.tamaya.spisupport.propertysource.EnvironmentPropertySource;
import org.apache.tamaya.spisupport.propertysource.JavaConfigurationPropertySource;
import org.apache.tamaya.spisupport.propertysource.SystemPropertySource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Logger;

/**
 * Default implementation of {@link ConfigurationBuilder}.
 */
public class DefaultConfigurationBuilder implements ConfigurationBuilder {

    private static final Logger LOG = Logger.getLogger(DefaultConfigurationBuilder.class.getName());

    protected ServiceContext serviceContext = ServiceContextManager.getServiceContext();
    protected List<PropertyFilter> propertyFilters = new ArrayList<>();
    protected List<PropertySource> propertySources = new ArrayList<>();
    protected Map<TypeLiteral<?>, List<PropertyConverter<?>>> propertyConverters = new HashMap<>();
    protected MetadataProvider metaDataProvider = serviceContext.create(MetadataProvider.class, DefaultMetaDataProvider::new);

    /**
     * Flag if the config has already been built.
     * Configuration can be built only once
     */
    protected boolean built;

    /**
     * Creates a new builder instance.
     */
    public DefaultConfigurationBuilder() {
    }


    /**
     * Creates a new builder instance.
     * @param context the configuration context to be used, not null.
     */
    public DefaultConfigurationBuilder(ConfigurationContext context) {
        this.propertyConverters.putAll(context.getPropertyConverters());
        this.propertyFilters.addAll(context.getPropertyFilters());
        for(PropertySource ps:context.getPropertySources()) {
            addPropertySources(ps);
        }
    }

    /**
     * Creates a new builder instance initializing it with the given context.
     * @param configuration the configuration to be used, not null.
     */
    public DefaultConfigurationBuilder(Configuration configuration) {
        this(configuration.getContext());
    }

    @Override
    public ConfigurationBuilder setClassLoader(ClassLoader classLoader) {
        setServiceContext(ServiceContextManager.getServiceContext(classLoader));
        return this;
    }

    @Override
    public ClassLoader getClassLoader() {
        return serviceContext.getClassLoader();
    }

    @Override
    public ConfigurationBuilder setServiceContext(ServiceContext serviceContext) {
        checkBuilderState();
        this.serviceContext = Objects.requireNonNull(serviceContext);
        return this;
    }

    /**
     * Allows to setCurrent configuration context during unit tests.
     * @param configuration the configuration to be used, not null.
     */
    public ConfigurationBuilder setConfiguration(Configuration configuration) {
        setContext(configuration.getContext());
        return this;
    }


    @Override
    public ConfigurationBuilder setContext(ConfigurationContext context) {
        checkBuilderState();
        //noinspection deprecation
        this.propertyFilters.clear();
        this.propertyFilters.addAll(context.getPropertyFilters());
        this.propertySources.clear();
        for(PropertySource ps:context.getPropertySources()) {
            addPropertySources(ps);
        }
        this.propertyConverters.clear();
        this.propertyConverters.putAll(context.getPropertyConverters());
        return this;
    }

    @Override
    public ConfigurationBuilder setMeta(String property, String key, String value){
        this.metaDataProvider.setMeta(property, key, value);
        return this;
    }

    @Override
    public ConfigurationBuilder setMeta(String property, Map<String, String> metaData){
        this.metaDataProvider.setMeta(property, metaData);
        return this;
    }

    /**
     * Adds the given sources as property sources.
     *
     * @param sources property sources to addPropertyValue.
     * @return the current configuration builder.
     */
    @Override
    public ConfigurationBuilder addPropertySources(Collection<PropertySource> sources){
        checkBuilderState();
        for(PropertySource source:sources) {
            if (!this.propertySources.contains(source)) {
                this.propertySources.add(source);
            }
        }
        return this;
    }

    public ConfigurationBuilder addDefaultPropertyFilters() {
        checkBuilderState();
        for(PropertyFilter pf:serviceContext.getServices(PropertyFilter.class)){
            addPropertyFilters(pf);
        }
        return this;
    }

    public ConfigurationBuilder addDefaultPropertySources() {
        checkBuilderState();
        List<PropertySource> propertySources = new ArrayList<>();
        addCorePropertyResources(propertySources);
        for(PropertySource ps: serviceContext.getServices(PropertySource.class)) {
            if(!propertySources.contains(ps)){
                propertySources.add(ps);
            }
        }
        for(PropertySourceProvider provider:
                serviceContext.getServices(PropertySourceProvider.class)){
            propertySources.addAll(provider.getPropertySources());
        }
        Collections.sort(propertySources, PropertySourceComparator.getInstance());
        return addPropertySources(propertySources);
    }

    public ConfigurationBuilder addDefaultPropertyConverters() {
        checkBuilderState();
        addCorePropertyConverters();
        for(Map.Entry<TypeLiteral, Collection<PropertyConverter>> en:getDefaultPropertyConverters().entrySet()){
            for(PropertyConverter pc: en.getValue()) {
                addPropertyConverters(en.getKey(), pc);
            }
        }
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertySources(Collection<PropertySource> propertySources) {
        checkBuilderState();
        this.propertySources.removeAll(propertySources);
        return this;
    }

    @Override
    public List<PropertySource> getPropertySources() {
        return Collections.unmodifiableList(this.propertySources);
    }

    @Override
    public ConfigurationBuilder increasePriority(PropertySource propertySource) {
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
    public ConfigurationBuilder decreasePriority(PropertySource propertySource) {
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
    public ConfigurationBuilder highestPriority(PropertySource propertySource) {
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
    public ConfigurationBuilder lowestPriority(PropertySource propertySource) {
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
    public ConfigurationBuilder addPropertyFilters(Collection<PropertyFilter> filters){
        checkBuilderState();
        for(PropertyFilter f:filters) {
            if (!this.propertyFilters.contains(f)) {
                this.propertyFilters.add(f);
            }
        }
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertyFilters(Collection<PropertyFilter> filters) {
        checkBuilderState();
        this.propertyFilters.removeAll(filters);
        return this;
    }

    @Override
    public <T> ConfigurationBuilder removePropertyConverters(TypeLiteral<T> typeToConvert,
                                                                    Collection<PropertyConverter<T>> converters) {
        Collection<PropertyConverter<?>> subConverters = this.propertyConverters.get(typeToConvert);
        if(subConverters!=null) {
            subConverters.removeAll(converters);
        }
        return this;
    }

    @Override
    public ConfigurationBuilder removePropertyConverters(TypeLiteral<?> typeToConvert) {
        this.propertyConverters.remove(typeToConvert);
        return this;
    }

    @Override
    public <T> ConfigurationBuilder addPropertyConverters(TypeLiteral<T> type, Collection<PropertyConverter<T>> propertyConverters){
        checkBuilderState();
        Objects.requireNonNull(type);
        Objects.requireNonNull(propertyConverters);
        List<PropertyConverter<?>> converters = this.propertyConverters.get(type);
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

    /**
     * Builds a new configuration based on the configuration of this builder instance.
     *
     * @return a new {@link org.apache.tamaya.Configuration} configuration instance,
     *         never {@code null}.
     */
    @Override
    public Configuration build() {
        Configuration cfg = new DefaultConfiguration(
                new DefaultConfigurationContext(
                        serviceContext,
                        this.propertyFilters,
                        this.propertySources,
                        this.propertyConverters,
                        this.metaDataProvider));
        this.built = true;
        return cfg;
    }

    @Override
    public ConfigurationBuilder sortPropertyFilter(Comparator<PropertyFilter> comparator) {
        Collections.sort(propertyFilters, comparator);
        return this;
    }

    @Override
    public ConfigurationBuilder sortPropertySources(Comparator<PropertySource> comparator) {
        Collections.sort(propertySources, comparator);
        return this;
    }

    @Override
    public List<PropertyFilter> getPropertyFilters() {
        return this.propertyFilters;
    }

    @Override
    public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverter() {
        return this.propertyConverters;
    }

    protected ConfigurationBuilder loadDefaults() {
        checkBuilderState();
        addDefaultPropertySources();
        addDefaultPropertyFilters();
        addDefaultPropertyConverters();
        return this;
    }

    protected Map<TypeLiteral, Collection<PropertyConverter>> getDefaultPropertyConverters() {
        Map<TypeLiteral, Collection<PropertyConverter>> result = new HashMap<>();
        for (PropertyConverter conv : serviceContext.getServices(
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

    protected void addCorePropertyResources(List<PropertySource> propertySources) {
        JavaConfigurationPropertySource jps = new JavaConfigurationPropertySource();
        jps.init(serviceContext.getClassLoader());
        for(PropertySource ps: new PropertySource[]{
                new EnvironmentPropertySource(),
                jps,
                new CLIPropertySource(),
                new SystemPropertySource()
        }){
            if(!propertySources.contains(ps)){
                propertySources.add(ps);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void addCorePropertyConverters() {
        // should be overridden by subclasses.
    }

    protected PropertySource getPropertySource(String name) {
        for(PropertySource ps:propertySources){
            if(ps.getName().equals(name)){
                return ps;
            }
        }
        throw new IllegalArgumentException("No such PropertySource: "+name);
    }

    private void checkBuilderState() {
        if (built) {
            throw new IllegalStateException("Configuration has already been build.");
        }
    }
}
