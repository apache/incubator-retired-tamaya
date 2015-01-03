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
package org.apache.tamaya.core.test.propertysource;

import org.apache.tamaya.core.propertysource.DefaultOrdinal;
import org.apache.tamaya.core.propertysource.EnvironmentPropertySource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Optional;

public class EnvironmentPropertySourceTest {

    private EnvironmentPropertySource propertySource = new EnvironmentPropertySource();


    @Test
    public void testGetOrdinal() {
        Assert.assertEquals(DefaultOrdinal.ENVIRONMENT_PROPERTIES, propertySource.getOrdinal());
    }

    @Test
    public void testGet() {
        String environmentPropertyToCheck = System.getenv().keySet().iterator().next();

        Optional<String> value = propertySource.get(environmentPropertyToCheck);
        Assert.assertTrue(value.isPresent());
        Assert.assertEquals(System.getenv(environmentPropertyToCheck), value.get());
    }

    @Test
    public void testGetProperties() {
        Map<String, String> environmentProperties = System.getenv();

        Assert.assertEquals(environmentProperties.size(), propertySource.getProperties().size());

        for (Map.Entry<String, String> propertySourceEntry : propertySource.getProperties().entrySet()) {
            Assert.assertEquals("Entry values for key '" + propertySourceEntry.getKey() + "' do not match",
                                environmentProperties.get(propertySourceEntry.getKey()), propertySourceEntry.getValue());
        }

        // modification is not allowed
        try {
            propertySource.getProperties().put("add.new.keys", "must throw exception");
            Assert.fail(UnsupportedOperationException.class.getName() + " expected");
        }
        catch (UnsupportedOperationException e) {
            // expected -> all is fine
        }
    }


}
