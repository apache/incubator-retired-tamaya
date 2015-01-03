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
package org.apache.tamaya.core.properties;

import org.apache.tamaya.PropertySource;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * Accessor that provides useful functions along with configuration.
 */
public final class PropertySourceFunctions {
    /**
     * Private singleton constructor.
     */
    private PropertySourceFunctions() {
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
    public static UnaryOperator<PropertySource> mapArea(String areaKey, String mappedAreaKey) {
        return mapKeys(key -> key.startsWith(areaKey + '.') ?
                mappedAreaKey + key.substring(areaKey.length()) : key);
    }

    /**
     * Creates a {@link java.util.function.UnaryOperator} that creates a {@link org.apache.tamaya.Configuration} that maps any keys as
     * defined by the {@code keyMapper} given. If the {@code keyMapper} returns
     * {@code null} for a keys, it is removed from the resulting map.
     *
     * @param keyMapper the key mapper, not null
     * @return the area configuration, with the areaKey stripped away.
     */
    public static UnaryOperator<PropertySource> mapKeys(UnaryOperator<String> keyMapper) {
        return (c) -> new MappedPropertySource(c, keyMapper);
    }



    /**
     * Intersetcs the current properties with the given {@link org.apache.tamaya.PropertySource} instance.
     *
     * @param providers the maps to be intersected, not null.
     * @return the builder for chaining.
     */
    public UnaryOperator<PropertySource> intersect(PropertySource... providers) {
        if (providers.length == 0) {
            return this;
        }
        String name = this.currentName;
        if (currentName == null) {
            name = "<intersection> -> " + Arrays.toString(providers);
        }
        return addPropertySources(PropertySourceFactory.intersected(name, aggregationPolicy, Arrays.asList(providers)));
    }


    /**
     * Filters the current properties based on the given predicate..
     *
     * @param filter the filter to be applied, not null.
     * @return the new filtering instance.
     */
    public UnaryOperator<PropertySource> filter(Predicate<String> filter) {
        String name = this.currentName;
        if (currentName == null) {
            name = "<filtered> -> " + filter;
        }
        current = PropertySourceFactory.filtered(name, filter, current);
        this.currentName = null;
        return this;
    }


    /**
     * Replaces all keys in the current provider by the given map.
     *
     * @param replacementMap the map instance, that will replace all corresponding entries in {@code mainMap}, not null.
     * @return the new delegating instance.
     */
    public PropertySourceBuilder replace(Map<String, String> replacementMap) {
        String name = this.currentName;
        if (currentName == null) {
            name = "<replacement> -> current=" + current.getName() + " with =" + replacementMap;
        }
        current = PropertySourceFactory.replacing(name, current, replacementMap);
        this.currentName = null;
        return this;
    }

}
