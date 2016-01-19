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
package org.apache.tamaya.events.folderobserver;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.propertysource.BasePropertySource;
import org.apache.tamaya.events.ConfigEventManager;
import org.apache.tamaya.events.ConfigurationContextChange;
import org.apache.tamaya.events.ConfigurationContextChangeBuilder;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

/**
 * This implementation runs in a folder taking up all files compatible with the given
 * ConfigurationFormats. When a file is added, deleted or modified the PropertySourceProvider
 * will adapt the changes automatically and trigger according
 * {@link org.apache.tamaya.events.PropertySourceChange} events.
 * The default folder is META-INF/config, but you can change it via an absolute path in the
 * "-Dtamaya.configdir" parameter.
 */
public class ObservingPropertySourceProvider implements PropertySourceProvider, FileChangeObserver {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(ObservingPropertySourceProvider.class.getName());
    /**
     * The current active property sources of this provider.
     */
    private final List<PropertySource> propertySources = Collections.synchronizedList(new LinkedList<PropertySource>());
    /**
     * The thread pool used.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Constructorm using an explicit directory, ignoring all kind of configuration, if set.
     *
     * @param directory the target directory. If null, the default configuration and system property are used.
     */
    public ObservingPropertySourceProvider(Path directory) {
        if (directory == null) {
            directory = getDirectory();
        }
        if (directory!=null){
            synchronized (this.propertySources) {
                this.propertySources.addAll(readConfiguration(directory));
            }
            final Runnable runnable = new FileChangeListener(directory, this);
            executor.execute(runnable);
        } else {
            executor.shutdown();
        }
    }

    /**
     * Read the initial configuration.
     *
     * @param directory the target directory, not null.
     */
    private List<PropertySource> readConfiguration(Path directory) {
        final List<PropertySource> result = new ArrayList<>();
        try {
            synchronized (propertySources) {
                for (final Path path : Files.newDirectoryStream(directory, "*")) {
                    result.addAll(getPropertySources(path));
                }
                return result;
            }
        } catch (final IOException e) {
            LOG.log(Level.WARNING, "Failed to read configuration from dir: " + directory, e);
        }
        return result;
    }

    /**
     * Read property sources from the given file.
     * 
     * @param file source of the property sources.
     * @return property sources from the given file.
     */
    protected Collection<PropertySource> getPropertySources(final Path file) {
        return Arrays.asList(new PropertySource[]{new BasePropertySource() {
            private final Map<String,String> props = readProperties(file);
            @Override
            public Map<String, String> getProperties() {
                return props;
            }
        }});
    }

    /**
     * Load a single file.
     *
     * @param file the file, not null.
     * @return properties as read from the given file.
     */
    protected static Map<String,String> readProperties(Path file) {
        try (InputStream is = file.toUri().toURL().openStream()){
            final Properties props = new Properties();
                props.load(is);
            final Map<String,String> result = new HashMap<>();
            for(final Map.Entry<Object,Object> en:props.entrySet()){
                result.put(String.valueOf(en.getKey()), String.valueOf(en.getValue()));
            }
            return result;
        } catch (final Exception e) {
            LOG.log(Level.INFO, "Error reading file: " + file.toString() +
                    ", using format: properties", e);
        }
        return Collections.emptyMap();
    }


    /**
     * Evaluates the target directory from system property (tamaya.configdir) or classpath.
     *
     * @return the directory to be read, or null.
     */
    private Path getDirectory() {
        final String absolutePath = System.getProperty("tamaya.configdir");
        if (null!=absolutePath) {
            final Path path = Paths.get(absolutePath);
            if (Files.isDirectory(path)) {
                return path;
            }
        }
        final URL resource = ObservingPropertySourceProvider.class.getResource("/META-INF/config/");
        if (null!=resource) {
            try {
                return Paths.get(resource.toURI());
            } catch (final URISyntaxException e) {
                throw new ConfigException("An error to find the directory to watch", e);
            }
        }
        return null;
    }


    @Override
    public void directoryChanged(Path directory) {
        synchronized (this.propertySources) {
            final List<PropertySource> existingPropertySources = new ArrayList<>(propertySources);
            propertySources.clear();
            final Collection<PropertySource> sourcesRead = readConfiguration(directory);
            this.propertySources.addAll(sourcesRead);
            triggerConfigChange(existingPropertySources, propertySources);
        }
    }


    private void triggerConfigChange(List<PropertySource> originalPropertySources,
                                     List<PropertySource> newPropertySources) {
        final ConfigurationContextChangeBuilder b = ConfigurationContextChangeBuilder.of();
        for (final PropertySource ps : originalPropertySources) {
            b.removedPropertySource(ps);
        }
        for (final PropertySource ps : newPropertySources) {
            b.newPropertySource(ps);
        }
        final ConfigurationContextChange changeEvent = b.build();
        LOG.fine("Trigger Config Context Change: " + changeEvent);
        ConfigEventManager.fireEvent(changeEvent);
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        synchronized (propertySources) {
            return new ArrayList<>(this.propertySources);
        }
    }
}
