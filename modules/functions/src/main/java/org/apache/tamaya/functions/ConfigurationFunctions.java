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
     * that are contained in the given area (non recursive). Hereby
     * the area key is stripped away fromMap the resulting key.
     *
     * @param filter the filter, not null
     * @return the area configuration, with the areaKey stripped away.
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
     * @return the area configuration, with the areaKey stripped away.
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
     * that are contained in the given area (non recursive). Hereby
     * the area key is stripped away fromMap the resulting key.
     *
     * @param areaKey the area key, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static ConfigOperator area(String areaKey) {
        return area(areaKey, true);
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (non recursive).
     *
     * @param areaKey   the area key, not null
     * @param stripKeys if set to true, the area key is stripped away fromMap the resulting key.
     * @return the area configuration, with the areaKey stripped away.
     */
    public static ConfigOperator area(final String areaKey, final boolean stripKeys) {
        return new ConfigOperator() {
            @Override
            public Configuration operate(Configuration config) {
                Configuration filtered = new FilteredConfiguration(config,
                        new BiPredicate<String, String>() {
                            @Override
                            public boolean test(String k, String v) {
                                return isKeyInArea(k, areaKey);
                            }
                        }, "area: " + areaKey);
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
     * Calculates the current area key and compares it with the given key.
     *
     * @param key     the fully qualified entry key, not null
     * @param areaKey the area key, not null
     * @return true, if the entry is exact in this area
     */
    public static boolean isKeyInArea(String key, String areaKey) {
        int lastIndex = key.lastIndexOf('.');
        String curAreaKey = lastIndex > 0 ? key.substring(0, lastIndex) : "";
        return curAreaKey.equals(areaKey);
    }

    /**
     * Calculates the current area key and compares it with the given area keys.
     *
     * @param key     the fully qualified entry key, not null
     * @param areaKeys the area keys, not null
     * @return true, if the entry is exact in this area
     */
    public static boolean isKeyInAreas(String key, String... areaKeys) {
        for(String areaKey:areaKeys){
            if(isKeyInArea(key, areaKey)){
                return true;
            }
        }
        return false;
    }

    /**
     * Return a query to evaluate the set with all fully qualifies area names. This method should return the areas as accurate as possible,
     * but may not provide a complete set of areas that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @return s set with all areas, never {@code null}.
     */
    public static ConfigQuery<Set<String>> areas() {
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
     * Return a query to evaluate the set with all fully qualified area names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not. This method should return the areas as accurate
     * as possible, but may not provide a complete set of areas that are finally accessible, especially when the
     * underlying storage does not support key iteration.
     *
     * @return s set with all transitive areas, never {@code null}.
     */
    public static ConfigQuery<Set<String>> transitiveAreas() {
        return new ConfigQuery<Set<String>>() {
            @Override
            public Set<String> query(Configuration config) {
                final Set<String> transitiveAreas = new HashSet<>();
                for (String s : config.query(areas())) {
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
     * Return a query to evaluate the set with all fully qualified area names, containing only the
     * areas that match the predicate and have properties attached. This method should return the areas as accurate as possible,
     * but may not provide a complete set of areas that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @param predicate A predicate to deternine, which areas should be returned, not {@code null}.
     * @return s set with all areas, never {@code null}.
     */
    public static ConfigQuery<Set<String>> areas(final Predicate<String> predicate) {
        return new ConfigQuery<Set<String>>(){
            @Override
            public Set<String> query(Configuration config) {
                Set<String> result = new TreeSet<>();
                for(String s: areas().query(config)){
                    if(predicate.test(s)){
                        result.add(s);
                    }
                }
                return result;
            }
        };

    }

    /**
     * Return a query to evaluate the set with all fully qualified area names, containing the transitive closure also including all
     * subarea names, regardless if properties are accessible or not. This method should return the areas as accurate as possible,
     * but may not provide a complete set of areas that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @param predicate A predicate to deternine, which areas should be returned, not {@code null}.
     * @return s set with all transitive areas, never {@code null}.
     */
    public static ConfigQuery<Set<String>> transitiveAreas(final Predicate<String> predicate) {
        return new ConfigQuery<Set<String>>(){
            @Override
            public Set<String> query(Configuration config) {
                Set<String> result = new TreeSet<>();
                for(String s: transitiveAreas().query(config)){
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
     * that are contained in the given area (recursive). Hereby
     * the area key is stripped away fromMap the resulting key.
     *
     * @param areaKeys the area keys, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static ConfigOperator areasRecursive(String... areaKeys) {
        return areaRecursive(true, areaKeys);
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (recursive).
     *
     * @param areaKeys   the area keys, not null
     * @param stripKeys if set to true, the area key is stripped away fromMap the resulting key.
     * @return the area configuration, with the areaKey stripped away.
     */
    public static ConfigOperator areaRecursive(final boolean stripKeys, final String... areaKeys) {
        return new ConfigOperator(){
            @Override
            public Configuration operate(Configuration config) {
                Configuration filtered = new FilteredConfiguration(config, new BiPredicate<String, String>() {
                    @Override
                    public boolean test(final String k, String v) {
                        return isKeyInAreas(k, areaKeys);
                    }
                } , "areas: " + Arrays.toString(areaKeys));
                if(stripKeys){
                    return new MappedConfiguration(filtered, new Function<String, String>() {
                        @Override
                        public String apply(String s) {
                            return PropertySourceFunctions.stripAreaKeys(s, areaKeys);
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
    public static ConfigQuery<String> info() {
        return INFO_QUERY;
    }

}
