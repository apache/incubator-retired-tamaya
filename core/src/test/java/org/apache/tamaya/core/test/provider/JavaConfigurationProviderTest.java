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
package org.apache.tamaya.core.test.provider;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.provider.JavaConfigurationProvider;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class JavaConfigurationProviderTest {

    @Test
    public void testJavaConfigurationProvider() {

        Collection<PropertySource> propertySources = new JavaConfigurationProvider().getPropertySources();

        Assert.assertNotNull(propertySources);
        Assert.assertEquals(1, propertySources.size());

        PropertySource propertySource = propertySources.iterator().next();
        for (int i = 1; i < 6; i++) {
            String key = "confkey" + i;
            String value = "javaconf-value" + i;

            Assert.assertEquals(value, propertySource.get(key).get());

            // check if we had our key in configuration.current
            Assert.assertTrue(Configuration.current().getProperties().containsKey(key));
            Assert.assertEquals(value, Configuration.current().get(key).get());
        }

    }

}
