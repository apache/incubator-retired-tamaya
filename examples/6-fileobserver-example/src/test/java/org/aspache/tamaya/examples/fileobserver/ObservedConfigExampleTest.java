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
package org.aspache.tamaya.examples.fileobserver;

import org.apache.commons.io.FileUtils;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.events.folderobserver.ObservingPropertySourceProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Test (currently manual) to test configuration changes.
 */
public class ObservedConfigExampleTest {

    @Before
    public void setup() throws IOException {
        // create some temporary config
        Path tempDir = Files.createTempDirectory("observedFolder");
        FileUtils.copyInputStreamToFile(
                getClass().getResourceAsStream("/test.properties"),
                new File(tempDir.toFile(), "test.properties"));
    }


    private static Path getSourceFile(String name) throws Exception {
        URL url = ObservedConfigExampleTest.class.getResource("/data");
        File testFile = new File(new File(url.toURI()), name);
        return Paths.get(testFile.toURI());
    }

    private static Path getTargetFile(String name) {
        File testFile = new File(TestObservingProvider.propertyLocation.toFile(), name);
        return Paths.get(testFile.toURI());
    }

    /**
     * Test method that periodically prints out what is happening.
     */
    public static void main() {
        while (true) {
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
    public static void cleanup() throws Exception {
        // cleanup directory
        Files.deleteIfExists(getTargetFile("test1.properties"));
        Files.deleteIfExists(getTargetFile("test2.properties"));
        Files.deleteIfExists(getTargetFile("test3.properties"));
    }



    public void testInitialConfig() throws IOException {
        Configuration config = ConfigurationProvider.getConfiguration().with(TestConfigView.of());

        Map<String, String> props = config.getProperties();

        assertThat(props.get("test"), is("test2"));
        assertThat(props.get("testValue1"), is("value"));
        assertNull(props.get("a"));

    }

    @Test
    @Ignore // TODO Reactivate and see whats wrong...
    public void testChangingConfig() throws IOException {
        Configuration config = ConfigurationProvider.getConfiguration().with(TestConfigView.of());

        Map<String, String> props = config.getProperties();
        assertThat(props.get("test"), is("test2"));
        assertThat(props.get("testValue1"), is("value"));
        assertNull(props.get("testValue2"));

        //insert a new properties file into the tempdirectory
        FileUtils.writeStringToFile(
                new File(TestObservingProvider.propertyLocation.toFile(), "test2.properties"),
                "testValue2=anotherValue");

        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        config = ConfigurationProvider.getConfiguration().with(TestConfigView.of());

        props = config.getProperties();

        assertThat(props.get("test"), is("test2"));
        assertThat(props.get("testValue1"), is("value"));
        assertThat(props.get("testValue2"), is("anotherValue"));
    }

}
