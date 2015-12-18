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

import org.apache.commons.io.FileUtils;
import org.apache.tamaya.events.folderobserver.ObservingPropertySourceProvider;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Test configuration property source provider that observes a directory and updated the config if necessary.
 */
public class TestObservingProvider extends ObservingPropertySourceProvider{

    public static Path propertyLocation;

    static{
        try {
            // create some temporary config
            Path tempDir = Files.createTempDirectory("observedFolder");

            TestObservingProvider.propertyLocation = tempDir;

            FileUtils.copyInputStreamToFile(
                    TestObservingProvider.class.getResourceAsStream("/test.properties"),
                    new File(tempDir.toFile(), "test.properties"));

            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run(){
                    try{
                        // cleanup directory
                        Files.deleteIfExists(getTargetFile("test1.properties"));
                        Files.deleteIfExists(getTargetFile("test2.properties"));
                        Files.deleteIfExists(getTargetFile("test3.properties"));
                    }
                    catch(Exception e){
                        Logger.getLogger("TestObservingProvider").log(Level.WARNING,
                                "Failed to cleanup config test dir", e);
                    }
                }
            });
        }
        catch(Exception e){
            Logger.getLogger("TestObservingProvider").log(Level.WARNING, "Failed to init config test dir", e);
        }
    }

    private static Path getTargetFile(String name) {
        File testFile = new File(TestObservingProvider.getTestDirectory(), name);
        return Paths.get(testFile.toURI());
    }

    public TestObservingProvider(){
        super(propertyLocation);
        Logger.getLogger(getClass().getName()).info("Using test directory: " + getTestPath());
    }

    public static File getTestDirectory(){
        String tempDir = System.getProperty("java.io.tmpdir");
        File dir = new File(tempDir, "tamaya-events-testdir");
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    private static String getTestPath(){
        return getTestDirectory().getAbsolutePath();
    }
}
