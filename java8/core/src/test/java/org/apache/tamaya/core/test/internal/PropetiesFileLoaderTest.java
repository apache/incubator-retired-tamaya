/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.test.internal;

import org.apache.tamaya.core.internal.PropertiesFileLoader;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Properties;
import java.util.Set;

public class PropetiesFileLoaderTest {


    @Test
    public void testResolvePropertiesFiles() throws Exception {
        Properties expectedProperties = PropertiesFileLoader.load(Thread.currentThread().getContextClassLoader().getResource("testfile.properties"));

        {
            // with .properties
            Set<URL> urls = PropertiesFileLoader.resolvePropertiesFiles("testfile.properties");
            Assert.assertNotNull(urls);
            Assert.assertFalse(urls.isEmpty());

            Properties properties = PropertiesFileLoader.load(urls.iterator().next());
            Assert.assertEquals(expectedProperties.size(), properties.size());
        }

        {
            // without .properties
            Set<URL> urls = PropertiesFileLoader.resolvePropertiesFiles("testfile");
            Assert.assertNotNull(urls);
            Assert.assertFalse(urls.isEmpty());

            Properties properties = PropertiesFileLoader.load(urls.iterator().next());
            Assert.assertEquals(expectedProperties.size(), properties.size());
        }

        {
            // with aa_a while which doesn't exist
            Set<URL> urls = PropertiesFileLoader.resolvePropertiesFiles("nonexistingfile.properties");
            Assert.assertNotNull(urls);
            Assert.assertTrue(urls.isEmpty());
        }

    }

    @Test
    public void testLoad() {
        Properties properties = PropertiesFileLoader.load(Thread.currentThread().getContextClassLoader().getResource("testfile.properties"));

        Assert.assertNotNull(properties);
        Assert.assertEquals(5, properties.size());

        for (int i = 1; i < 6; i++) {
            Assert.assertEquals(properties.getProperty("key" + i), "val" + i);
        }
    }
}
