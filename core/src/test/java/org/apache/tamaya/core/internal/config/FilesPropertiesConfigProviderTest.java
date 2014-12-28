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
package org.apache.tamaya.core.internal.config;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;
import org.junit.Before;
import org.junit.Test;

public class FilesPropertiesConfigProviderTest {


	private ConfigurationProviderSpi configurationProvider;

	@Before
	public void init() throws InterruptedException {
		configurationProvider = new FilesPropertiesConfigProvider();
	}

	@Test
	public void getTest() throws InterruptedException{
	    Configuration configuration = configurationProvider.getConfiguration();
	    assertEquals(configuration.get("team").get(), "Bahia");
	    assertFalse(configuration.get("ignore").isPresent());
	}

	@Test
	public void shouldUpdateAsync() throws Exception {
	    createPropertiesFile("newFile.properties", "language=java");
	    Configuration configuration = configurationProvider.getConfiguration();

	    Thread.sleep(100L);
	    assertEquals(configuration.get("language").get(), "java");

	}

	   @Test
	    public void shoulIgnoreAsync() throws Exception {
	        createPropertiesFile("newFile.ini", "name=otavio");
	        Configuration configuration = configurationProvider.getConfiguration();

	        Thread.sleep(100L);
	        assertFalse(configuration.get("otavio").isPresent());

	    }

    private void createPropertiesFile(String fileName, String context) throws URISyntaxException,
            FileNotFoundException, IOException {
        URL resource = FilesPropertiesConfigProviderTest.class.getResource("/META-INF/configuration/");
	    Path directory = Paths.get(resource.toURI());
	    File file = new File(directory.toFile(), fileName);
        try (OutputStream stream = new FileOutputStream(file)) {
            stream.write(context.getBytes());
            file.deleteOnExit();
        }
    }
}
