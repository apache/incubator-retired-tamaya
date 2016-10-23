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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Map;
import java.util.Set;

/**
 * This tests checks if the combination of 2 prioritized PropertySource return valid results on the final Configuration.
 */
public class ConfigurationTest {

    @Test
    public void testAccess(){
        assertNotNull(current());
    }

    private Configuration current() {
        return ConfigurationProvider.getConfiguration();
    }

    @Test
    public void testContent(){
        assertEquals("Robin", current().get("name"));
        assertEquals("Sabine", current().get("name2")); // from default
        assertEquals("Mapped to name: Robin", current().get("name3"));  // oderridden default, mapped by filter to name property
        assertEquals("Sereina(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)", current().get("name4")); // final only
        assertNull(current().get("name5")); // final only, but removed from filter

        System.out.println("name : " + current().get("name"));
        System.out.println("name2: " + current().get("name2"));
        System.out.println("name3: " + current().get("name3"));
        System.out.println("name4: " + current().get("name4"));
        System.out.println("name5: " + current().get("name5"));
    }
}
