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
package org.apache.tamaya.json;


import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.FlattenedDefaultPropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JSONFormatTest extends CommonJSONTestCaseCollection {
    private final JSONFormat format = new JSONFormat();

    @Test(expected = NullPointerException.class)
    public void acceptsNeedsNonNullParameter() throws Exception {
        format.accepts(null);
    }

    @Test
    public void aNonJSONFileBasedURLIsNotAccepted() throws Exception {
        URL url = new URL("file:///etc/service/conf.conf");

        assertThat(format.accepts(url), is(false));
    }

    @Test
    public void aJSONFileBasedURLIsAccepted() throws Exception {
        URL url = new URL("file:///etc/service/conf.json");

        assertThat(format.accepts(url), is(true));
    }

    @Test
    public void aHTTPBasedURLIsNotAccepted() throws Exception {
        URL url = new URL("http://nowhere.somewhere/conf.json");
        assertThat(format.accepts(url), is(true));
    }

    @Test
    public void aFTPBasedURLIsNotAccepted() throws Exception {
        URL url = new URL("ftp://nowhere.somewhere/a/b/c/d/conf.json");

        assertThat(format.accepts(url), is(true));
    }

    @Override
    PropertySource getPropertiesFrom(URL source) throws Exception {
        try (InputStream is = source.openStream()) {
            ConfigurationData data = format.readConfiguration(source.toString(), is);
            return new FlattenedDefaultPropertySource(data);
        }
    }
}