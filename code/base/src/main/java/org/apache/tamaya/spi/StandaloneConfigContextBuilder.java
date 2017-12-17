///*
// * Licensed to the Apache Software Foundation (ASF) under one
// *  or more contributor license agreements.  See the NOTICE file
// *  distributed with this work for additional information
// *  regarding copyright ownership.  The ASF licenses this file
// *  to you under the Apache License, Version 2.0 (the
// *  "License"); you may not use this file except in compliance
// *  with the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing,
// *  software distributed under the License is distributed on an
// *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// *  KIND, either express or implied.  See the License for the
// *  specific language governing permissions and limitations
// *  under the License.
// */
//package org.apache.tamaya.spi;
//
//import org.apache.tamaya.base.configsource.ConfigSourceComparator;
//import org.apache.tamaya.base.configsource.CLIConfigSource;
//import org.apache.tamaya.base.configsource.EnvironmentConfigSource;
//import org.apache.tamaya.base.configsource.JavaConfigurationConfigSource;
//import org.apache.tamaya.base.configsource.SystemConfigSource;
//
//import javax.config.spi.ConfigSource;
//import javax.config.spi.ConfigSourceProvider;
//import javax.config.spi.Converter;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.logging.Logger;
//
///**
// * Default implementation of {@link StandaloneConfigContextBuilder}.
// */
//public final class StandaloneConfigContextBuilder {
//
//    private static final Logger LOG = Logger.getLogger(StandaloneConfigContextBuilder.class.getName());
//
//    protected List<Filter> filters = new ArrayList<>();
//    protected List<ConfigSource> propertySources = new ArrayList<>();
//    protected ConfigValueCombinationPolicy combinationPolicy = ConfigValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;
//    protected Map<Type, Collection<Converter>> propertyConverters = new HashMap<>();
//
//    /**
//     * Flag if the config has already been built.
//     * Configuration can be built only once
//     */
//    private boolean built;
//    private ClassLoader classLoader;
//
//    /**
//     * Creates a new builder instance.
//     */
//    public StandaloneConfigContextBuilder() {
//    }
//
//    /**
//     * Creates a new builder instance initializing it with the given context.
//     * @param context the context to be used, not null.
//     */
//    public StandaloneConfigContextBuilder(ConfigContext context) {
//        this.propertyConverters.putAll(context.getConverters());
//        this.filters.addAll(context.getFilters());
//        for(ConfigSource ps:context.getSources()) {
//            withSources(ps);
//        }
//        this.combinationPolicy = context.getConfigValueCombinationPolicy();
//    }
//
//    /**
//     * Allows to reset configuration context during unit tests.
//     */
//    public final StandaloneConfigContextBuilder reset() {
//        checkBuilderState();
//        this.filters.clear();
//        this.propertySources.clear();
//        this.propertyConverters.clear();
//        this.combinationPolicy = ConfigValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;
//        return this;
//    }
//
//
//    public StandaloneConfigContextBuilder withContext(ConfigContext context) {
//        checkBuilderState();
//        this.propertyConverters.putAll(context.getConverters());
//        for(ConfigSource ps:context.getSources()){
//            this.propertySources.add(ps);
//        }
//        this.filters.addAll(context.getFilters());
//        this.combinationPolicy = context.getConfigValueCombinationPolicy();
//        return this;
//    }
//
//    public final StandaloneConfigContextBuilder withSources(ConfigSource... sources){
//        return withSources(Arrays.asList(sources));
//    }
//
//    public StandaloneConfigContextBuilder withSources(Collection<ConfigSource> sources){
//        checkBuilderState();
//        for(ConfigSource source:sources) {
//            if (!this.propertySources.contains(source)) {
//                this.propertySources.add(source);
//            }
//        }
//        return this;
//    }
//
//
//    public StandaloneConfigContextBuilder addDiscoveredSources() {
//        checkBuilderState();
//        List<ConfigSource> propertySources = new ArrayList<>();
////        addDiscoveredSources(propertySources);
//        for(ConfigSource ps: ServiceContextManager.getServiceContext().getServices(ConfigSource.class, classLoader)) {
//            if(!propertySources.contains(ps)){
//                propertySources.add(ps);
//            }
//        }
//
//        for(ConfigSourceProvider provider:
//                ServiceContextManager.getServiceContext().getServices(ConfigSourceProvider.class, classLoader)){
//                for(ConfigSource src: provider.getConfigSources(classLoader)){
//                    propertySources.add(src);
//                }
//        }
//        Collections.sort(propertySources, ConfigSourceComparator.getInstance());
//        return withSources(propertySources);
//    }
//
//    private StandaloneConfigContextBuilder addDiscoveredSources(List<ConfigSource> propertySources) {
//        for(ConfigSource ps: new ConfigSource[]{
//                new EnvironmentConfigSource(),
//                new JavaConfigurationConfigSource(),
//                new CLIConfigSource(),
//                new SystemConfigSource()
//        }){
//            if(!propertySources.contains(ps)){
//                propertySources.add(ps);
//            }
//        }
//        return this;
//    }
//
//    public StandaloneConfigContextBuilder addDiscoveredPropertyFilters() {
//        checkBuilderState();
//        for(Filter pf:ServiceContextManager.getServiceContext().getServices(Filter.class, classLoader)){
//            withFilters(pf);
//        }
//        return this;
//    }
//
//    public StandaloneConfigContextBuilder addDiscoveredConverters() {
//        checkBuilderState();
//        addDiscoveredConverters();
//        for(Map.Entry<Type, Collection<Converter>> en: getDefaultConverters().entrySet()){
//            for(Converter pc: en.getValue()) {
//                withConverters(en.getKey(), pc);
//            }
//        }
//        return this;
//    }
//
//    @SuppressWarnings("unchecked")
//    public void addDiscoveredConverters() {
//        // should be overridden by subclasses.
//    }
//
//    public final StandaloneConfigContextBuilder removeSources(ConfigSource... propertySources) {
//        return removeSources(Arrays.asList(propertySources));
//    }
//
//    public StandaloneConfigContextBuilder removeSources(Collection<ConfigSource> propertySources) {
//        checkBuilderState();
//        this.propertySources.removeAll(propertySources);
//        return this;
//    }
//
//    protected ConfigSource getSource(String name) {
//        for(ConfigSource ps:propertySources){
//            if(ps.getName().equals(name)){
//                return ps;
//            }
//        }
//        throw new IllegalArgumentException("No such PropertySource: "+name);
//    }
//
//    public List<ConfigSource> getSources() {
//        return Collections.unmodifiableList(this.propertySources);
//    }
//
//    public StandaloneConfigContextBuilder increasePriority(ConfigSource propertySource) {
//        checkBuilderState();
//        int index = propertySources.indexOf(propertySource);
//        if(index<0){
//            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
//        }
//        if(index<(propertySources.size()-1)){
//            propertySources.remove(propertySource);
//            propertySources.add(index+1, propertySource);
//        }
//        return this;
//    }
//
//    public StandaloneConfigContextBuilder decreasePriority(ConfigSource propertySource) {
//        checkBuilderState();
//        int index = propertySources.indexOf(propertySource);
//        if(index<0){
//            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
//        }
//        if(index>0){
//            propertySources.remove(propertySource);
//            propertySources.add(index-1, propertySource);
//        }
//        return this;
//    }
//
//    public StandaloneConfigContextBuilder highestPriority(ConfigSource propertySource) {
//        checkBuilderState();
//        int index = propertySources.indexOf(propertySource);
//        if(index<0){
//            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
//        }
//        if(index<(propertySources.size()-1)){
//            propertySources.remove(propertySource);
//            propertySources.add(propertySource);
//        }
//        return this;
//    }
//
//    public StandaloneConfigContextBuilder lowestPriority(ConfigSource propertySource) {
//        checkBuilderState();
//        int index = propertySources.indexOf(propertySource);
//        if(index<0){
//            throw new IllegalArgumentException("No such PropertySource: " + propertySource);
//        }
//        if(index>0){
//            propertySources.remove(propertySource);
//            propertySources.add(0, propertySource);
//        }
//        return this;
//    }
//
//    public final StandaloneConfigContextBuilder withFilters(Filter... filters){
//        return withFilters(Arrays.asList(filters));
//    }
//
//    public final StandaloneConfigContextBuilder withFilters(Collection<Filter> filters){
//        checkBuilderState();
//        for(Filter f:filters) {
//            if (!this.filters.contains(f)) {
//                this.filters.add(f);
//            }
//        }
//        return this;
//    }
//
//    public final StandaloneConfigContextBuilder removeFilters(Filter... filters) {
//        return removeFilters(Arrays.asList(filters));
//    }
//
//    public final StandaloneConfigContextBuilder removeFilters(Collection<Filter> filters) {
//        checkBuilderState();
//        this.filters.removeAll(filters);
//        return this;
//    }
//
//
//    public final <T> StandaloneConfigContextBuilder removeConverters(Type typeToConvert,
//                                                                     @SuppressWarnings("unchecked") Converter<T>... converters) {
//        return removeConverters(typeToConvert, Arrays.asList(converters));
//    }
//
//    public final <T> StandaloneConfigContextBuilder removeConverters(Type typeToConvert,
//                                                                     Collection<Converter<T>> converters) {
//        Collection<Converter> subConverters = this.propertyConverters.get(typeToConvert);
//        if(subConverters!=null) {
//            subConverters.removeAll(converters);
//        }
//        return this;
//    }
//
//    public final StandaloneConfigContextBuilder removeConverters(TypeLiteral<?> typeToConvert) {
//        this.propertyConverters.remove(typeToConvert);
//        return this;
//    }
//
//
//    public final StandaloneConfigContextBuilder withPropertyValueCombinationPolicy(ConfigValueCombinationPolicy combinationPolicy){
//        checkBuilderState();
//        this.combinationPolicy = Objects.requireNonNull(combinationPolicy);
//        return this;
//    }
//
//
//    public <T> StandaloneConfigContextBuilder withConverters(Type type, Converter<T>... propertyConverters){
//        checkBuilderState();
//        Objects.requireNonNull(type);
//        Objects.requireNonNull(propertyConverters);
//        Collection<Converter> converters = this.propertyConverters.get(type);
//        if(converters==null){
//            converters = new ArrayList<>();
//            this.propertyConverters.put(type, converters);
//        }
//        for(Converter<T> propertyConverter:propertyConverters) {
//            if (!converters.contains(propertyConverter)) {
//                converters.add(propertyConverter);
//            } else {
//                LOG.warning("Converter ignored, already registered: " + propertyConverter);
//            }
//        }
//        return this;
//    }
//
//    public <T> StandaloneConfigContextBuilder withConverters(Type type, Collection<Converter<T>> propertyConverters){
//        checkBuilderState();
//        Objects.requireNonNull(type);
//        Objects.requireNonNull(propertyConverters);
//        Collection<Converter> converters = this.propertyConverters.get(type);
//        if(converters==null){
//            converters = new ArrayList<>();
//            this.propertyConverters.put(type, converters);
//        }
//        for(Converter<T> propertyConverter:propertyConverters) {
//            if (!converters.contains(propertyConverter)) {
//                converters.add(propertyConverter);
//            } else {
//                LOG.warning("Converter ignored, already registered: " + propertyConverter);
//            }
//        }
//        return this;
//    }
//
//    protected StandaloneConfigContextBuilder loadDefaults() {
//        checkBuilderState();
//        this.combinationPolicy = ConfigValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
//        addDiscoveredSources();
//        addDiscoveredPropertyFilters();
//        addDiscoveredConverters();
//        return this;
//    }
//
//
//    protected Map<Type, Collection<Converter>> getDefaultConverters() {
//        Map<Type, Collection<Converter>> result = new HashMap<>();
//        for (Converter conv : ServiceContextManager.getServiceContext().getServices(
//                Converter.class, classLoader)) {
//            for(Type type:conv.getClass().getGenericInterfaces()){
//                if(type instanceof ParameterizedType){
//                    ParameterizedType pt = (ParameterizedType)type;
//                    if(Converter.class.equals(((ParameterizedType) type).getRawType())){
//                        Type target = pt.getActualTypeArguments()[0];
//                        Collection<Converter> convList = result.get(target);
//                        if (convList == null) {
//                            convList = new ArrayList<>();
//                            result.put(target, convList);
//                        }
//                        convList.add(conv);
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//
//    /**
//     * Builds a new configuration based on the configuration of this builder instance.
//     *
//     * @return a new {@link javax.config.Config configuration instance},
//     *         never {@code null}.
//     */
//    public ConfigContext build() {
//        checkBuilderState();
//        built = true;
//        return new StandaloneConfigContext(this);
//    }
//
//    public StandaloneConfigContextBuilder sortFilter(Comparator<Filter> comparator) {
//        Collections.sort(filters, comparator);
//        return this;
//    }
//
//    public StandaloneConfigContextBuilder sortSources(Comparator<ConfigSource> comparator) {
//        Collections.sort(propertySources, comparator);
//        return this;
//    }
//
//    private void checkBuilderState() {
//        if (built) {
//            throw new IllegalStateException("Configuration has already been build.");
//        }
//    }
//
//    public List<Filter> getFilters() {
//        return filters;
//    }
//
//    public Map<Type, Collection<Converter>> getConverter() {
//        return Collections.unmodifiableMap(this.propertyConverters);
//    }
//
//    public void setClassLoader(ClassLoader classLoader) {
//        this.classLoader = classLoader;
//    }
//}
