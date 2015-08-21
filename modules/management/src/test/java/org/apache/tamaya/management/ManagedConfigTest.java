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
package org.apache.tamaya.management;

import static org.junit.Assert.*;

/**
 * Created by Anatole on 20.08.2015.
 */
public class ManagedConfigTest {

    private ManagedConfig bean = new ManagedConfig();

    @org.junit.Test
    public void testGetConfigurationInfo() throws Exception {
        System.out.println(bean.getConfigurationInfo());
    }

    @org.junit.Test
    public void testGetConfiguration() throws Exception {
        System.out.println(bean.getConfiguration());
    }

    @org.junit.Test
    public void testGetConfigurationArea() throws Exception {
        System.out.println(bean.getConfigurationArea("a", false));
    }

    @org.junit.Test
    public void testGetAreas() throws Exception {
        System.out.println(bean.getAreas());
    }

    @org.junit.Test
    public void testGetTransitiveAreas() throws Exception {
        System.out.println(bean.getTransitiveAreas());
    }

    @org.junit.Test
    public void testIsAreaExisting() throws Exception {
        assertTrue(bean.isAreaExisting("java"));
        assertFalse(bean.isAreaExisting("sd.fldsfl.erlwsf"));
    }

    @org.junit.Test
    public void testRegisterMBean() throws Exception {
        ManagedConfig.registerMBean();
        ManagedConfig.registerMBean();
        // Lookup object name

    }

    @org.junit.Test
    public void testRegisterMBean1() throws Exception {
        ManagedConfig.registerMBean("SubContext1");
        ManagedConfig.registerMBean("SubContext1");
        ManagedConfig.registerMBean("SubContext2");
        // Lookup object name

    }
}