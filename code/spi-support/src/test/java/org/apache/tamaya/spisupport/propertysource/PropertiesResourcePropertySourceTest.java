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
package org.apache.tamaya.spisupport.propertysource;

import java.net.URL;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author William.Lieurance 2018.02.17
 */
public class PropertiesResourcePropertySourceTest {

    private final String testFileName = "testfile.properties";
    private final URL resource = getClass().getResource("/" + testFileName);

    @Test
    public void testBasicConstructor() {
        PropertiesResourcePropertySource source = new PropertiesResourcePropertySource(resource);
        assertThat(source).isNotNull();
        assertThat(source.getProperties()).hasSize(5).containsKey("key1"); // double the getNumChilds for .source values.
    }

    @Test
    public void testPrefixedPathBadClassloaderConstructor() {
        ClassLoader badLoader = new ClassLoader() {
            @Override
            public URL getResource(String name){
                return null;
            }
        };
        PropertiesResourcePropertySource source = new PropertiesResourcePropertySource(testFileName, badLoader);
        assertThat(source).isNotNull();
        assertThat(source.getProperties()).isEmpty();
    }
    
}
