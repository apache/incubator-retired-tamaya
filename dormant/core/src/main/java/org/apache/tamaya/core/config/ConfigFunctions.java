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

import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.properties.PropertySourceBuilder;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Accessor that provides useful functions along with configuration.
 */
public final class ConfigFunctions {
    /**
     * Private singleton constructor.
     */
    private ConfigFunctions() {
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (non recursive). Hereby
     * the area key is stripped away fromMap the resulting key.
     *
     * @param areaKey the area key, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> selectArea(String areaKey) {
        return selectArea(areaKey, true);
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (non recursive).
     *
     * @param areaKey   the area key, not null
     * @param stripKeys if set to true, the area key is stripped away fromMap the resulting key.
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> selectArea(String areaKey, boolean stripKeys) {
        return config -> {
            Map<String, String> area = new HashMap<>();
            area.putAll(
                    config.getProperties().entrySet().stream()
                            .filter(e -> isKeyInArea(e.getKey(), areaKey))
                            .collect(Collectors.toMap(
                                    e -> stripKeys ? e.getKey().substring(areaKey.length() + 1) : e.getKey(),
                                    Map.Entry::getValue)));
            return PropertySourceBuilder.of("area: " + areaKey).addMap(area).build().toConfiguration();
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
     * Return a query to evaluate the set with all fully qualifies area names. This method should return the areas as accurate as possible,
     * but may not provide a complete set of areas that are finally accessible, especially when the underlying storage
     * does not support key iteration.
     *
     * @return s set with all areas, never {@code null}.
     */
    public static ConfigQuery<Set<String>> getAreas() {
        return config -> {
            final Set<String> areas = new HashSet<>();
            config.getProperties().keySet().forEach(s -> {
                int index = s.lastIndexOf('.');
                if (index > 0) {
                    areas.add(s.substring(0, index));
                } else {
                    areas.add("<root>");
                }
            });
            return areas;
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
    public static ConfigQuery<Set<String>> getTransitiveAreas() {
        return config -> {
            final Set<String> transitiveAreas = new HashSet<>();
            config.query(getAreas()).forEach(s -> {
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
            });
            return transitiveAreas;
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
    public static ConfigQuery<Set<String>> getAreas(final Predicate<String> predicate) {
        return config -> {
            return config.query(getAreas()).stream().filter(predicate).collect(Collectors.toCollection(TreeSet::new));
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
    public static ConfigQuery<Set<String>> getTransitiveAreas(Predicate<String> predicate) {
        return config -> {
            return config.query(getTransitiveAreas()).stream().filter(predicate).collect(Collectors.toCollection(TreeSet::new));
        };
    }

    /**
     * Return a query to evaluate to evaluate if an area exists. In case where the underlying storage implementation does not allow
     * querying the keys available, {@code false} should be returned.
     *
     * @param areaKey the configuration area (sub)path.
     * @return {@code true}, if such a node exists.
     */
    public static ConfigQuery<Boolean> containsArea(String areaKey) {
        return config -> {
            return config.query(getAreas()).contains(areaKey);
        };
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (recursive). Hereby
     * the area key is stripped away fromMap the resulting key.
     *
     * @param areaKey the area key, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> selectAreaRecursive(String areaKey) {
        return selectAreaRecursive(areaKey, true);
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (recursive).
     *
     * @param areaKey   the area key, not null
     * @param stripKeys if set to true, the area key is stripped away fromMap the resulting key.
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> selectAreaRecursive(String areaKey, boolean stripKeys) {
        return config -> {
            Map<String, String> area = new HashMap<>();
            String lookupKey = areaKey + '.';
            area.putAll(
                    config.getProperties().entrySet().stream()
                            .filter(e -> e.getKey().startsWith(lookupKey))
                            .collect(Collectors.toMap(
                                    e -> stripKeys ? e.getKey().substring(areaKey.length() + 1) : e.getKey(),
                                    Map.Entry::getValue)));
            return PropertySourceBuilder.of("area (recursive): " + areaKey).addMap(area).build().toConfiguration();
        };
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (non recursive). Hereby
     * the area key is stripped away fromMap the resulting key.
     *
     * @param areaKey       the area key, not null
     * @param mappedAreaKey the target key, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> mapArea(String areaKey, String mappedAreaKey) {
        return mapKeys(key -> key.startsWith(areaKey + '.') ?
                mappedAreaKey + key.substring(areaKey.length()) : key);
    }

    /**
     * Creates a {@link UnaryOperator} that creates a {@link org.apache.tamaya.Configuration} that maps any keys as
     * defined by the {@code keyMapper} given. If the {@code keyMapper} returns
     * {@code null} for a keys, it is removed from the resulting map.
     *
     * @param keyMapper the key mapper, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> mapKeys(UnaryOperator<String> keyMapper) {
        return (c) -> new MappedConfiguration(c, keyMapper);
    }

}
