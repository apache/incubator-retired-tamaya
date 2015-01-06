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

import org.apache.tamaya.spi.ConfigurationContext;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class JSONPropertySourceConfigurationTest {


    @Test
    public void testFull() throws Exception {
        URL configURL = JSONPropertySourceTest.class.getResource("/configs/valid/simple-flat-string-only-config.json");

        assertThat(configURL, CoreMatchers.notNullValue());

        File configFile = new File(configURL.toURI());

        JSONPropertySource source = new JSONPropertySource(configFile, 10);

        assertThat(source.getProperties().keySet(), hasSize(3));

        ConfigurationContext context = ConfigurationContext.context();

        context.addPropertySources(source);

        // @todo Finish!
    }
}
