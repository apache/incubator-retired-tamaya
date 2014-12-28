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

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

/**
 *  This implementation run in a folder and once found xml and properties files
 *  will create the Configuration, when one file is created, deleted or modified the configuration will commit
 *  automatically.
 * The default folder is META-INF/configuration, but you can change using the absolute path in
 * "-Dtamaya.configbase" parameter.
 * @author otaviojava
 */
public class FilesPropertiesConfigProvider implements ConfigurationProviderSpi, FileChangeObserver {

    private static final String DEFAULT_CONFIG_NAME = "files.configuration";

    private Map<String, String> configurationMap = Collections.emptyMap();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private List<FileChangeObserver> fileChangeObservers = new ArrayList<>();

    public FilesPropertiesConfigProvider() {
        Path directory = getDirectory();
        if (Objects.nonNull(directory)) {
            configurationMap = new FileReader().runFiles(directory);
            Runnable runnable = new FileChangeListener(this, configurationMap, directory);
            executor.execute(runnable);
        } else {
            executor.shutdown();
        }
    }

    @Override
    public String getConfigName() {
        return DEFAULT_CONFIG_NAME;
    }

    private Path getDirectory() {
            String absolutePath = System.getProperty("tamaya.configbase");

            if(Objects.nonNull(absolutePath)) {
                Path path = Paths.get(absolutePath);
                if(Files.isDirectory(path)) {
                    return path;
                }
            }

            URL resource = FilesPropertiesConfigProvider.class.getResource("/META-INF/configuration/");
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
    public Configuration getConfiguration() {
      return new FileConfiguration(Collections.unmodifiableMap(configurationMap));
    }

    @Override
    public void reload() {
        Path directory = getDirectory();
        if (Objects.nonNull(directory)) {
            configurationMap = new FileReader().runFiles(directory);
        }
    }

    @Override
    public void update(Map<String, String> configurationMap) {
        synchronized (this) {
            this.configurationMap = configurationMap;
            Map<String, String> unmodifiableMap = Collections.unmodifiableMap(configurationMap);
            fileChangeObservers.forEach(fi -> fi.update(unmodifiableMap));
        }
    }
}