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

import static org.junit.Assert.assertEquals;

/**
 * Test for checking correct lookup using the filesystem.
 */
public class FileCollectorTest {

    private String getResourceDir() throws URISyntaxException {
        URL res = getClass().getResource("/resources_testroot/");
        return new File(res.toURI()).getParentFile().getAbsolutePath();
    }

    @Test
    public void testCollectResourcesFromLocalFSPath() throws Exception {
        String resDir = getResourceDir();
        Collection<URL> found = FileCollector.collectFiles("file:" + resDir + "/**/*.file");
        assertEquals(7, found.size());
        System.out.println("file:" + resDir + "/**/*.file: " + found);
        Collection<URL> found2 = FileCollector.collectFiles(resDir + "/**/*.file");
        assertEquals(found, found2);
    }

    @Test
    public void testCollectResourcesFromLocalFSPath_WithFolderPlaceholder() throws Exception {
        String resDir = getResourceDir();
        Collection<URL> found = FileCollector.collectFiles("file:" + resDir + "/resources_testroot/aa?a/*.file");
        assertEquals(5, found.size());
        System.out.println("file:" + resDir + "/resources_testroot/aa?a/*.file: " + found);
        Collection<URL> found2 = FileCollector.collectFiles(resDir + "/resources_testroot/aa?a/*.file");
        assertEquals(found, found2);
    }

    @Test
    public void testCollectResourcesFromLocalFSPath_WithFolderAny() throws Exception {
        String resDir = getResourceDir();
        Collection<URL> found = FileCollector.collectFiles("file:" + resDir + "/resources_testroot/b*/b?/*.file");
        assertEquals(1, found.size());
        System.out.println("file:" + resDir + "/resources_testroot/b*/b?/*.file: " + found);
        Collection<URL> found2 = FileCollector.collectFiles(resDir + "/resources_testroot/b*/b?/*.file");
        assertEquals(found, found2);
    }


}