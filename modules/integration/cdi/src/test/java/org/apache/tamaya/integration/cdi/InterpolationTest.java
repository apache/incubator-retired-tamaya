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
//import org.tomitribe.util.IO;
//
//import java.net.URL;
//import java.util.Properties;
//
//import static org.junit.Assert.assertEquals;
//
//public class InterpolationTest {
//
//    @Test
//    public void testInterpolate() throws Exception {
//        final Properties interpolated;
//        {
//            final Properties properties = new Properties();
//            properties.setProperty("foo.host", "localhost");
//            properties.setProperty("foo.port", "1234");
//            properties.setProperty("address", "http://${foo.host}:${foo.port}");
//            properties.setProperty("url", "${address}/webapp");
//            properties.setProperty("urlUnchanged", "${not an address}/webapp");
//
//            interpolated = Interpolation.interpolate(properties);
//        }
//
//        assertEquals("localhost", interpolated.getProperty("foo.host"));
//        assertEquals("1234", interpolated.getProperty("foo.port"));
//        assertEquals("http://localhost:1234", interpolated.getProperty("address"));
//        assertEquals("http://localhost:1234/webapp", interpolated.getProperty("url"));
//        assertEquals("${not an address}/webapp", interpolated.getProperty("urlUnchanged"));
//    }
//
//    @Test
//    public void test() throws Exception {
//
//        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
//
//        final URL resource = loader.getResource("test.properties");
//        final Properties properties = Interpolation.interpolate(IO.readProperties(resource));
//
//        //remote.wsdl.location = classpath:/lx01116-zhr-active-partner-service-wsdl.xml
//        assertEquals("classpath:/test-service-wsdl.xml", properties.getProperty("remote.wsdl.location"));
//    }
//
//}