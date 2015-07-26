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
package org.apache.tamaya.json;

import org.apache.tamaya.format.ConfigurationFormat;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Integration tests for {@link JSONFormat}.
 */
public class JSONFormatIT {
    @Test
    public void jsonFormatCanBeFoundViaServiceLoader() throws Exception {
        List<ConfigurationFormat> formats = ServiceContextManager.getServiceContext()
                                                          .getServices(ConfigurationFormat.class);

        ConfigurationFormat format = null;
        for (ConfigurationFormat f : formats) {
            if (f instanceof JSONFormat) {
                format = f;
                break;
            }
        }
        assertThat(format, notNullValue());
    }
}
