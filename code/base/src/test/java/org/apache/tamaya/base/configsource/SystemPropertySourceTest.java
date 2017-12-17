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
package org.apache.tamaya.base.configsource;

import org.apache.tamaya.base.configsource.SystemConfigSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Properties;

import static javax.config.spi.ConfigSource.CONFIG_ORDINAL;

public class SystemPropertySourceTest {

    private final SystemConfigSource testPropertySource = new SystemConfigSource();


    @Test
    public void testGetOrdinal() throws Exception {

        // test the default ordinal
        Assert.assertEquals(SystemConfigSource.DEFAULT_ORDINAL, testPropertySource.getOrdinal());

        // set the ordinal to 1001
        System.setProperty(CONFIG_ORDINAL, "1001");
        Assert.assertEquals(1001, new SystemConfigSource().getOrdinal());
        // currently its not possible to change ordinal at runtime

        // reset it to not destroy other tests!!
        System.clearProperty(CONFIG_ORDINAL);
    }

    @Test
    public void testGetName() throws Exception {
        Assert.assertEquals("system-properties", testPropertySource.getName());
    }

    @Test
    public void testGet() throws Exception {
        String propertyKeyToCheck = System.getProperties().stringPropertyNames().iterator().next();

        String property = testPropertySource.getValue(propertyKeyToCheck);
        Assert.assertNotNull("Property '" + propertyKeyToCheck + "' is not present in " +
                SystemConfigSource.class.getSimpleName(), property);
        Assert.assertEquals(System.getProperty(propertyKeyToCheck), property);
    }

    @Test
    public void testGetProperties() throws Exception {
        checkWithSystemProperties(testPropertySource.getProperties());

        // modify system properties
        System.setProperty("test", "myTestVal");

        checkWithSystemProperties(testPropertySource.getProperties());

        // cleanup
        System.clearProperty("test");
    }

    private void checkWithSystemProperties(Map<String,String> toCheck) {
        Properties systemEntries = System.getProperties();
        int num = 0;
        for (Map.Entry<String,String> propertySourceEntry : toCheck.entrySet()) {
            if(propertySourceEntry.getKey().endsWith("[meta]")){
                continue; // meta entry
            }
            num ++;
            Assert.assertEquals("Entry values for key '" + propertySourceEntry.getKey() + "' do not match",
                                systemEntries.getProperty(propertySourceEntry.getKey()), propertySourceEntry.getValue());
        }
        Assert.assertEquals("size of System.getProperties().entrySet() must be the same as SystemPropertySrouce.getProperties().entrySet()",
                systemEntries.size(), num);
    }
}