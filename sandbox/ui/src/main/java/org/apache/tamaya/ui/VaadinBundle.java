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

import com.google.common.collect.Maps;
import io.dropwizard.Bundle;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.session.SessionHandler;

import javax.servlet.Servlet;
import java.util.Map;

public class VaadinBundle implements Bundle {

    private final Map<String, Class<? extends Servlet>> servlets = Maps.newLinkedHashMap();

    private final SessionHandler sessionHandler;


    public VaadinBundle(Class<? extends Servlet> servlet, String pathSpec) {
        this.sessionHandler = new SessionHandler();
        servlets.put(pathSpec, servlet);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/VAADIN", "/VAADIN", null, "vaadin"));
    }

    @Override
    public void run(Environment environment) {
        environment.servlets().setSessionHandler(sessionHandler);
        for (Map.Entry<String, Class<? extends Servlet>> servlet : servlets.entrySet()) {
            environment.getApplicationContext().addServlet(servlet.getValue(), servlet.getKey());
        }
    }
}