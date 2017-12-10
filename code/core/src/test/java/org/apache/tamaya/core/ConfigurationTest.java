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
package org.apache.tamaya.core;

import org.junit.Test;

import javax.config.Config;
import javax.config.ConfigProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This tests checks if the combination of 2 prioritized PropertySource return valid results of the final configuration.
 */
public class ConfigurationTest {

    @Test
    public void testAccess(){
        assertNotNull(current());
    }

    private Config current() {
        return ConfigProvider.getConfig();
    }

    @Test
    public void testContent(){
        assertNotNull(current().getValue("name", String.class));
        assertNotNull(current().getValue("name2", String.class)); // from default
        assertNotNull(current().getValue("name3", String.class)); // overridden default, mapped by filter to name property
        assertNotNull(current().getValue("name4", String.class)); // final only


        assertEquals("Robin", current().getValue("name", String.class));
        assertEquals("Sabine", current().getValue("name2", String.class)); // from default
        assertEquals("Mapped to name: Robin", current().getValue("name3", String.class));  // overridden default, mapped by filter to name property
        assertEquals("Sereina(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)", current().getValue("name4", String.class)); // final only
        assertNull(current().getValue("name5", String.class)); // final only, but removed from filter
    }
}
