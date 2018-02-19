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
package org.apache.tamaya.base;

import org.apache.tamaya.base.convert.ConverterManager;
import org.apache.tamaya.base.filter.Filter;
import org.apache.tamaya.base.filter.FilterManager;

import javax.config.Config;
import javax.config.spi.ConfigSource;
import javax.config.spi.Converter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Central SPI for programmatically dealing with the setup of the configuration system.
 * This includes adding and enlisting {@link javax.config.spi.ConfigSource}s,
 * managing {@link javax.config.spi.Converter}s, ConfigFilters, etc.
 */
public interface ConfigContext {

    /**
     * Get a context from the given {@link Config}. If the {@link Config} implements
     * {@link ConfigContextSupplier} it is cast and the result will be returned. If the
     * config does not implement {@link ConfigContextSupplier}, a default context is created,
     * which includes all convereters as defined by {@link ConverterManager#defaultInstance()#getConverters()},
     * an empty filter list and the {@link ConfigSource}s as declared by the given {@link Config}
     * instance.
     * @param config the config instance, not null.
     * @return a context instance, never null.
     */
    static ConfigContext from(Config config){
        if(config instanceof ConfigContextSupplier){
            return ((ConfigContextSupplier)config).getConfigContext();
        }
        return new ConfigContext() {
            @Override
            public Iterable<ConfigSource> getConfigSources() {
                return Objects.requireNonNull(config.getConfigSources());
            }

            @Override
            public List<Filter> getFilters() {
                return Collections.emptyList();
            }

            @Override
            public Map<Type, List<Converter>> getConverters() {
                return ConverterManager.defaultInstance().getConverters();
            }

            @Override
            public String toString() {
                return "ConfigContext#default{\n  delegate:"+config+"\n}";
            }
        };
    }

    /**
     * This method returns the current list of registered PropertySources ordered via their ordinal.
     * PropertySources with a lower ordinal come last. The PropertySource with the
     * highest ordinal comes first.
     * If two PropertySources have the same ordinal number they will get sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after a JVM restart, hereby names before are added last.
     * PropertySources are loaded when this method is called the first time, which basically is
     * when the first time configuration is accessed.
     *
     * @return a sorted list of registered PropertySources.  The returned list need not be modifiable
     */
    Iterable<ConfigSource> getConfigSources();

//    /**
//     * Access a {@link ConfigSource} using its (unique) name.
//     * @param name the propoerty source's name, not {@code null}.
//     * @return the propoerty source found, or {@code null}.
//     */
//    default ConfigSource getSource(String name) {
//        for(ConfigSource ps: getConfigSources()){
//            if(name.equals(ps.getName())){
//                return ps;
//            }
//        }
//        return null;
//    }

    /**
     * Access the current PropertyFilter instances.
     * @return the list of registered PropertyFilters, never null.
     */
    List<Filter> getFilters();

    /**
     * <p>
     * This method returns the Map of registered PropertyConverters
     * per type.
     * The List for each type is ordered via their {@link javax.annotation.Priority} and
     * cladd name.
     * </p>
     *
     * <p>A simplified scenario could be like:</p>
     * <pre>
     *  {
     *      Date.class -&gt; {StandardDateConverter, TimezoneDateConverter, MyCustomDateConverter }
     *      Boolean.class -&gt; {StandardBooleanConverter, FrenchBooleanConverter}
     *      Integer.class -&gt; {DynamicDefaultConverter}
     *  }
     * </pre>
     *
     * @return map with sorted list of registered PropertySources per type.
     */
    Map<Type, List<Converter>> getConverters();

    /**
     * <p>
     * This method returns the registered PropertyConverters for a given type.
     * The List for each type is ordered via their {@link javax.annotation.Priority}.
     * </p>
     *
     * <p>
     * PropertyConverters with a higher Priority come first. The PropertyConverter with the
     * lowest Priority comes last.
     * If two PropertyConverter have the same ordinal number they will get sorted
     * using their class name just to ensure the user at least gets the same ordering
     * after a JVM restart.
     * </p>
     *
     * <p>
     * Additionally if a PropertyProvider is accessed, which is not registered the implementation
     * should try to figure out, if there could be a default implementation as follows:</p>
     * <ol>
     *     <li>Look for static factory methods: {@code of(String), valueOf(String), getInstance(String),
     *     instanceOf(String), fomr(String)}</li>
     *     <li>Look for a matching constructor: {@code T(String)}.</li>
     * </ol>
     *
     * <p>
     * If a correspoding factory method or constructor could be found, a corresponding
     * PropertyConverter should be created and registered automatically for the given
     * type.
     * </p>
     *
     * <p> The scenario could be like:</p>
     *
     * <pre>
     *  {
     *      Date.class -&gt; {MyCustomDateConverter,StandardDateConverter, TimezoneDateConverter}
     *      Boolean.class -&gt; {StandardBooleanConverter, FrenchBooleanConverter}
     *      Integer.class -&gt; {DynamicDefaultConverter}
     *  }
     * </pre>
     *
     * <p>
     * The converters returned for a type should be used as a chain, whereas the result of the
     * first converters that is able to convert the configured value, is taken as the chain's result.
     * No more converters are called after a converters has successfully converted the input into
     * the required target type.
     * </p>
     *
     * @param type type of the desired converters
     * @return a sorted list of registered PropertySources per type, or null.
     */
    default List<Converter> getConverters(Type type){
        return Optional.ofNullable(getConverters().get(type)).orElse(Collections.emptyList());
    }

    /**
     * Access the {@link ConfigValueCombinationPolicy} used to evaluate the final
     * property values.
     * @return the {@link ConfigValueCombinationPolicy} used, never null.
     */
    default ConfigValueCombinationPolicy getConfigValueCombinationPolicy(){
        return ConfigValueCombinationPolicy.DEFAULT_OVERRIDING_POLICY;
    }

}

