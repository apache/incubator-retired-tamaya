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
package org.apache.tamaya.json;

import org.apache.tamaya.core.internal.DefaultServiceContext;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.ServiceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@RunWith(Arquillian.class)
public class JSONPropertySourceProviderMissingConfigFileIT {

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);

        archive.addPackage(org.apache.tamaya.Configuration.class.getPackage())
               .addPackage(org.apache.tamaya.spi.PropertySource.class.getPackage());

        archive.addPackage(DefaultServiceContext.class.getPackage())
               .addAsServiceProvider(ServiceContext.class, DefaultServiceContext.class);


        archive.addPackage(JSONPropertySource.class.getPackage())
               .addAsServiceProvider(PropertySourceProvider.class, JSONPropertySourceProvider.class);

        return archive;
    }

    @Test
    public void providerReturnsEmptyListOfProvidersIfThereIsNoOneDefaultJSONConfig() {
        List<PropertySourceProvider> services = ServiceContext.getInstance().getServices(PropertySourceProvider.class);

        PropertySourceProvider provider = services.stream()
                                                  .filter(s -> s instanceof JSONPropertySourceProvider)
                                                  .findFirst().get();

        assertThat(provider.getPropertySources(), notNullValue());
        assertThat(provider.getPropertySources(), hasSize(0));
    }

}
