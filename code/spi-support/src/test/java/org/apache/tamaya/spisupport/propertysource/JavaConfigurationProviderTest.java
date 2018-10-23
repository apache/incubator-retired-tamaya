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
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;


public class JavaConfigurationProviderTest {

    private static final String A_UMLAUT = "\u00E4";
    private static final String O_UMLAUT = "\u00F6";

    @Test
    public void loadsSimpleAndXMLPropertyFilesProper() {
        JavaConfigurationPropertySource propertySource = new JavaConfigurationPropertySource();
        propertySource.init(getClass().getClassLoader());
        assertThat(propertySource.getProperties().keySet()).hasSize(7);  // double the getNumChilds for .source values.

        for (int i = 1; i < 6; i++) {
            String key = "confkey" + i;
            String value = "javaconf-value" + i;

            assertThat(value).isEqualTo(propertySource.get(key).getValue());
        }

    }
    
    @Test
    public void testConstructionPropertiesAndDisabledBehavior() throws IOException {
        JavaConfigurationPropertySource localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
        StringWriter stringBufferWriter = new StringWriter();
        System.getProperties().store(stringBufferWriter, null);
        String before = stringBufferWriter.toString();

        try {
            assertThat(localJavaConfigurationPropertySource.isEnabled()).isTrue();

            System.setProperty("tamaya.defaultprops.disable", "true");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertThat(localJavaConfigurationPropertySource.isEnabled()).isFalse();
            assertThat(localJavaConfigurationPropertySource.getProperties().isEmpty()).isTrue();
            assertThat(localJavaConfigurationPropertySource.toString().contains("enabled=false")).isTrue();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "true");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertThat(localJavaConfigurationPropertySource.isEnabled()).isFalse();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaultprops.disable", "");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertThat(localJavaConfigurationPropertySource.isEnabled()).isTrue();

            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
            System.setProperty("tamaya.defaults.disable", "");
            localJavaConfigurationPropertySource = new JavaConfigurationPropertySource();
            assertThat(localJavaConfigurationPropertySource.isEnabled()).isTrue();

        } finally {
            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
        }
    }
}
