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
package org.apache.tamaya.base.configsource;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Simple PropertySource implementation that just takes a Map and an (optional) priority.
 * Optionally the entries passed can be mapped to a different rootContext.
 */
public class MapConfigSource extends BaseConfigSource {

    /**
     * The current properties.
     */
    private final Map<String, String> props = new HashMap<>();

    /**
     * Creates a new instance, hereby using the default mechanism for evaluating the property source's
     * priority.
     *
     * @param name unique name of this source.
     * @param props the properties
     */
    public MapConfigSource(String name, Map<String, String> props) {
        this(name, props, null);
    }

    /**
     * Creates a new instance, hereby using the default mechanism for evaluating the property source's
     * priority, but applying a custom mapping {@code prefix} to the entries provided.
     *
     * @param name        unique name of this source.
     * @param props       the properties
     * @param prefix      the prefix context mapping, or null (for no mapping).
     */
    public MapConfigSource(String name, Map<String, String> props, String prefix) {
        super(name);
        if (prefix == null) {
            for (Map.Entry<String, String> en : props.entrySet()) {
                this.props.put(en.getKey(), en.getValue());
            }
        } else {
            for (Map.Entry<String, String> en : props.entrySet()) {
                this.props.put(prefix + en.getKey(), en.getValue());
            }
        }
    }

    /**
     * Creates a new instance, hereby using the default mechanism for evaluating the property source's
     * priority, but applying a custom mapping {@code rootContext} to the entries provided.
     *
     * @param name unique name of this source.
     * @param props       the properties
     * @param prefix      the prefix context mapping, or null (for no mapping).
     */
    public MapConfigSource(String name, Properties props, String prefix) {
        this(name, getMap(props), prefix);
    }

    /**
     * Simple method to convert Properties into a Map instance.
     * @param props the properties, not null.
     * @return the corresponding Map instance.
     */
    public static Map<String, String> getMap(Properties props) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry en : props.entrySet()) {
            result.put(en.getKey().toString(), en.getValue().toString());
        }
        return result;
    }


    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.props);
    }

}
