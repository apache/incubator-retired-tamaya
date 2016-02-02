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

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Main Application for the Tamaya Configuration Server.
 */
public class ConfigServiceApp extends Application<ConfigServiceConfiguration> {

    public static void main(String... args) throws Exception {
        new ConfigServiceApp().run(args);
    }

    @Override
    public String getName() {
        return "Tamaya Config-Server";
    }

    @Override
    public void initialize(Bootstrap<ConfigServiceConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(ConfigServiceConfiguration configuration,
                    Environment environment) {
        final ConfigurationResource resource = new ConfigurationResource(
                configuration.getScope()
        );
    //    final TemplateHealthCheck healthCheck =
    //            new TemplateHealthCheck(configuration.getTemplate());
    //    environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }


}