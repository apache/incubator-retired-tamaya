/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tamaya.integration.cdi;
//
//import org.junit.Test;
//
//import java.util.Map;
//import java.util.Properties;
//
//import static org.junit.Assert.assertEquals;
//
///**
// * Tests the related environment properties exist
// */
//public class EnvironmentsTest {
//
//    @Test
//    public void testGetProperties() throws Exception {
//
//        final Properties test = Environments.getProperties("test");
//
//        // loaded from test.properties
//        assertEquals("classpath:/test-service-wsdl.xml", test.getProperty("remote.wsdl.location"));
//
//        // loaded from base.properties
//        assertEquals("joecool", test.getProperty("remote.username"));
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void noEnvFound() {
//        Environments.getProperties("does not exists");
//    }
//
//    @Test
//    public void dev() throws Exception {
//
//        final Properties test = Environments.getProperties("dev");
//
//        assertEquals("org.apache.openejb.cipher.StaticDESPasswordCipher", test.getProperty("cipher"));
//        assertEquals("NjAq6q2agYVnvSMz+eYUZg==", test.getProperty("remote.password"));
//        assertEquals("1443", test.getProperty("remote.port"));
//        assertEquals("https://srv1114.supertribe.org:1443/remote/service/url", test.getProperty("remote.target.url"));
//        assertEquals("srv1114.supertribe.org:1443", test.getProperty("remote.address"));
//        assertEquals("srv1114.supertribe.org", test.getProperty("remote.host"));
//        assertEquals("classpath:/service-wsdl.xml", test.getProperty("remote.wsdl.location"));
//        assertEquals("joecool", test.getProperty("remote.username"));
//    }
//
//    @Test
//    public void cert() throws Exception {
//        final Properties test = Environments.getProperties("cert");
//        assertEquals("srv1016.supertribe.org", test.getProperty("remote.host"));
//        assertEquals("joecool", test.getProperty("remote.username"));
//    }
//
//    @Test
//    public void prod() throws Exception {
//        final Properties test = Environments.getProperties("prod");
//        assertEquals("remotedb001.supertribe.org", test.getProperty("remote.host"));
//        assertEquals("joecool", test.getProperty("remote.username"));
//    }
//
//
//    private static void generateAsserts(Properties test) {
//        for (Map.Entry<Object, Object> entry : test.entrySet()) {
//            System.out.printf("assertEquals(\"%s\", test.getProperty(\"%s\"));%n", entry.getValue(), entry.getKey());
//        }
//    }
//}