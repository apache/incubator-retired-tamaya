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
package org.apache.tamaya.modules.json;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.propertysource.DefaultOrdinal;
import org.apache.tamaya.spi.PropertySource;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

public class JSONPropertySourceTest {

    @Test(expected = ConfigException.class)
    public void emptyJSONFileResultsInConfigException() throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/empty-file.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        source.getProperties();
    }

    @Test
    public void canHandleEmptyJSONObject() throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/empty-object-config.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getProperties().keySet(), hasSize(0));
    }

    @Test
    public void canReadFlatStringOnlyJSONConfigFile() throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/simple-flat-string-only-config.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getProperties().keySet(), hasSize(3));

        String keyA = source.get("a");
        String keyB = source.get("b");
        String keyC = source.get("c");

        assertThat(keyA, notNullValue());
        assertThat(keyA, equalTo("A"));
        assertThat(keyB, notNullValue());
        assertThat(keyB, is("B"));
        assertThat(keyC, notNullValue());
        assertThat(keyC, is("C"));
    }

    @Test
    public void canReadNestedStringOnlyJSONConfigFile() throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/simple-nested-string-only-config-1.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getProperties().keySet(), hasSize(5));

        String keyb = source.get("b");
        String keyDO = source.get("d.o");
        String keyDP = source.get("d.p");

        assertThat(keyb, notNullValue());
        assertThat(keyb, equalTo("B"));
        assertThat(keyDO, notNullValue());
        assertThat(keyDO, equalTo("O"));
        assertThat(keyDP, Matchers.notNullValue());
        assertThat(keyDP, is("P"));
    }

    @Test
    public void canReadNestedStringOnlyJSONConfigFileWithObjectInTheMiddle()
            throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/simple-nested-string-only-config-2.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getProperties().keySet(), hasSize(4));

        String keyA = source.get("a");
        String keyDO = source.get("b.o");
        String keyDP = source.get("b.p");
        String keyC = source.get("c");

        assertThat(keyA, notNullValue());
        assertThat(keyA, is("A"));
        assertThat(keyC, notNullValue());
        assertThat(keyC, equalTo("C"));
        assertThat(keyDO, notNullValue());
        assertThat(keyDO, equalTo("O"));
        assertThat(keyDP, notNullValue());
        assertThat(keyDP, is("P"));
    }

    @Test
    public void tamayaOrdinalKeywordIsNotPropagatedAsNormalProperty() throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/with-explicit-priority.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.get(PropertySource.TAMAYA_ORDINAL).isPresent(), is(false));
    }

    @Test
    public void priorityInConfigFileOverwriteExplicitlyGivenPriority() throws URISyntaxException {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/with-explicit-priority.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getOrdinal(), is(16784));
    }

    @Test
    public void priorityInConfigFileIsReturnedPriority() throws URISyntaxException {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/with-explicit-priority.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile);

        assertThat(source.getOrdinal(), is(16784));

    }
}
