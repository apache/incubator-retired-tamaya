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
package org.apache.tamaya.resource.internal;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests the {@link org.apache.tamaya.resource.internal.DefaultResourceResolver} using CP and FS path expressions.
 */
public class DefaultResourceResolverTest {

    private DefaultResourceResolver resolver = new DefaultResourceResolver();


    @Test
    public void testGetResources_CP() throws Exception {
        Collection<URL> found = resolver.getResources("classpath:resources_testRoot/**/*.file");
        assertEquals(7, found.size());
        Collection<URL> found2 = resolver.getResources("resources_testRoot/**/*.file");
        assertEquals(found, found2);
    }

    @Test
    public void testGetResources_FS() throws Exception {
        String resDir = getResourceDir();
        Collection<URL> found = resolver.getResources("file:" + resDir + "/resources_testroot/aa?a/*.file");
        assertEquals(5, found.size());
        Collection<URL> found2 = resolver.getResources(resDir + "/resources_testroot/aa?a/*.file");
        assertEquals(found, found2);
    }

    private String getResourceDir() throws URISyntaxException {
        URL res = getClass().getResource("/resources_testroot/");
        return new File(res.toURI()).getParentFile().getAbsolutePath();
    }
}