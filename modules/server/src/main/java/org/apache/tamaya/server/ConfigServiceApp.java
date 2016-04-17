/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.server;


import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Main Application for the Tamaya Configuration Server.
 */
public class ConfigServiceApp {

    public class ResourceLoader extends Application{

        @Override
        public Set<Class<?>> getClasses() {
            final Set<Class<?>> classes = new HashSet<Class<?>>();

            // register root resource
            classes.add(ConfigurationResource.class);
            return classes;
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        String contextPath = config.getOrDefault("tamaya.server.contextPath", "/tamaya");
        String appBase = ".";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.valueOf(config.getOrDefault("tamaya.server.port", Integer.class, 8085) ));

        // Define a web application context.
        Context context = tomcat.addWebapp(contextPath, new File(
                appBase).getAbsolutePath());
//        tomcat.getHost().setAppBase(appBase);
        // Add servlet that will register Jersey REST resources
        Wrapper wrapper = tomcat.addServlet(context, "jersey-container-servlet",
                ServletContainer.class.getName());
        wrapper.addInitParameter("com.sun.jersey.config.property.packages",
                ConfigurationResource.class.getPackage().getName());
        context.addServletMapping("/*", "jersey-container-servlet");
        tomcat.start();
        tomcat.getServer().await();
    }


}