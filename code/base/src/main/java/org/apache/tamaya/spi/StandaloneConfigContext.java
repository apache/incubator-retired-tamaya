///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package org.apache.tamaya.spi;
//
//import org.apache.tamaya.base.configsource.ConfigSourceComparator;
//import org.apache.tamaya.base.convert.ConverterManager;
//
//import javax.config.spi.ConfigSource;
//import javax.config.spi.Converter;
//import java.lang.reflect.ParameterizedType;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.concurrent.locks.ReentrantReadWriteLock;
//import java.util.logging.Logger;
//
///**
// * Central SPI for programmatically dealing with the setup of the configuration system.
// * This includes adding and enlisting {@link ConfigSource}s,
// * managing {@link Converter}s, ConfigFilters, etc.
// */
//public final class StandaloneConfigContext implements ConfigContext{
//
//    private static final Logger LOG = Logger.getLogger(StandaloneConfigContext.class.getName());
//
//    /**
//     * Subcomponent handling {@link Converter} instances.
//     */
//    private final ConverterManager converterManager = new ConverterManager();
//
//
//
//    /**
//     * The current unmodifiable list of loaded {@link Filter} instances.
//     */
//    private final List<Filter> immutableFilters;
//
//    /**
//     * The current unmodifiable list of loaded {@link ConfigSource} instances.
//     */
//    @Override
//    public List<ConfigSource> getSources(){
//        return immutableConfigSources;
//    }
//
//    /**
//     * The current unmodifiable list of loaded {@link Filter} instances.
//     */
//    @Override
//    public List<Filter> getFilters(){
//        return immutableFilters;
//    }
//
//
//    /**
//     * Lock for internal synchronization.
//     */
//    final ReentrantReadWriteLock propertySourceLock = new ReentrantReadWriteLock();
//
//    @SuppressWarnings("unchecked")
//    StandaloneConfigContext(StandaloneConfigContextBuilder builder) {
//        List<ConfigSource> propertySources = new ArrayList<>();
//        // first we load all PropertySources which got registered via java.util.ServiceLoader
//        propertySources.addAll(builder.propertySources);
//        // now sort them according to their ordinal values
//        immutableConfigSources = Collections.unmodifiableList(propertySources);
//
//        // as next step we pick up the PropertyFilters pretty much the same way
//        List<Filter> filters = new ArrayList<>(builder.getFilters());
//        immutableFilters = Collections.unmodifiableList(filters);
//
//        // Finally add the converters
//        for(Map.Entry<Type, Collection<Converter>> en:builder.getConverter().entrySet()) {
//            for (@SuppressWarnings("rawtypes") Converter converter : en.getValue()) {
//                this.converterManager.addSources(en.getKey(), converter);
//            }
//        }
//        LOG.info("Registered " + converterManager.getConverters().size() + " property converters: " +
//                converterManager.getConverters());
//
//        configValueCombinationPolicy = builder.combinationPolicy;
//
//    }
//
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (!(o instanceof StandaloneConfigContext)){
//            return false;
//        }
//
//        StandaloneConfigContext that = (StandaloneConfigContext) o;
//
//        if (!converterManager.equals(that.converterManager)) {
//            return false;
//        }
//        if (!immutableConfigSources.equals(that.immutableConfigSources)) {
//            return false;
//        }
//        if (!immutableFilters.equals(that.immutableFilters)) {
//            return false;
//        }
//        return getConfigValueCombinationPolicy().equals(that.getConfigValueCombinationPolicy());
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = converterManager.hashCode();
//        result = 31 * result + immutableConfigSources.hashCode();
//        result = 31 * result + immutableFilters.hashCode();
//        result = 31 * result + getConfigValueCombinationPolicy().hashCode();
//        return result;
//    }
//
//    @Override
//    public String toString() {
//        StringBuilder b = new StringBuilder("ConfigurationContext{\n");
//        b.append("  Property Sources\n");
//        b.append("  ----------------\n");
//        if(immutableConfigSources.isEmpty()){
//            b.append("  No property sources loaded.\n\n");
//        }else {
//            b.append("  CLASS                         NAME                                                                  ORDINAL SCANNABLE SIZE    STATE     ERROR\n\n");
//            for (ConfigSource ps : immutableConfigSources) {
//                b.append("  ");
//                appendFormatted(b, ps.getClass().getSimpleName(), 30);
//                appendFormatted(b, ps.getName(), 70);
//                appendFormatted(b, String.valueOf(ConfigSourceComparator.getOrdinal(ps)), 8);
//                String state = ps.getValue("_state");
//                if(state==null){
//                    appendFormatted(b, "OK", 10);
//                }else {
//                    appendFormatted(b, state, 10);
//                    if("ERROR".equals(state)){
//                        String val = ps.getValue("_exception");
//                        if(val!=null) {
//                            appendFormatted(b, val, 30);
//                        }
//                    }
//                }
//                b.append('\n');
//            }
//            b.append("\n");
//        }
//        b.append("  Property Filters\n");
//        b.append("  ----------------\n");
//        if(immutableFilters.isEmpty()){
//            b.append("  No property filters loaded.\n\n");
//        }else {
//            b.append("  CLASS                         INFO\n\n");
//            for (Filter filter : getPropertyFilters()) {
//                b.append("  ");
//                appendFormatted(b, filter.getClass().getSimpleName(), 30);
//                b.append(removeNewLines(filter.toString()));
//                b.append('\n');
//            }
//            b.append("\n\n");
//        }
//        b.append("  Property Converters\n");
//        b.append("  -------------------\n");
//        b.append("  CLASS                         TYPE                          INFO\n\n");
//        for(Map.Entry<Type, List<Converter>> converterEntry: getConverters().entrySet()){
//            for(Converter converter: converterEntry.getValue()){
//                b.append("  ");
//                appendFormatted(b, converter.getClass().getSimpleName(), 30);
//                if(converterEntry.getKey() instanceof ParameterizedType){
//                    ParameterizedType pt = (ParameterizedType)converterEntry.getKey();
//                    appendFormatted(b, pt.getRawType().getTypeName(), 30);
//                }else{
//                    appendFormatted(b, converterEntry.getKey().getTypeName(), 30);
//                }
//                b.append(removeNewLines(converter.toString()));
//                b.append('\n');
//            }
//        }
//        b.append("\n\n");
//        b.append("  PropertyValueCombinationPolicy: " + getConfigValueCombinationPolicy().getClass().getName()).append('\n');
//        b.append('}');
//        return b.toString();
//    }
//
//
//
//
//
//    /**
//     * <p>
//     * This method returns the Map of registered PropertyConverters
//     * per type.
//     * The List for each type is ordered via their {@link javax.annotation.Priority} and
//     * cladd name.
//     * </p>
//     *
//     * <p>A simplified scenario could be like:</p>
//     * <pre>
//     *  {
//     *      Date.class -&gt; {StandardDateConverter, TimezoneDateConverter, MyCustomDateConverter }
//     *      Boolean.class -&gt; {StandardBooleanConverter, FrenchBooleanConverter}
//     *      Integer.class -&gt; {DynamicDefaultConverter}
//     *  }
//     * </pre>
//     *
//     * @return map with sorted list of registered PropertySources per type.
//     */
//    public Map<Type, List<Converter>> getConverters() {
//        return converterManager.getConverters();
//    }
//
//    /**
//     * <p>
//     * This method returns the registered PropertyConverters for a given type.
//     * The List for each type is ordered via their {@link javax.annotation.Priority}.
//     * </p>
//     *
//     * <p>
//     * PropertyConverters with a higher Priority come first. The PropertyConverter with the
//     * lowest Priority comes last.
//     * If two PropertyConverter have the same ordinal number they will get sorted
//     * using their class name just to ensure the user at least gets the same ordering
//     * after a JVM restart.
//     * </p>
//     *
//     * <p>
//     * Additionally if a PropertyProvider is accessed, which is not registered the implementation
//     * should try to figure out, if there could be a default implementation as follows:</p>
//     * <ol>
//     *     <li>Look for static factory methods: {@code of(String), valueOf(String), getInstance(String),
//     *     instanceOf(String), fomr(String)}</li>
//     *     <li>Look for a matching constructor: {@code T(String)}.</li>
//     * </ol>
//     *
//     * <p>
//     * If a correspoding factory method or constructor could be found, a corresponding
//     * PropertyConverter should be created and registered automatically for the given
//     * type.
//     * </p>
//     *
//     * <p> The scenario could be like:</p>
//     *
//     * <pre>
//     *  {
//     *      Date.class -&gt; {MyCustomDateConverter,StandardDateConverter, TimezoneDateConverter}
//     *      Boolean.class -&gt; {StandardBooleanConverter, FrenchBooleanConverter}
//     *      Integer.class -&gt; {DynamicDefaultConverter}
//     *  }
//     * </pre>
//     *
//     * <p>
//     * The converters returned for a type should be used as a chain, whereas the result of the
//     * first converters that is able to convert the configured value, is taken as the chain's result.
//     * No more converters are called after a converters has successfully converted the input into
//     * the required target type.
//     * </p>
//     *
//     * @param type type of the desired converters
//     * @return a sorted list of registered PropertySources per type.
//     */
//    public List<Converter> getConverters(Type type) {
//        return converterManager.getConverters(type);
//    }
//
//    /**
//     * Access the current PropertyFilter instances.
//     * @return the list of registered PropertyFilters, never null.
//     */
//    public List<Filter> getPropertyFilters() {
//        return immutableFilters;
//    }
//
//
//
//}
//
