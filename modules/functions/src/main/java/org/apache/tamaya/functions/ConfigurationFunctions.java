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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Accessor that provides useful functions along with configuration.
 */
public final class ConfigurationFunctions {

    private static final ConfigQuery<String> INFO_QUERY = new ConfigQuery<String>(){
        @Override
        public String query(Configuration config) {
            StringBuilder builder = new StringBuilder();
            Map<String,String> props = new TreeMap<>(config.getProperties());
            builder.append("Configuration: {\n")
                    .append("  \"class\": \n"+ config.getClass().getName() + ",\n")
                    .append("  \"properties\": {\n");
            for(Map.Entry<String,String> en: props.entrySet()){
                builder.append("     \"" + escape(en.getKey()) +"\": \""+escape(en.getValue())+"\",\n");
            }
            builder.append("    }\n}");
            return builder.toString();
        }
    };


    /**
     * Replaces new lines, returns, tabs and '"' with escaped variants.
     * @param text the input text, not null
     * @return the escaped text.
     */
    private static String escape(String text){
        return text.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t").replace("\"", "\\\"");
    }

    /**
     * Private singleton constructor.
     */
    private ConfigurationFunctions() {
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are selected by the given {@link BiPredicate}.
     *
     * @param filter the filter, not null
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator filter(final BiPredicate<String, String> filter) {
        return new ConfigOperator() {
            @Override
            public Configuration operate(Configuration config) {
                return new FilteredConfiguration(config, filter, null);
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
    public static ConfigOperator map(final Function<String, String> keyMapper) {
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
                        new BiPredicate<String, String>() {
                            @Override
                            public boolean test(String k, String v) {
                                return isKeyInSection(k, areaKey);
                            }
                        }, "section: " + areaKey);
                if(stripKeys){
                    return new MappedConfiguration(filtered, new Function<String, String>() {
                        @Override
                        public String apply(String k) {
                            return k.substring(areaKey.length() + 1);
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
     * @param key     the fully qualified entry key, not null
     * @param sectionKey the section key, not null
     * @return true, if the entry is exact in this section
     */
    public static boolean isKeyInSection(String key, String sectionKey) {
        int lastIndex = key.lastIndexOf('.');
        String curAreaKey = lastIndex > 0 ? key.substring(0, lastIndex) : "";
        return curAreaKey.equals(sectionKey);
    }

    /**
     * Calculates the current section key and compares it with the given section keys.
     *
     * @param key     the fully qualified entry key, not null
     * @param sectionKeys the section keys, not null
     * @return true, if the entry is exact in this section
     */
    public static boolean isKeyInSections(String key, String... sectionKeys) {
        for(String areaKey:sectionKeys){
            if(isKeyInSection(key, areaKey)){
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
                final Set<String> areas = new HashSet<>();
                for(String s: config.getProperties().keySet()) {
                    int index = s.lastIndexOf('.');
                    if (index > 0) {
                        areas.add(s.substring(0, index));
                    } else {
                        areas.add("<root>");
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
                final Set<String> transitiveAreas = new HashSet<>();
                for (String s : config.query(sections())) {
                    int index = s.lastIndexOf('.');
                    if (index < 0) {
                        transitiveAreas.add("<root>");
                    } else {
                        while (index > 0) {
                            s = s.substring(0, index);
                            transitiveAreas.add(s);
                            index = s.lastIndexOf('.');
                        }
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
        return new ConfigQuery<Set<String>>(){
            @Override
            public Set<String> query(Configuration config) {
                Set<String> result = new TreeSet<>();
                for(String s: sections().query(config)){
                    if(predicate.test(s)){
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
        return new ConfigQuery<Set<String>>(){
            @Override
            public Set<String> query(Configuration config) {
                Set<String> result = new TreeSet<>();
                for(String s: transitiveSections().query(config)){
                    if(predicate.test(s)){
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
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given section (recursive).
     *
     * @param sectionKeys   the section keys, not null
     * @param stripKeys if set to true, the section key is stripped away fromMap the resulting key.
     * @return the section configuration, with the areaKey stripped away.
     */
    public static ConfigOperator sectionRecursive(final boolean stripKeys, final String... sectionKeys) {
        return new ConfigOperator(){
            @Override
            public Configuration operate(Configuration config) {
                Configuration filtered = new FilteredConfiguration(config, new BiPredicate<String, String>() {
                    @Override
                    public boolean test(final String k, String v) {
                        return isKeyInSections(k, sectionKeys);
                    }
                } , "sections: " + Arrays.toString(sectionKeys));
                if(stripKeys){
                    return new MappedConfiguration(filtered, new Function<String, String>() {
                        @Override
                        public String apply(String s) {
                            return PropertySourceFunctions.stripSectionKeys(s, sectionKeys);
                        }
                    }, "stripped");
                }
                return filtered;
            }
        };
    }

    /**
     * Creates a ConfigQuery that creates a JSON formatted ouitput of all properties in the given configuration.
     * @return the given query.
     */
    public static ConfigQuery<String> jsonInfo() {
        return INFO_QUERY;
    }

}
