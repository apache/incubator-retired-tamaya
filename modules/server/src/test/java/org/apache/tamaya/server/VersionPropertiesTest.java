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
package org.apache.tamaya.server;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.*;

public class VersionPropertiesTest {

    @Test
    public void correctVersionPropertiesAreReadAndSet() throws IOException {
        InputStream resource = VersionProperties.class.getResourceAsStream("/META-INF/tamaya-server-version.properties");

        Properties properties = new Properties();
        properties.load(resource);

        assertThat(VersionProperties.getVersion(), not(Matchers.isEmptyOrNullString()));
        assertThat(VersionProperties.getVersion(), equalTo(properties.get("server.version")));
        assertThat(VersionProperties.getProduct(), not(Matchers.isEmptyOrNullString()));
        assertThat(VersionProperties.getProduct(), equalTo(properties.get("server.product")));
    }
}
