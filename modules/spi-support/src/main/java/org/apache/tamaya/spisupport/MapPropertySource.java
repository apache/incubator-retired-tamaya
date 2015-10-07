/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tamaya.spisupport;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Simple PropertySource implementation that just takes a Map and an (optional) priority.
 * Optionally the entries passed can be mapped to a different rootContext.
 */
public class MapPropertySource extends BasePropertySource {

    /** The unique name of the PropertySource. */
    private final String name;

    /**
     * The Property Sources priority, if a fixed priority should be used.
     */
    private final Integer priority;

    /**
     * The current properties.
     */
    private Map<String, String> props = new HashMap<>();

    /**
     * Creates a new instance, hereby using the default mechanism for evaluting the property source's
     * priority.
     *
     * @param props the properties
     */
    public MapPropertySource(String name, Map<String, String> props) {
        this(name, props, null, null);
    }

    /**
     * Creates a new instance, hereby using the default mechanism for evaluting the property source's
     * priority, but applying a custom mapping {@code rootContext} to the entries provided.
     *
     * @param props       the properties
     * @param rootContext the root context mapping, or null (for no mapping).
     */
    public MapPropertySource(String name, Map<String, String> props, String rootContext) {
        this(name, props, rootContext, null);
    }

    /**
     * Creates a new instance, hereby using the default mechanism for evaluting the property source's
     * priority, but applying a custom mapping {@code rootContext} to the entries provided.
     *
     * @param props       the properties
     * @param rootContext the root context mapping, or null (for no mapping).
     * @param priority    the (optional) fixed priority. If null, the default priority
     *                    evaluation is used.
     */
    public MapPropertySource(String name, Map<String, String> props, String rootContext, Integer priority) {
        this.priority = priority;
        this.name = Objects.requireNonNull(name);
        if (rootContext == null) {
            this.props.putAll(props);
        } else {
            for (Map.Entry<String, String> en : props.entrySet()) {
                String prefix = rootContext;
                if (prefix == null) {
                    prefix = "";
                }
                if (!prefix.endsWith(".") && prefix.length() > 0) {
                    prefix += ".";
                }
                this.props.put(prefix + en.getKey(), en.getValue());
            }
        }
    }

    /**
     * Creates a new instance, hereby using the default mechanism for evaluting the property source's
     * priority, but applying a custom mapping {@code rootContext} to the entries provided.
     *
     * @param props       the properties
     * @param rootContext the root context mapping, or null (for no mapping).
     * @param priority    the (optional) fixed priority. If null, the default priority
     *                    evaluation is used.
     */
    public MapPropertySource(String name, Properties props, String rootContext, Integer priority) {
        this(name, getMap(props), rootContext, priority);
    }

    /**
     * Simple method to convert a Properties to a Map instance.
     * @param props the properties, not null.
     * @return the corresponding Map instance.
     */
    private static Map<String, String> getMap(Properties props) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry en : props.entrySet()) {
            result.put(en.getKey().toString(), en.getValue().toString());
        }
        return result;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.props);
    }

    @Override
    public int getOrdinal() {
        return priority;
    }

    @Override
    public String toString() {
        return "SimplePropertiesPropertySource{" +
                "name=" + name + ", " +
                "priority=" + priority +
                '}';
    }

}
