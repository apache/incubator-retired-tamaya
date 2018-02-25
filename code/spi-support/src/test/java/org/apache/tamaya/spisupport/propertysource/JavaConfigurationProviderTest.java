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
package org.apache.tamaya.spisupport.propertysource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.apache.tamaya.spi.PropertySource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JavaConfigurationProviderTest {

    private static final String A_UMLAUT = "\u00E4";
    private static final String O_UMLAUT = "\u00F6";

    @Test
    public void loadsSimpleAndXMLPropertyFilesProper() {
        PropertySource propertySource = new JavaConfigurationPropertySource();
        MatcherAssert.assertThat(propertySource.getProperties().keySet(), Matchers.hasSize(7));  // double the size for .source values.

        for (int i = 1; i < 6; i++) {
            String key = "confkey" + i;
            String value = "javaconf-value" + i;

            MatcherAssert.assertThat(value, Matchers.equalTo(propertySource.get(key).getValue()));
        }

    }
    
    @Test
    public void testConstructionPropertiesAndDisabledBehavior() throws IOException {
        JavaConfigurationPropertySource localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
        StringWriter stringBufferWriter = new StringWriter();
        System.getProperties().store(stringBufferWriter, null);
        String before = stringBufferWriter.toString();

        try {
            assertTrue(localJavaConfigurationPropertySource.isEnabled());

            System.setProperty("tamaya.defaultprops.disable", "true");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertFalse(localJavaConfigurationPropertySource.isEnabled());
            assertTrue(localJavaConfigurationPropertySource.getProperties().isEmpty());
            assertTrue(localJavaConfigurationPropertySource.toString().contains("enabled=false"));

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "true");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertFalse(localJavaConfigurationPropertySource.isEnabled());

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaultprops.disable", "");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertTrue(localJavaConfigurationPropertySource.isEnabled());

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertTrue(localJavaConfigurationPropertySource.isEnabled());

        } finally {
            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
        }
    }
}
