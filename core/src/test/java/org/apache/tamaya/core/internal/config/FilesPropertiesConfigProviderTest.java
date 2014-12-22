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
