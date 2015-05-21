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
package org.apache.tamaya.metamodel.environment;

import org.apache.tamaya.environment.RuntimeContext;
import org.apache.tamaya.environment.RuntimeContextProvider;
import org.junit.Test;


import static org.junit.Assert.*;

/**
 * Tests for basic {@link RuntimeContextProvider} functionality.
 * Created by Anatole on 17.10.2014.
 */
public class RuntimeContextProviderSpiTest {

    @Test
    public void testGetEnvironment(){
        RuntimeContext env = RuntimeContextProvider.current();
        assertNotNull(env);
        RuntimeContext env2 = RuntimeContextProvider.current();
        assertNotNull(env2);
        assertFalse("Current Environments requested in same context are not the same!", env==env2);
    }

    @Test
    public void testRootIsNotCurrentEnvironment(){
        RuntimeContext env1 = RuntimeContextProvider.current();
        assertNotNull(env1);
        RuntimeContext env2 = RuntimeContextProvider.current();
        assertNotNull(env2);
        // within this testdata environment these are always the same
        assertEquals(env1, env2);
    }

    @Test
    public void testEnvironmentOverride(){
        assertEquals(RuntimeContextProvider.current().get("env.STAGE"), "MyStage");
    }
}
