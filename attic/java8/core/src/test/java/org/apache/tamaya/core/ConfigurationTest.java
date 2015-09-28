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

import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This tests checks if the combination of 2 prioritized PropertySource return valid results on the final Configuration.
 */
public class ConfigurationTest {

    @Test
    public void testAccess(){
        assertNotNull(ConfigurationProvider.getConfiguration());
    }

    @Test
    public void testContent(){
        assertEquals("Robin", ConfigurationProvider.getConfiguration().getOptional("name").get());
        assertEquals("Sabine", ConfigurationProvider.getConfiguration().getOptional("name2").get()); // from default
        assertEquals("Mapped to name: Robin", ConfigurationProvider.getConfiguration().getOptional("name3").get());  // oderridden default, mapped by filter to name property
        assertEquals("Sereina(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)(filtered)", ConfigurationProvider.getConfiguration().getOptional("name4").get()); // final only
        assertNull(ConfigurationProvider.getConfiguration().getOptional("name5").orElse(null)); // final only, but removed from filter

        System.out.println("name : " + ConfigurationProvider.getConfiguration().getOptional("name").get());
        System.out.println("name2: " + ConfigurationProvider.getConfiguration().getOptional("name2").get());
        System.out.println("name3: " + ConfigurationProvider.getConfiguration().getOptional("name3").get());
        System.out.println("name4: " + ConfigurationProvider.getConfiguration().getOptional("name4").get());
        System.out.println("name5: " + ConfigurationProvider.getConfiguration().getOptional("name5").orElse(null));

        System.out.println("ALL :");
        ConfigurationProvider.getConfiguration().getProperties().entrySet().forEach(e ->
                System.out.println("   " + e.getKey()+" = " + e.getValue()));
    }


}
