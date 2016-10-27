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
package org.apache.tamaya.core.propertysource;

import org.apache.tamaya.ConfigException;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.hasEntry;

public class SimplePropertySourceTest {
    @Test
    public void successfulCreationWithPropertiesFromXMLPropertiesFile() {
        URL resource = getClass().getResource("/valid-properties.xml");

        SimplePropertySource source = new SimplePropertySource(resource);

        assertThat(source, notNullValue());
        assertThat(source.getProperties(), aMapWithSize(2)); // double the size for .source values.
        assertThat(source.getProperties(), hasEntry("a", "b"));
        assertThat(source.getProperties(), hasEntry("b", "1"));

    }

    @Test
    public void failsToCreateFromNonXMLPropertiesXMLFile() {
        URL resource = getClass().getResource("/non-xml-properties.xml");
        ConfigException catchedException = null;

        try {
            new SimplePropertySource(resource);
        } catch (ConfigException ce) {
            catchedException = ce;
        }

        assertThat(catchedException.getMessage(), allOf(startsWith("Error loading properties from"),
                                                        endsWith("non-xml-properties.xml")));
    }

    @Test
    public void failsToCreateFromInvalidPropertiesXMLFile() {
        URL resource = getClass().getResource("/invalid-properties.xml");
        ConfigException catchedException = null;

        try {
            new SimplePropertySource(resource);
        } catch (ConfigException ce) {
            catchedException = ce;
        }

        assertThat(catchedException.getMessage(), allOf(startsWith("Error loading properties from"),
                                                        endsWith("invalid-properties.xml")));
    }


    @Test
    public void successfulCreationWithPropertiesFromSimplePropertiesFile() {
        URL resource = getClass().getResource("/testfile.properties");

        SimplePropertySource source = new SimplePropertySource(resource);

        assertThat(source, notNullValue());
        assertThat(source.getProperties(), aMapWithSize(5)); // double the size for .source values.
    }
}
