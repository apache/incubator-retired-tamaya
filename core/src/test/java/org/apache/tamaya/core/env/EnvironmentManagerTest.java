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
package org.apache.tamaya.core.env;

import org.apache.tamaya.Environment;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Tests for basic {@link org.apache.tamaya.EnvironmentManager} functionality.
 * Created by Anatole on 17.10.2014.
 */
public class EnvironmentManagerTest {

    @Test
    public void testGetEnvironment(){
        Environment env = Environment.current();
        assertNotNull(env);
        Environment env2 = Environment.current();
        assertNotNull(env2);
        assertFalse("Current Environments requested in same context are not the same!", env==env2);
    }

    @Test
    public void testGetRootEnvironment(){
        Environment env = Environment.root();
        assertNotNull(env);
        Environment env2 = Environment.root();
        assertNotNull(env2);
        assertTrue("Root Environments requested in same context are not the same!", env==env2);
    }

    @Test
    public void testRootIsNotCurrentEnvironment(){
        Environment env1 = Environment.root();
        Environment env2 = Environment.current();
        assertNotNull(env1);
        assertNotNull(env2);
        // within this test environment these are always the same
        assertEquals(env1, env2);
    }

    @Test
    public void testEnvironmentOverride(){
        assertEquals(Environment.root().get("user.country").get(),System.getProperty("user.country"));
        assertEquals(Environment.current().get("user.country").get(), System.getProperty("user.country"));
    }
}
