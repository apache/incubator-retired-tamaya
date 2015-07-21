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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.events.ConfigEvent;
import org.apache.tamaya.events.delta.ConfigurationContextChange;
import org.apache.tamaya.events.delta.ConfigurationContextChangeBuilder;
import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationFormat;
import org.apache.tamaya.format.FlattenedDefaultPropertySource;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This implementation run in a folder taking up all file compatible with the given
 * ConfigurationFormats. When a file is added, deleted or modified the PropertySourceProvider
 * will adapt the changes automatically and trigger according
 * {@link org.apache.tamaya.events.delta.PropertySourceChange} events.
 * The default folder is META-INF/config, but you can change using the absolute path in
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
     * The supported configuration formats of this provider.
     */
    private Collection<ConfigurationFormat> formats = new ArrayList<>();
    /**
     * The thread pool used.
     */
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Constructor, reading the config file from classpath resource and system property.
     */
    public ObservingPropertySourceProvider(ConfigurationFormat... formats) {
        this(null, formats);
    }

    /**
     * Constructorm using an explicit directory, ignoring all kind of configuration, if set.
     *
     * @param directory the target directory. If null, the default configuration and system property are used.
     * @param formats   the formats to be used.
     */
    public ObservingPropertySourceProvider(Path directory, ConfigurationFormat... formats) {
        this.formats = Arrays.asList(formats);
        if (directory == null) {
            directory = getDirectory();
        }
        if (Objects.nonNull(directory)) {
            synchronized (this.propertySources) {
                this.propertySources.addAll(readConfiguration(directory));
            }
            Runnable runnable = new FileChangeListener(directory, this);
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
        List<PropertySource> result = new ArrayList<>();
        try {
            synchronized (propertySources) {
                for (Path path : Files.newDirectoryStream(directory, "*")) {
                    ConfigurationData data = loadFile(path);
                    if (data != null) {
                        result.addAll(getPropertySources(data));
                    }
                }
                return result;
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to read configuration from dir: " + directory, e);
        }
        return result;
    }

    protected Collection<PropertySource> getPropertySources(ConfigurationData data) {
        return Arrays.asList(new PropertySource[]{new FlattenedDefaultPropertySource(data)});
    }

    /**
     * Load a single file.
     *
     * @param file the file, not null.
     */
    protected ConfigurationData loadFile(Path file) {
        InputStream is = null;
        for (ConfigurationFormat format : formats) {
            try {
                URL url = file.toUri().toURL();
                if (format.accepts(url)) {
                    is = url.openStream();
                    ConfigurationData data = format.readConfiguration(file.toString(), is);
                    if (data != null) {
                        return data;
                    }
                }
            } catch (IOException e) {
                LOG.log(Level.INFO, "Error reading file: " + file.toString() +
                        ", using format: " + format, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ioe) {
                        LOG.log(Level.SEVERE, "Failed to rea data...", ioe);
                    }
                }
            }
        }
        LOG.warning("Error reading file: " + file.toString());
        return null;
    }


    /**
     * Evaluates the target directory from system property (tamaya.configdir) or classpath.
     *
     * @return the directory to be read, or null.
     */
    private Path getDirectory() {
        String absolutePath = System.getProperty("tamaya.configdir");
        if (Objects.nonNull(absolutePath)) {
            Path path = Paths.get(absolutePath);
            if (Files.isDirectory(path)) {
                return path;
            }
        }
        URL resource = ObservingPropertySourceProvider.class.getResource("/META-INF/config/");
        if (Objects.nonNull(resource)) {
            try {
                return Paths.get(resource.toURI());
            } catch (URISyntaxException e) {
                throw new ConfigException("An error to find the directory to watch", e);
            }
        }
        return null;
    }


    @Override
    public void directoryChanged(Path directory) {
        synchronized (this.propertySources) {
            List<PropertySource> existingPropertySources = new ArrayList<>(propertySources);
            propertySources.clear();
            Collection<PropertySource> sourcesRead = readConfiguration(directory);
            this.propertySources.addAll(sourcesRead);
            triggerConfigChange(existingPropertySources, propertySources);
        }
    }


    private void triggerConfigChange(List<PropertySource> originalPropertySources,
                                     List<PropertySource> newPropertySources) {
        ConfigurationContextChangeBuilder b = ConfigurationContextChangeBuilder.of();
        for (PropertySource ps : originalPropertySources) {
            b.removedPropertySource(ps);
        }
        for (PropertySource ps : newPropertySources) {
            b.newPropertySource(ps);
        }
        ConfigurationContextChange changeEvent = b.build();
        LOG.fine("Trigger Config Context Change: " + changeEvent);
        ConfigEvent.fireEvent(changeEvent);
    }

    @Override
    public Collection<PropertySource> getPropertySources() {
        synchronized (propertySources) {
            return new ArrayList<>(this.propertySources);
        }
    }
}