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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.core.propertysource.EnvironmentPropertySource;
import org.apache.tamaya.core.propertysource.SystemPropertySource;
import org.apache.tamaya.core.provider.JavaConfigurationProvider;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationProviderSpi;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;

import static java.lang.System.getenv;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class ConfigurationFromPropertiesFileIT {

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);

        archive.addPackage(ConfigurationProviderSpi.class.getPackage())
               .addPackage(ConfigurationProvider.class.getPackage());

        archive.addPackage(DefaultServiceContext.class.getPackage())
               .addPackage(JavaConfigurationProvider.class.getPackage())
               .addPackage(EnvironmentPropertySource.class.getPackage());

        archive.addAsServiceProvider(ServiceContext.class, DefaultServiceContext.class)
               .addAsServiceProvider(PropertySource.class, EnvironmentPropertySource.class,
                                     SystemPropertySource.class)
               .addAsServiceProvider(PropertySourceProvider.class, JavaConfigurationProvider.class)
               .addAsServiceProvider(ConfigurationContext.class, DefaultConfigurationContext.class)
               .addAsServiceProvider(ConfigurationProviderSpi.class, DefaultConfigurationProvider.class);

        archive.addAsManifestResource("x34.properties", JavaConfigurationProvider.DEFAULT_PROPERTIES_FILE_NAME);

        return archive;
    }

    @Test
    public void configurationIsBuildProperly() {
        Configuration config = ConfigurationProvider.getConfiguration();

        // Source is x34.properties
        assertThat(config.get("x34"), notNullValue());
        assertThat(config.get("x34"), equalTo("x34"));
        assertThat(config.get("x34.a.b.c"), notNullValue());
        assertThat(config.get("x34.a.b.c"), equalTo("C"));

        // Source is the environment USERNAME on Windows, USER on unix based systems
        assertThat(Optional.ofNullable(getenv("USERNAME")).orElse(getenv("USER")),
                   not(isEmptyOrNullString()));
    }

}
