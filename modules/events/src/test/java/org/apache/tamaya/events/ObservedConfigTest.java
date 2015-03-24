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
package org.apache.tamaya.events;

import org.apache.tamaya.ConfigurationProvider;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test (currently manual) to test configuration changes.
 */
public class ObservedConfigTest {

    @Test
    //Y TODO Check tests later
    public void testConfigChanges()throws Exception{
//        // test empty directory
//        testEmpty();
//        // add a file, test for changes
//        Files.copy(getSourceFile("test1.properties"), getTargetFile("test1.properties"));
//        try {
//            Thread.sleep(2000L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        testProperties1();
//        Files.copy(getSourceFile("test2.properties"), getTargetFile("test2.properties"));
//        Files.copy(getSourceFile("test3.properties"), getTargetFile("test3.properties"));
//        try {
//            Thread.sleep(2000L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        testAllFiles();
//        // change a file, test for changes
//        Files.copy(getSourceFile("test1b.properties"), getTargetFile("test1.properties"));
//        try {
//            Thread.sleep(2000L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        testProperties2();
//        // remove a file, test for changes
//        Files.delete(getTargetFile("test2.properties"));
//        try {
//            Thread.sleep(2000L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        testProperties3();
//        // cleanup directory
//        Files.deleteIfExists(getTargetFile("test1.properties"));
//        Files.deleteIfExists(getTargetFile("test2.properties"));
//        Files.deleteIfExists(getTargetFile("test3.properties"));
//        try {
//            Thread.sleep(2000L);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        testEmpty();
    }

    private static Path getSourceFile(String name) throws Exception{
        URL url = ObservedConfigTest.class.getResource("/data");
        File testFile = new File(new File(url.toURI()), name);
        return Paths.get(testFile.toURI());
    }

    private static Path getTargetFile(String name) {
        File testFile = new File(TestObservingProvider.getTestDirectory(), name);
        return Paths.get(testFile.toURI());
    }

    private void testEmpty() {
        assertNull(ConfigurationProvider.getConfiguration().get("1"));
        assertNull(ConfigurationProvider.getConfiguration().get("2"));
        assertNull(ConfigurationProvider.getConfiguration().get("3"));
        assertNull(ConfigurationProvider.getConfiguration().get("4"));
        assertNull(ConfigurationProvider.getConfiguration().get("5"));
        assertNull(ConfigurationProvider.getConfiguration().get("6"));
    }

    private void testAllFiles() {
        assertNotNull(ConfigurationProvider.getConfiguration().get("1"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("2"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("3"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("4"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("5"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("6"));
    }

    private void testProperties1() {
        assertNotNull(ConfigurationProvider.getConfiguration().get("1"));
        assertNull(ConfigurationProvider.getConfiguration().get("2"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("3"));
        assertNull(ConfigurationProvider.getConfiguration().get("4"));
        assertNull(ConfigurationProvider.getConfiguration().get("5"));
        assertNull(ConfigurationProvider.getConfiguration().get("6"));
    }

    private void testProperties2() {
        assertNotNull(ConfigurationProvider.getConfiguration().get("1"));
        assertNull(ConfigurationProvider.getConfiguration().get("2"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("3"));
        assertNull(ConfigurationProvider.getConfiguration().get("4"));
        assertNull(ConfigurationProvider.getConfiguration().get("5"));
        assertNull(ConfigurationProvider.getConfiguration().get("6"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("7"));
    }

    private void testProperties3() {
        assertNotNull(ConfigurationProvider.getConfiguration().get("1"));
        assertNull(ConfigurationProvider.getConfiguration().get("2"));
        assertNotNull(ConfigurationProvider.getConfiguration().get("3"));
        assertNull(ConfigurationProvider.getConfiguration().get("4"));
        assertNull(ConfigurationProvider.getConfiguration().get("5"));
        assertNull(ConfigurationProvider.getConfiguration().get("6"));
    }

    /**
     * Test method that periodically prints out what is happening.
     */
    public static void main() {
        while(true){
            System.out.println("1: " + ConfigurationProvider.getConfiguration().get("1"));
            System.out.println("2: " + ConfigurationProvider.getConfiguration().get("2"));
            System.out.println("3: " + ConfigurationProvider.getConfiguration().get("3"));
            System.out.println("4: " + ConfigurationProvider.getConfiguration().get("4"));
            System.out.println("5: " + ConfigurationProvider.getConfiguration().get("5"));
            System.out.println("6: " + ConfigurationProvider.getConfiguration().get("6"));
            System.out.println("=======================================================================");
            try {
                Thread.sleep(2000L);
            } catch (Exception e) {
                // stop here...
            }
        }
    }

    @AfterClass
    public static void cleanup()throws Exception{
        // cleanup directory
        Files.deleteIfExists(getTargetFile("test1.properties"));
        Files.deleteIfExists(getTargetFile("test2.properties"));
        Files.deleteIfExists(getTargetFile("test3.properties"));
    }

}
