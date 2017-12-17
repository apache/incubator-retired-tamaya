/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.base.configsource;

import org.apache.tamaya.base.configsource.MapConfigSource;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class MapPropertySourceTest {

    private Map<String,String> sourceMap;
    private Properties sourceProperties;

    @Before
    public void createMapAndProperties() throws Exception {
        sourceMap = new HashMap<>();
        sourceMap.put("a", "AAA");
        sourceMap.put("b", "BBB");

        sourceProperties = new Properties();
        sourceProperties.setProperty("a", "AAA");
        sourceProperties.setProperty("b", "BBB");
    }

    @Test
    public void sourceWillProperlyInitializedWithMapWithoutPrefix() throws Exception {
        MapConfigSource propertySource = new MapConfigSource("UT", sourceMap);

        assertThat(propertySource.getProperties()).describedAs("Should contain exactly 2 properties.")
                                                  .hasSize(2);
        assertThat(propertySource.getValue("a")).isNotNull();
        assertThat(propertySource.getValue("b")).isNotNull();
    }

    @Test
    public void sourceWillProperlyInitializedWithMapWithPrefix() throws Exception {
        MapConfigSource propertySource = new MapConfigSource("UT", sourceMap, "pre-");

        assertThat(propertySource.getProperties()).describedAs("Should contain exactly 2 properties.")
                                                  .hasSize(2);
        assertThat(propertySource.getValue("pre-a")).isNotNull();
        assertThat(propertySource.getValue("pre-b")).isNotNull();
    }

    @Test
    public void sourceWillProperlyInitializedWithPropertiesWithPrefix() throws Exception {
        MapConfigSource propertySource = new MapConfigSource("UT", sourceProperties, "pre-");

        assertThat(propertySource.getProperties()).describedAs("Should contain exactly 2 properties.")
                                                  .hasSize(2);
        assertThat(propertySource.getValue("pre-a")).isNotNull();
        assertThat(propertySource.getValue("pre-b")).isNotNull();
    }
}