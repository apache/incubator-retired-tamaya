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
package org.apache.tamaya.extras.json;

import org.apache.tamaya.ConfigException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

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

        Optional<String> keyA = source.get("a");
        Optional<String> keyB = source.get("b");
        Optional<String> keyC = source.get("c");

        assertThat(keyA.isPresent(), is(true));
        assertThat(keyA.get(), equalTo("A"));
        assertThat(keyB.isPresent(), is(true));
        assertThat(keyB.get(), is("B"));
        assertThat(keyC.isPresent(), is(true));
        assertThat(keyC.get(), is("C"));
    }

    @Test
    public void canReadNestedStringOnlyJSONConfigFile() throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/simple-nested-string-only-config-1.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getProperties().keySet(), hasSize(5));

        Optional<String> keyb = source.get("b");
        Optional<String> keyDO = source.get("d.o");
        Optional<String> keyDP = source.get("d.p");

        assertThat(keyb.isPresent(), is(true));
        assertThat(keyb.get(), equalTo("B"));
        assertThat(keyDO.isPresent(), is(true));
        assertThat(keyDO.get(), equalTo("O"));
        assertThat(keyDP.isPresent(), is(true));
        assertThat(keyDP.get(), is("P"));
    }

    @Test
    public void canReadNestedStringOnlyJSONConfigFileWithObjectInTheMiddle()
            throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/simple-nested-string-only-config-2.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getProperties().keySet(), hasSize(4));

        Optional<String> keyA = source.get("a");
        Optional<String> keyDO = source.get("b.o");
        Optional<String> keyDP = source.get("b.p");
        Optional<String> keyC = source.get("c");

        assertThat(keyA.isPresent(), is(true));
        assertThat(keyA.get(), is("A"));
        assertThat(keyC.isPresent(), is(true));
        assertThat(keyC.get(), equalTo("C"));
        assertThat(keyDO.isPresent(), is(true));
        assertThat(keyDO.get(), equalTo("O"));
        assertThat(keyDP.isPresent(), is(true));
        assertThat(keyDP.get(), is("P"));
    }
}
