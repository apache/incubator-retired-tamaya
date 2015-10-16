/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.tamaya.integration.cdi;

import org.apache.deltaspike.testcontrol.api.TestControl;
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Singleton;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for CDI integration.
 */
@RunWith(CdiTestRunner.class)
@TestControl(startScopes = {ApplicationScoped.class, Singleton.class})
public class ConfiguredTest{

    @Test
    public void test_Configuration_is_injected_correctly(){
        ConfiguredClass item = CDI.current().select(ConfiguredClass.class).get();
        System.out.println("********************************************");
        System.out.println(item);
        System.out.println("********************************************");
        double actual = 1234.5678;
        MatcherAssert.assertThat(item.getDoubleValue(), is(actual));
    }

    @Test
    public void test_Default_injections_are_accessible(){
        InjectedClass injectedClass =  CDI.current().select(InjectedClass.class).get();
        System.out.println("********************************************");
        System.out.println(injectedClass);
        System.out.println("********************************************");
        assertNotNull(injectedClass.builder1);
        assertNotNull(injectedClass.builder2);
        assertNotNull(injectedClass.config);
        assertNotNull(injectedClass.configContext);
    }

    @Test
    public void test_Injected_builders_are_notSame(){
        InjectedClass injectedClass =  CDI.current().select(InjectedClass.class).get();
        assertTrue(injectedClass.builder1 != injectedClass.builder2);
    }

    @Test
    public void test_Injected_configs_are_same(){
        InjectedClass injectedClass =  CDI.current().select(InjectedClass.class).get();
        assertTrue(injectedClass.config == injectedClass.config2);
    }

    @Test
    public void test_Injected_configContexts_are_same(){
        InjectedClass injectedClass =  CDI.current().select(InjectedClass.class).get();
        assertTrue(injectedClass.configContext == injectedClass.configContext2);
    }

}
