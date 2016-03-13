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
package org.apache.tamaya.functions;

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import java.net.Inet4Address;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Accessor that provides useful functions along with configuration.
 */
public final class ConfigurationFunctions {

    /**
     * The Logger used.
     */
    private static final Logger LOG = Logger.getLogger(ConfigurationFunctions.class.getName());

    /**
     * Implementation of an empty propertySource.
     */
    private static final Configuration EMPTY_CONFIGURATION = new Configuration() {

        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public String getOrDefault(String key, String defaultValue) {
            return defaultValue;
        }

        @Override
        public <T> T getOrDefault(String key, Class<T> type, T defaultValue) {
            return defaultValue;
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            return null;
        }

        @Override
        public <T> T get(String key, TypeLiteral<T> type) {
            return null;
        }

        @Override
        public <T> T getOrDefault(String key, TypeLiteral<T> type, T defaultValue) {
            return defaultValue;
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public Configuration with(ConfigOperator operator) {
            return operator.operate(this);
        }

        @Override
        public <T> T query(ConfigQuery<T> query) {
            return query.query(this);
        }

        @Override
        public ConfigurationContext getContext() {
            return EMPTY_CONFIGURATION_CONTEXT;
        }

        @Override
        public String toString(){
            return "Configuration<empty>";
        }
    };

    private static final ConfigurationContext EMPTY_CONFIGURATION_CONTEXT = new ConfigurationContext() {
        @Override
        public void addPropertySources(PropertySource... propertySourcesToAdd) {
            // ignore
        }

        @Override
        public List<PropertySource> getPropertySources() {
            return Collections.emptyList();
        }

        @Override
        public <T> void addPropertyConverter(TypeLiteral<T> typeToConvert, PropertyConverter<T> propertyConverter) {
            // ignore
        }

        @Override
        public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
            return Collections.emptyMap();
        }

        @Override
        public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> type) {
            return Collections.emptyList();
        }

        @Override
        public List<PropertyFilter> getPropertyFilters() {
            return Collections.emptyList();
        }

        @Override
        public PropertyValueCombinationPolicy getPropertyValueCombinationPolicy() {
            return PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
        }

        @Override
        public ConfigurationContextBuilder toBuilder() {
            throw new UnsupportedOperationException("Cannot build from ConfigurationContext.EMPTY.");
        }

        @Override
        public String toString(){
            return "ConfigurationContext.EMPTY";
        }
    };

    /**
     * Private singleton constructor.
     */
    private ConfigurationFunctions() {
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are selected by the given {@link PropertyMatcher}.
     *
     * @param filter the filter, not null
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator filter(final PropertyMatcher filter) {
        return new ConfigOperator() {
            @Override
            public Configuration operate(Configuration config) {
                return new FilteredConfiguration(config, filter, "FilterClass: " + filter.getClass().getName());
            }
        };
    }

    /**
     * Creates a ConfigOperator that creates a Configuration with keys mapped as
     * defined by the given keyMapper.
     *
     * @param keyMapper the keyMapper, not null
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator map(final KeyMapper keyMapper) {
        return new ConfigOperator() {
            @Override
            public Configuration operate(Configuration config) {
                return new MappedConfiguration(config, keyMapper, null);
            }
        };
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (non recursive). Hereby
     * the section key is stripped away fromMap the resulting key.
     *
     * @param areaKey the section key, not null
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator section(String areaKey) {
        return section(areaKey, false);
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (non recursive).
     *
     * @param areaKey   the section key, not null
     * @param stripKeys if set to true, the section key is stripped away fromMap the resulting key.
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator section(final String areaKey, final boolean stripKeys) {
        return new ConfigOperator() {
            @Override
            public Configuration operate(Configuration config) {
                Configuration filtered = new FilteredConfiguration(config,
                        new PropertyMatcher() {
                            @Override
                            public boolean test(String k, String v) {
                                return isKeyInSection(k, areaKey);
                            }
                        }, "section: " + areaKey);
                if (stripKeys) {
                    return new MappedConfiguration(filtered, new KeyMapper(){
                        @Override
                        public String mapKey(String key) {
                            return key.substring(areaKey.length());
                        }
                    }, "stripped");
                }
                return filtered;
            }
        };
    }

    /**
     * Calculates the current section key and compares it with the given key.
     *
     * @param key        the fully qualified entry key, not null
     * @param sectionKey the section key, not null
     * @return true, if the entry is exact in this section
     */
    public static boolean isKeyInSection(String key, String sectionKey) {
        return sectionKey.isEmpty() || key.startsWith(sectionKey);
    }

    /**
     * Calculates the current section key and compares it with the given section keys.
     *
     * @param key         the fully qualified entry key, not null
     * @param sectionKeys the section keys, not null
     * @return true, if the entry is exact in this section
     */
    public static boolean isKeyInSections(String key, String... sectionKeys) {
        for (String areaKey : sectionKeys) {
            if (isKeyInSection(key, areaKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return a query to evaluate the set with all fully qualifies section names. This method should return the sections as accurate as possible,
     * but may not provide a complete set of sections that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @return s set with all sections, never {@code null}.
     */
    public static ConfigQuery<Set<String>> sections() {
        return new ConfigQuery<Set<String>>() {
            @Override
            public Set<String> query(Configuration config) {
                final Set<String> areas = new TreeSet<>();
                for (String s : config.getProperties().keySet()) {
                    int index = s.lastIndexOf('.');
                    if (index > 0) {
                        areas.add(s.substring(0, index));
                    }
                }
                return areas;
            }
        };
    }

    /**
     * Return a query to evaluate the set with all fully qualified section names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not. This method should return the sections as accurate
     * as possible, but may not provide a complete set of sections that are finally accessible, especially when the
     * underlying storage does not support key iteration.
     *
     * @return s set with all transitive sections, never {@code null}.
     */
    public static ConfigQuery<Set<String>> transitiveSections() {
        return new ConfigQuery<Set<String>>() {
            @Override
            public Set<String> query(Configuration config) {
                final Set<String> transitiveAreas = new TreeSet<>();
                for (String s : config.query(sections())) {
                    transitiveAreas.add(s);
                    int index = s.lastIndexOf('.');
                    while (index > 0) {
                        s = s.substring(0, index);
                        transitiveAreas.add(s);
                        index = s.lastIndexOf('.');
                    }
                }
                return transitiveAreas;
            }
        };
    }

    /**
     * Return a query to evaluate the set with all fully qualified section names, containing only the
     * sections that match the predicate and have properties attached. This method should return the sections as accurate as possible,
     * but may not provide a complete set of sections that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @param predicate A predicate to deternine, which sections should be returned, not {@code null}.
     * @return s set with all sections, never {@code null}.
     */
    public static ConfigQuery<Set<String>> sections(final Predicate<String> predicate) {
        return new ConfigQuery<Set<String>>() {
            @Override
            public Set<String> query(Configuration config) {
                Set<String> result = new TreeSet<>();
                for (String s : sections().query(config)) {
                    if (predicate.test(s)) {
                        result.add(s);
                    }
                }
                return result;
            }
        };

    }

    /**
     * Return a query to evaluate the set with all fully qualified section names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not. This method should return the sections as accurate as possible,
     * but may not provide a complete set of sections that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @param predicate A predicate to deternine, which sections should be returned, not {@code null}.
     * @return s set with all transitive sections, never {@code null}.
     */
    public static ConfigQuery<Set<String>> transitiveSections(final Predicate<String> predicate) {
        return new ConfigQuery<Set<String>>() {
            @Override
            public Set<String> query(Configuration config) {
                Set<String> result = new TreeSet<>();
                for (String s : transitiveSections().query(config)) {
                    if (predicate.test(s)) {
                        result.add(s);
                    }
                }
                return result;
            }
        };
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (recursive).
     *
     * @param sectionKeys the section keys, not null
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator sectionsRecursive(String... sectionKeys) {
        return sectionRecursive(false, sectionKeys);
    }

    /**
     * Creates a Configuration that creates a new instance using the configuration instances provided. Hereby
     * values from higher instances override previous values..
     *
     * @param configName the new config name
     * @param configs    the configs to be combined. The entries of the first config are overwritten
     *                   by entries of the later instances.
     * @return the resulting configuration instance.
     */
    public static Configuration combine(String configName, Configuration... configs) {
        return new CombinedConfiguration(configName, configs);
    }

    /**
     * Creates a {@link PropertySource}, based on the given {@link Configuration}. The keys and propertx map
     * are dynamically calculated, so the returned PropertySource is a real dynamic wrapper.
     * @param name the name of the property source, not null.
     * @param ordinal ordinal of the property source.
     * @param config the config to be mapped, not null.
     * @return a property source wrapping the configuration.
     */
    public static PropertySource propertySourceFrom(final String name, final int ordinal, final Configuration config){
        return new ConfigWrappingPropertySource(name, ordinal, config);
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (recursive).
     *
     * @param sectionKeys the section keys, not null
     * @param stripKeys   if set to true, the section key is stripped away fromMap the resulting key.
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator sectionRecursive(final boolean stripKeys, final String... sectionKeys) {
        return new ConfigOperator() {
            @Override
            public Configuration operate(Configuration config) {
                Configuration filtered = new FilteredConfiguration(config, new PropertyMatcher() {
                    @Override
                    public boolean test(final String k, String v) {
                        return isKeyInSections(k, sectionKeys);
                    }
                }, "sections: " + Arrays.toString(sectionKeys));
                if (stripKeys) {
                    return new MappedConfiguration(filtered, new KeyMapper() {
                        @Override
                        public String mapKey(String key) {
                            return PropertySourceFunctions.stripSectionKeys(key, sectionKeys);
                        }
                    }, "stripped");
                }
                return filtered;
            }
        };
    }

    /**
     * Creates a ConfigQuery that creates a JSON formatted ouitput of all properties in the given configuration.
     *
     * @return the given query.
     */
    public static ConfigQuery<String> jsonInfo() {
        return jsonInfo(null);
    }

    /**
     * Creates a ConfigQuery that creates a JSON formatted ouitput of all properties in the given configuration.
     *
     * @param info the additional information attributes to be added to the output, e.g. the original request
     *             parameters.
     * @return the given query.
     */
    public static ConfigQuery<String> jsonInfo(final Map<String, String> info) {
        return new ConfigQuery<String>() {
            @Override
            public String query(Configuration config) {
                Map<String, String> props = new TreeMap<>(config.getProperties());
                props.put("__timestamp", String.valueOf(System.currentTimeMillis()));
                if(info!=null) {
                    for (Map.Entry<String, String> en : info.entrySet()) {
                        props.put("__" + escape(en.getKey()), escape(en.getValue()));
                    }
                }
                StringBuilder builder = new StringBuilder(400).append("{\n");
                for (Map.Entry<String, String> en : props.entrySet()) {
                    builder.append("  \"").append(escape(en.getKey())).append("\": \"" )
                            .append(escape(en.getValue())).append("\",\n");
                }
                if(builder.toString().endsWith(",\n")){
                    builder.setLength(builder.length()-2);
                    builder.append('\n');
                }
                builder.append("}\n");
                return builder.toString();
            }
        };
    }

    /**
     * Creates a ConfigQuery that creates a XML formatted ouitput of all properties in the given configuration.
     *
     * @return the given query.
     */
    public static ConfigQuery<String> xmlInfo() {
        return xmlInfo(null);
    }

    /**
     * Creates a ConfigQuery that creates a XML formatted ouitput of all properties in the given configuration.
     *
     * @param info the additional information attributes to be added to the output, e.g. the original request
     *             parameters.
     * @return the given query.
     */
    public static ConfigQuery<String> xmlInfo(final Map<String, String> info) {
        return new ConfigQuery<String>() {
            @Override
            public String query(Configuration config) {
                Map<String, String> props = new TreeMap<>(config.getProperties());
                props.put("__timestamp", String.valueOf(System.currentTimeMillis()));
                if(info!=null) {
                    for (Map.Entry<String, String> en : info.entrySet()) {
                        props.put("__" + escape(en.getKey()), escape(en.getValue()));
                    }
                }
                StringBuilder builder = new StringBuilder(400);
                builder.append("<configuration>\n");
                for (Map.Entry<String, String> en : props.entrySet()) {
                    builder.append("  <entry key=\"" + escape(en.getKey()) + "\">" + escape(en.getValue()) + "</entry>\n");
                }
                builder.append("</configuration>\n");
                return builder.toString();
            }
        };
    }

    /**
     * Creates a ConfigQuery that creates a plain text formatted output of all properties in the given configuration.
     *
     * @return the given query.
     */
    public static ConfigQuery<String> textInfo() {
        return textInfo(null);
    }

    /**
     * Creates a ConfigQuery that creates a plain text formatted output of all properties in the given configuration.
     * @param info configuration values to use for filtering.
     * @return the given query.
     */
    public static ConfigQuery<String> textInfo(final Map<String, String> info) {
        return new ConfigQuery<String>() {
            @Override
            public String query(Configuration config) {
                Map<String, String> props = new TreeMap<>(config.getProperties());
                props.put("__timestamp", String.valueOf(System.currentTimeMillis()));
                if(info!=null) {
                    for (Map.Entry<String, String> en : info.entrySet()) {
                        props.put("__" + escape(en.getKey()), escape(en.getValue()));
                    }
                }
                StringBuilder builder = new StringBuilder(400).append("Configuration:\n");
                for (Map.Entry<String, String> en : props.entrySet()) {
                    builder.append("  " + escape(en.getKey()) + ": " + escape(en.getValue()).replace("\n", "\n     ") + ",\n");
                }
                if(builder.toString().endsWith(",\n")){
                    builder.setLength(builder.length() - 2);
                }
                builder.append("\n");
                return builder.toString();
            }
        };
    }

    /**
     * Creates a ConfigOperator that adds the given items.
     * @param items the items to be added/replaced.
     * @param override if true, all items existing are overridden by the new ones passed.
     * @return the ConfigOperator, never null.
     */
    public static ConfigOperator addItems(final Map<String,Object> items, final boolean override){
        return new ConfigOperator() {
            @Override
            public Configuration operate(Configuration config) {
                return new EnrichedConfiguration(config,items, override);
            }
        };
    }

    /**
     * Creates an operator that adds items to the instance.
     * @param items the items, not null.
     * @return the operator, never null.
     */
    public static ConfigOperator addItems(Map<String,Object> items){
        return addItems(items, false);
    }

    /**
     * Creates an operator that replaces the given items.
     * @param items the items.
     * @return the operator for replacing the items.
     */
    public static ConfigOperator replaceItems(Map<String,Object> items){
        return addItems(items, true);
    }

    /**
     * Creates a ConfigQuery that creates a html formatted ouitput of all properties in the given configuration.
     *
     * @return the given query.
     */
    public static ConfigQuery<String> htmlInfo() {
        return htmlInfo(null);
    }

    /**
     * Creates a ConfigQuery that creates a html formatted ouitput of all properties in the given configuration.
     * @param info configuration values to use for filtering.
     * @return the given query.
     */
    public static ConfigQuery<String> htmlInfo(final Map<String, String> info) {
        return new ConfigQuery<String>() {
            @Override
            public String query(Configuration config) {
                StringBuilder builder = new StringBuilder();
                addHeader(builder);
                builder.append("<pre>\n").append(textInfo(info).query(config)).append("</pre>\n");
                addFooter(builder);
                return builder.toString();
            }
        };
    }

    private static void addFooter(StringBuilder b) {
        b.append("</body>\n</html>\n");
    }

    private static void addHeader(StringBuilder b) {
        String host = "unknown";
        try {
            host = Inet4Address.getLocalHost().getHostName();
        } catch (Exception e) {
            LOG.log(Level.INFO, "Failed to lookup hostname.", e);
        }
        b.append("<html>\n<head><title>System Configuration</title></head>\n" +
                "<body>\n" +
                "<h1>System Configuration</h1>\n" +
                "<p>This view shows the system configuration of " + host + " at " + new Date() + ".</p>");

    }

    /**
     * Replaces new lines, returns, tabs and '"' with escaped variants.
     *
     * @param text the input text, not null
     * @return the escaped text.
     */
    private static String escape(String text) {
        return text.replace("\t", "\\t").replace("\"", "\\\"");
    }

    /**
     * Accesses an empty {@link Configuration}.
     * @return an empty {@link Configuration}, never null.
     */
    public static Configuration emptyConfiguration(){
        return EMPTY_CONFIGURATION;
    }

    /**
     * Accesses an empty {@link ConfigurationContext}.
     * @return an empty {@link ConfigurationContext}, never null.
     */
    public static ConfigurationContext emptyConfigurationContext(){
        return EMPTY_CONFIGURATION_CONTEXT;
    }

}
