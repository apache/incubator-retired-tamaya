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
package org.apache.tamaya.events;

import org.apache.commons.io.FileUtils;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;

/**
 * Test (currently manual) to test configuration changes.
 */
public class ObservedConfigTest {

    @Test
    public void testChangingConfig() throws IOException {
        Configuration config = ConfigurationProvider.getConfiguration().with(TestConfigView.of());

        Map<String, String> props = config.getProperties();
        assertEquals(props.get("test"), "test2");
        assertEquals(props.get("testValue1"), "value");
        assertNull(props.get("testValue2"));

        //insert a new properties file into the tempdirectory
        FileUtils.writeStringToFile(
                new File(TestObservingProvider.propertyLocation.toFile(), "test2.properties"),
                "testValue2=anotherValue");

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        config = ConfigurationProvider.getConfiguration().with(TestConfigView.of());

        props = config.getProperties();

        assertEquals(props.get("test"), "test2");
        assertEquals(props.get("testValue1"), "value");
        assertEquals(props.get("testValue2"), "anotherValue");
    }

}
