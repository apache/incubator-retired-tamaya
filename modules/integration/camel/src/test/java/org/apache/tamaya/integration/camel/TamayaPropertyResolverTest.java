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
package org.apache.tamaya.integration.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.ProxyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.RoutesDefinition;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * Tests for integration of Tamaya with Apache Camel using Java DSL and XML DSL.
 */
public class TamayaPropertyResolverTest {

    @Test
    public void testJavaDSLWithCfgResolution() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("properties", new TamayaPropertiesComponent());
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                from("direct:hello").transform().simple("{{cfg:message}}");
            }
        };
        camelContext.addRoutes(builder);
        camelContext.start();
        // test configuration is injected right...
        Greeter proxy = new ProxyBuilder(camelContext).endpoint("direct:hello").build(Greeter.class);
        String greetMessage = proxy.greet();
        assertEquals("Good Bye from Apache Tamaya!", greetMessage);
    }

    @Test
    public void testJavaDSLWithTamayaResolution() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addComponent("properties", new TamayaPropertiesComponent());
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                from("direct:hello").transform().simple("{{tamaya:message}}");
            }
        };
        camelContext.addRoutes(builder);
        camelContext.start();
        // test configuration is injected right...
        Greeter proxy = new ProxyBuilder(camelContext).endpoint("direct:hello").build(Greeter.class);
        String greetMessage = proxy.greet();
        assertEquals("Good Bye from Apache Tamaya!", greetMessage);
    }

    @Test
    public void testJavaDSLWithOverrideActive() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        TamayaPropertiesComponent props = new TamayaPropertiesComponent();
        props.setTamayaOverrides(true);
        camelContext.addComponent("properties", props);
        RouteBuilder builder = new RouteBuilder() {
            public void configure() {
                from("direct:hello").transform().simple("{{message}}");
            }
        };
        camelContext.addRoutes(builder);
        camelContext.start();
        // test configuration is injected right...
        Greeter proxy = new ProxyBuilder(camelContext).endpoint("direct:hello").build(Greeter.class);
        String greetMessage = proxy.greet();
        assertEquals("Good Bye from Apache Tamaya!", greetMessage);
    }

    @Test
    public void testXmlDSL() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        // This is normally done by the Spring implemented registry, we keep it simple here...
        TamayaPropertiesComponent props = new TamayaPropertiesComponent();
        props.setTamayaOverrides(true);
        camelContext.addComponent("properties", props);
        // Read routes from XML DSL
        InputStream is = getClass().getResourceAsStream("/META-INF/routes.xml");
        RoutesDefinition routes = camelContext.loadRoutesDefinition(is);
        for(RouteDefinition def: routes.getRoutes()) {
            camelContext.addRouteDefinition(def);
        }
        camelContext.start();
        Greeter greeter = new ProxyBuilder(camelContext).endpoint("direct:hello1").build(Greeter.class);
        assertEquals("Good Bye from Apache Tamaya!", greeter.greet());
        greeter = new ProxyBuilder(camelContext).endpoint("direct:hello2").build(Greeter.class);
        assertEquals("Good Bye from Apache Tamaya!", greeter.greet());
        greeter = new ProxyBuilder(camelContext).endpoint("direct:hello3").build(Greeter.class);
        assertEquals("Good Bye from Apache Tamaya!", greeter.greet());
    }

    public interface Greeter {
        String greet();
    }
}