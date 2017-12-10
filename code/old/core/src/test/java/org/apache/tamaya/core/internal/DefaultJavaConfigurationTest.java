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

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spisupport.propertysource.JavaConfigurationPropertySource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.apache.tamaya.ConfigurationProvider.getConfiguration;

public class DefaultJavaConfigurationTest {

    private static final String A_UMLAUT = "\u00E4";
    private static final String O_UMLAUT = "\u00F6";

    @Test
    public void loadsSimpleAndXMLPropertyFilesProper() {
        for (int i = 1; i < 6; i++) {
            String key = "confkey" + i;
            String value = "javaconf-value" + i;
            // check if we had our key in configuration.current
            MatcherAssert.assertThat(getConfiguration().getProperties().containsKey(key), Matchers.is(true));
            MatcherAssert.assertThat(value, Matchers.equalTo(getConfiguration().get(key)));
        }

        MatcherAssert.assertThat(getConfiguration().getProperties().containsKey("aaeehh"), Matchers.is(true));
        MatcherAssert.assertThat(getConfiguration().getProperties().get("aaeehh"), Matchers.equalTo(A_UMLAUT));

        MatcherAssert.assertThat(getConfiguration().getProperties().containsKey(O_UMLAUT), Matchers.is(true));
        MatcherAssert.assertThat(getConfiguration().getProperties().get(O_UMLAUT), Matchers.equalTo("o"));
    }
}
