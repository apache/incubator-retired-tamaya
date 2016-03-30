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

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import io.dropwizard.Application;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

public class VaadinApplication extends Application<VaadinApplication.Configuration> {

    private static final Logger LOG = Logger.getLogger(VaadinApplication.class.getName());

    @Override
    public void run(Configuration configuration, Environment environment) throws Exception {
        // empty
    }

    @Override
    public void initialize(Bootstrap<Configuration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new AdaptiveConfigurationsourceProvider());
        bootstrap.addBundle(new VaadinBundle(MyUI.MyUIServlet.class, "/tamaya/*"));
    }

    /**
     * Configuration source provider that reads from a file (similar to the default), but if not present/resovable also
     * tries to resolve the path as URL or classpath resource.
     */
    private static class AdaptiveConfigurationsourceProvider implements ConfigurationSourceProvider{

        @Override
        public InputStream open(String path) throws IOException {
            File file = new File(path);
            if (file.exists()) {
                LOG.info("Reading configuration from file: " + path);
                return new FileInputStream(file);
            }
            try {
                URL url = new URL(path);
                LOG.info("Reading configuration from url: " + path);
                return url.openStream();

            } catch (Exception e) {
                // continue
            }
            URL url = getClass().getClassLoader().getResource(path);
            if (url != null) {
                LOG.info("Reading configuration from classpath: " + path);
                return url.openStream();
            }
            // try default
            url = getClass().getClassLoader().getResource("/config/application.yml");
            if (url != null) {
                LOG.info("Reading configuration from classpath: /config/application.yml");
                return url.openStream();
            }
            return null;
        }
    }

    public static class Configuration extends io.dropwizard.Configuration {
        public String name;
    }

    public static void main(String... args) throws Exception {
        new VaadinApplication().run(args);
    }
}