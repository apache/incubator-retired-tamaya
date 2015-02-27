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

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Class that has the responsibility to watch the folder and then publish the changes to a
 * {@link org.apache.tamaya.events.delta.PropertySourceChange}.
 * @see ObservingPropertySourceProvider
 * This listener will wait to events and wait to one second to watch again.
 * <p>If new file was created or modified will commit from this file.</p>
 * <p>If a file was removed then the listener will load using all files left.</p>
 * @author otaviojava
 */
class FileChangeListener implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FileChangeListener.class.getName());

    private WatchService watchService;

    private FileChangeObserver observer;

    private Path directory;

    private volatile boolean running = true;

    public FileChangeListener(Path directory, FileChangeObserver observer) {
        this.observer = observer;
        this.directory = directory;
        this.watchService = getWatchService();

        if (Objects.nonNull(watchService) && Objects.nonNull(directory)) {
            try {
                directory.register(watchService, ENTRY_DELETE, ENTRY_MODIFY,
                        ENTRY_CREATE);
            } catch (IOException e) {
                throw new FileChangeListenerException("An error happened when does try to registry to watch the folder", e);
            }
        }
    }

    public void stopListener(){
        running = false;
    }

    @Override
    public void run() {
        if (Objects.isNull(watchService) || Objects.isNull(directory)) {
            return;
        }
        while (running) {
            watchFolder();
        }
    }


    private void watchFolder() {
        try {
            WatchKey watckKey = watchService.take();
            for (WatchEvent<?> event : watckKey.pollEvents()) {
                Path filePath = (Path) watckKey.watchable();
                if(event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)||
                        event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY) ||
                        event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE)){
                    LOGGER.info("File change detected in: " + filePath.getFileName());
                    observer.directoryChanged(filePath);
                }
            }
            watckKey.reset();
            Thread.sleep(1_000L);
        } catch (Exception e) {
            throw new FileChangeListenerException("An error happened when does try to watch the folder", e);
        }
    }


    private WatchService getWatchService() {
        try {
            FileSystem fileSystem = Paths.get(".").getFileSystem();
            return fileSystem.newWatchService();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "The file System does not supports WatchService", e);
            return null;
        }

    }

    static class FileChangeListenerException extends ConfigException {

        private static final long serialVersionUID = -8965486770881001513L;

        public FileChangeListenerException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}