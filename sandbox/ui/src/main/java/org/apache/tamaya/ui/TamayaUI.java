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
package org.apache.tamaya.ui;

import com.vaadin.server.VaadinServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Wrapper;
import org.apache.catalina.startup.Tomcat;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.io.File;
import java.util.logging.Logger;

public class TamayaUI {

    private static final Logger LOG = Logger.getLogger(TamayaUI.class.getName());


    public static void main(String[] args) throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        String contextPath = config.getOrDefault("tamaya.server.contextPath", "/tamaya");
        String appBase = ".";
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(Integer.valueOf(config.getOrDefault("tamaya.server.port", Integer.class, 8090) ));

        // Define a web application context.
        Context context = tomcat.addWebapp(contextPath, new File(
                appBase).getAbsolutePath());
        // Add Vadiin servlet
        Wrapper wrapper = tomcat.addServlet(context, "vadiin-servlet",
                VaadinServlet.class.getName());
        wrapper.addInitParameter("ui",
                VadiinApp.class.getName());
        wrapper.addInitParameter("productionMode",config.getOrDefault("tamaya.server.productionMode", String.class,
                "false"));
        wrapper.addInitParameter("asyncSupported", "true");
        context.addServletMapping("/*", "vadiin-servlet");
        // bootstrap.addBundle(new AssetsBundle("/VAADIN", "/VAADIN", null, "vaadin"));
        tomcat.start();
        tomcat.getServer().await();
    }

}