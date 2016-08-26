///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
package org.apache.tamaya.integration.cdi;
//
//import org.apache.openejb.loader.SystemInstance;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class ConfigurationResolverTest {
//
//    private ConfigurationResolver resolver;
//
//    @Before
//    public void cleanEnv() {
//        SystemInstance.reset();
//        System.clearProperty("environment");
//
//        resolver = new ConfigurationResolver();
//    }
//
//    @Test
//    public void defaultEnvironment() {
//        assertEquals("test", resolver.getEnvironment());
//    }
//
//    @Test
//    public void overrideDefaultEnvironment() {
//        System.setProperty("environment", "dev");
//
//        // don't use the field cause before is invoked before we have a chance to set the environment
//        assertEquals("dev", new ConfigurationResolver().getEnvironment());
//    }
//
//    @Test
//    public void isResolvable() {
//
//        { // precondition
//            try {
//                resolver.isResolvableConfig(null, "value");
//                fail("a null key is not resolvable");
//
//            } catch (final NullPointerException e) {
//                // expected
//            }
//        }
//        { // precondition
//            try {
//                resolver.isResolvableConfig("key", null);
//                fail("a null default value is not resolvable");
//
//            } catch (final NullPointerException e) {
//                // expected
//            }
//        }
//
//        // loaded from test.properties
//        assertTrue(resolver.isResolvableConfig("remote.wsdl.location", ""));
//        assertFalse(resolver.isResolvableConfig("something", ""));
//
//        // loaded from base.properties
//        assertTrue(resolver.isResolvableConfig("remote.username", ""));
//        assertFalse(resolver.isResolvableConfig("bla", ""));
//    }
//
//    @Test
//    public void found() {
//
//        { // precondition
//            try {
//                resolver.isResolvableConfig(null, "value");
//                fail("a null key is not resolvable");
//
//            } catch (final NullPointerException e) {
//                // expected
//            }
//        }
//        { // precondition
//            try {
//                resolver.isResolvableConfig("key", null);
//                fail("a null default value is not resolvable");
//
//            } catch (final NullPointerException e) {
//                // expected
//            }
//        }
//
//        // loaded from test.properties
//        assertEquals("classpath:/service-wsdl.xml", resolver.resolve("remote.wsdl.location", ""));
//        assertEquals("something-else", resolver.resolve("something", "something-else"));
//
//        // loaded from base.properties
//        assertEquals("joecool", resolver.resolve("remote.username", ""));
//        assertEquals("blabla", resolver.resolve("bla", "blabla"));
//    }
//
//}
