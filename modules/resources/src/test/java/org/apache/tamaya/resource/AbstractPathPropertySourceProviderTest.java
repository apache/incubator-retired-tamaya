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
package org.apache.tamaya.resource;

import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractPathPropertySourceProviderTest {

    private AbstractPathPropertySourceProvider myProvider = new AbstractPathPropertySourceProvider("*.properties") {
        @Override
        protected Collection<PropertySource> getPropertySources(URL url) {
            List<PropertySource> result = new ArrayList<>();
            result.add(new PropertySource() {
                @Override
                public String getName() {
                    return "<empty>";
                }

                @Override
                public Map<String, String> getProperties() {
                    return Collections.emptyMap();
                }
            });
            return result;
        }
    };

    @Test
    public void testGetPropertySources() throws Exception {
        assertNotNull(myProvider.getPropertySources());
    }

    @Test
    public void testCreatePropertiesPropertySource() throws Exception {
        PropertySource ps = AbstractPathPropertySourceProvider.createPropertiesPropertySource(
                ClassLoader.getSystemClassLoader().getResource("test.properties")
        );
        assertNotNull(ps);
        assertTrue(ps.getProperties().isEmpty());
    }
}