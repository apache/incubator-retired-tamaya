package org.apache.tamaya.core.internal.config;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.spi.ConfigurationProviderSpi;

/**
 * Class that has the responsibility to watch the folder and then commit the {@link ConfigurationProviderSpi}
 * to commit the Configuration from the properties or xml files, another ones will be ignored.
 * @see FilesPropertiesConfigProvider
 * This listener will wait to events and wait to one second to watch again.
 * <p>If new file was created or modified will commit from this file.</p>
 * <p>If a file was removed then the listener will load using all files left.</p>
 * @author otaviojava
 */
class FileChangeListener implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(FileChangeListener.class);

    private WatchService watchService;

    private FileChangeObserver observer;

    private Map<String, String> configurationMap;

    private Path directory;

    private FileReader fileReader = new FileReader();

    public FileChangeListener(FileChangeObserver observer, Map<String, String> mapConfiguration, Path directory) {
        this.observer = observer;
        this.configurationMap = mapConfiguration;
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


    @Override
    public void run() {
        if (Objects.isNull(watchService) || Objects.isNull(directory)) {
            return;
        }
        while (true) {
            watchFolder();
        }
    }


    private void watchFolder() {
        try {
            WatchKey watckKey = watchService.take();
            boolean needUpdate = false;
            for (WatchEvent<?> event : watckKey.pollEvents()) {
                Path keyDirectory = (Path) watckKey.watchable();
                if(listenerPath(event, keyDirectory)) {
                    needUpdate = true;
                }
            }

            if (needUpdate) {
                observer.update(configurationMap);
            }
            watckKey.reset();
            Thread.sleep(1_000L);
        } catch (Exception e) {
            throw new FileChangeListenerException("An error happened when does try to watch the folder", e);
        }
    }

    private boolean listenerPath(WatchEvent<?> event, Path keyDirectory) {
        boolean wasModified = false;
        Path path = keyDirectory.resolve((Path)event.context());
        if(fileReader.isObservavleFile(path)) {

            if (event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY) {
                wasModified = true;
                configurationMap.putAll(fileReader.runFile(path.toAbsolutePath()));
                LOGGER.info("An event was detected  in file: " + path.getFileName());
            }

            if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                wasModified = true;
                configurationMap = fileReader.runFiles(directory);
                LOGGER.info("A remotion event was detected  in file: " + path.getFileName());
            }

        } else {
            LOGGER.info("Ignoring the file: " +  path.getFileName() + " because is not a properties or xml file");
        }
        return wasModified;
    }

    private WatchService getWatchService() {
        try {
            FileSystem fileSystem = Paths.get(".").getFileSystem();
            return fileSystem.newWatchService();
        } catch (IOException e) {
            LOGGER.warn("This file System does not supports WatchService", e);
            return null;
        }

    }

    class FileChangeListenerException extends ConfigException {

        private static final long serialVersionUID = -8965486770881001513L;

        public FileChangeListenerException(String message, Throwable cause) {
            super(message, cause);
        }

    }
}