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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.properties.PropertySourceBuilder;

import java.util.*;
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
                    config.toMap().entrySet().stream()
                            .filter(e -> isKeyInArea(e.getKey(), areaKey))
                            .collect(Collectors.toMap(
                                    e -> stripKeys ? e.getKey().substring(areaKey.length() + 1) : e.getKey(),
                                    e -> e.getValue())));
            return PropertySourceBuilder.create("area: " + areaKey).addMap(area).build().toConfiguration();
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
                    config.toMap().entrySet().stream()
                            .filter(e -> e.getKey().startsWith(lookupKey))
                            .collect(Collectors.toMap(
                                    e -> stripKeys ? e.getKey().substring(areaKey.length() + 1) : e.getKey(),
                                    e -> e.getValue())));
            return PropertySourceBuilder.create("area (recursive): " + areaKey).addMap(area).build().toConfiguration();
        };
    }

    /**
     * Creates a ConfigOperator that creates a Configuration containing only keys
     * that are contained in the given area (non recursive). Hereby
     * the area key is stripped away fromMap the resulting key.
     *
     * @param areaKey the area key, not null
     * @param mappedAreaKey the target key, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> mapArea(String areaKey, String mappedAreaKey) {
        return mapKeys(key -> key.startsWith(areaKey + '.')?
                mappedAreaKey + key.substring(areaKey.length()):key);
    }

    /**
     * Creates a {@link UnaryOperator} that creates a {@link org.apache.tamaya.Configuration} that maps any keys as
     * defined by the {@code keyMapper} given. If the {@code keyMapper} returns
     * {@code null} for a value, it is removed from the resulting map.
     *
     * @param keyMapper the key mapper, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<Configuration> mapKeys(UnaryOperator<String> keyMapper) {
        return (c) -> new MappedConfiguration(c, keyMapper);
    }

}
