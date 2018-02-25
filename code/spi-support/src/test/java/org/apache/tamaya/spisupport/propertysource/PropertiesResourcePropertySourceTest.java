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
import static org.junit.Assert.*;
 	

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
        assertNotNull(source);
        assertTrue(5 == source.getProperties().size()); // double the size for .source values.
        assertTrue(source.getProperties().containsKey("key1"));
    }

    @Test
    public void testPrefixedConstructor() {
        PropertiesResourcePropertySource source = new PropertiesResourcePropertySource(resource, "somePrefix");
        assertNotNull(source);
        assertTrue(5 == source.getProperties().size());
        assertTrue(source.getProperties().containsKey("somePrefixkey1"));
    }

    @Test
    public void testPrefixedPathConstructor() {
        //File path must be relative to classloader, not the class
        System.out.println(resource.getPath());
        PropertiesResourcePropertySource source = new PropertiesResourcePropertySource(testFileName, "somePrefix");
        assertNotNull(source);
        assertTrue(5 == source.getProperties().size());
        assertTrue(source.getProperties().containsKey("somePrefixkey1"));
    }
    
    @Test
    public void testPrefixedPathBadClassloaderConstructor() {
        ClassLoader badLoader = new ClassLoader() {
            @Override
            public URL getResource(String name){
                return null;
            }
        };
        PropertiesResourcePropertySource source = new PropertiesResourcePropertySource(testFileName, "somePrefix", badLoader);
        assertNotNull(source);
        assertTrue(source.getProperties().isEmpty());
    }
    
    @Test
    public void testPrefixedPathClassloaderConstructor() {
        PropertiesResourcePropertySource source = new PropertiesResourcePropertySource(testFileName, "somePrefix", null);
        assertNotNull(source);
        assertTrue(5 == source.getProperties().size());
        assertTrue(source.getProperties().containsKey("somePrefixkey1"));
    }

}
