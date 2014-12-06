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
package org.apache.tamaya.core.properties;

import org.apache.tamaya.PropertyProviderBuilder;
import org.junit.Test;

import org.apache.tamaya.PropertyProvider;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anatole on 30.09.2014.
 */
public class PropertyProviderBuilderTest {

    @Test
    public void testFromEnvironmentProperties(){
        PropertyProvider prov = PropertyProviderBuilder.create("test").addEnvironmentProperties().build();
        assertNotNull(prov);
        for(Map.Entry<String,String> en:System.getenv().entrySet()){
            assertEquals(en.getValue(), prov.get(en.getKey()).get());
        }
    }

    @Test
    public void testFromSystemProperties(){
        PropertyProvider prov = PropertyProviderBuilder.create("test").addSystemProperties().build();
        assertNotNull(prov);
        for(Map.Entry<Object,Object> en:System.getProperties().entrySet()){
            assertEquals(en.getValue(), prov.get(en.getKey().toString()).get());
        }
    }

}
