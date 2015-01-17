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
package org.apache.tamaya.core.internal;

import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

public class PropetiesFileLoaderTest {


    @Test
    public void testResolvePropertiesFiles() throws Exception {
        Properties expectedProperties = PropertiesFileLoader.load(Thread.currentThread().getContextClassLoader().getResource("testfile.properties"));

        {
            // with .properties
            Enumeration<URL> urls = PropertiesFileLoader.resolvePropertiesFiles("testfile.properties");
            Assert.assertNotNull(urls);
            Assert.assertTrue(urls.hasMoreElements());

            Properties properties = PropertiesFileLoader.load(urls.nextElement());
            Assert.assertEquals(expectedProperties.size(), properties.size());

            Assert.assertFalse(urls.hasMoreElements());
        }

        {
            // without .properties
            Enumeration<URL> urls = PropertiesFileLoader.resolvePropertiesFiles("testfile");
            Assert.assertNotNull(urls);
            Assert.assertTrue(urls.hasMoreElements());

            Properties properties = PropertiesFileLoader.load(urls.nextElement());
            Assert.assertEquals(expectedProperties.size(), properties.size());

            Assert.assertFalse(urls.hasMoreElements());
        }

        {
            // with a while which doesn't exist
            Enumeration<URL> urls = PropertiesFileLoader.resolvePropertiesFiles("nonexistingfile.properties");
            Assert.assertNotNull(urls);
            Assert.assertFalse(urls.hasMoreElements());
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
