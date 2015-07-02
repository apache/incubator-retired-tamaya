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
package org.apache.tamaya.format;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link org.apache.tamaya.format.ConfigurationFormats}.
 */
public class ConfigurationFormatsTest {

    @org.junit.Test
    public void testGetFormats() throws Exception {
        List<ConfigurationFormat> formats = ConfigurationFormats.getFormats();
        assertNotNull(formats);
        assertEquals(formats.size(), 3);
    }

    @org.junit.Test
    public void testReadConfigurationData() throws Exception {
        List<ConfigurationFormat> formats = ConfigurationFormats.getFormats(getClass().getResource("/Test.ini"));
        assertNotNull(formats);
        assertEquals(formats.size(), 1);
        formats = ConfigurationFormats.getFormats(getClass().getResource("/Test.properties"));
        assertNotNull(formats);
        assertEquals(formats.size(), 1);
//        formats = ConfigurationFormats.getFormats(getClass().getResource("/Test.xml"));
//        assertNotNull(formats);
//        assertEquals(formats.size(), 1);

    }

    @org.junit.Test
    public void testReadConfigurationData_URL() throws Exception {
        ConfigurationData data = ConfigurationFormats.readConfigurationData(getClass().getResource("/Test.ini"));
        assertNotNull(data);
        data = ConfigurationFormats.readConfigurationData(getClass().getResource("/Test.properties"));
        assertNotNull(data);
    }

    @org.junit.Test
    public void testReadConfigurationData2() throws Exception {
        List<ConfigurationFormat> formats = ConfigurationFormats.getFormats();
        ConfigurationData data = ConfigurationFormats.readConfigurationData(getClass().getResource("/Test.ini"),
                formats.toArray(new ConfigurationFormat[formats.size()]));
        assertNotNull(data);
        System.out.println(data);
    }
}