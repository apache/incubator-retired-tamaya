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
package org.apache.tamaya.core.provider;

import org.apache.tamaya.core.propertysource.JavaConfigurationPropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import static org.apache.tamaya.ConfigurationProvider.getConfiguration;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class JavaConfigurationProviderTest {

    private static final String A_UMLAUT = "\u00E4";
    private static final String O_UMLAUT = "\u00F6";

    @Test
    public void loadsSimpleAndXMLPropertyFilesProper() {
        PropertySource propertySource = new JavaConfigurationPropertySource();
        assertThat(propertySource.getProperties().keySet(), hasSize(7));  // double the size for .source values.

        for (int i = 1; i < 6; i++) {
            String key = "confkey" + i;
            String value = "javaconf-value" + i;

            assertThat(value, equalTo(propertySource.get(key).getValue()));

            // check if we had our key in configuration.current
            assertThat(getConfiguration().getProperties().containsKey(key), is(true));
            assertThat(value, equalTo(getConfiguration().get(key)));
        }

        assertThat(getConfiguration().getProperties().containsKey("aaeehh"), is(true));
        assertThat(getConfiguration().getProperties().get("aaeehh"), equalTo(A_UMLAUT));

        assertThat(getConfiguration().getProperties().containsKey(O_UMLAUT), is(true));
        assertThat(getConfiguration().getProperties().get(O_UMLAUT), equalTo("o"));
    }
}
