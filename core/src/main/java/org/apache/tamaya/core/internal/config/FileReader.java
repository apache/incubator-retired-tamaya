package org.apache.tamaya.core.internal.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class to read a file and creates a {@link Map} of {@link String}.
 * The implementation of {@link Map} will {@link HashMap}
 * @author otaviojava
 */
class FileReader {

    private static final String EXTENSIONS_ACCEPTED = "*.{xml,properties}";

    public Map<String, String> runFiles(Path directory) {
        Properties properties = createProperties(directory);
        return properties
                .stringPropertyNames()
                .stream()
                .collect(
                        Collectors.toMap(Function.identity(),
                                properties::getProperty));
    }

    public Map<String, String> runFile(Path path) {
        Properties properties = new Properties();
        try {
            loadFile(properties, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties
                .stringPropertyNames()
                .stream()
                .collect(
                        Collectors.toMap(Function.identity(),
                                properties::getProperty));
    }

    private Properties createProperties(Path directory) {
        Properties properties = new Properties();

            try {
                for (Path path : Files.newDirectoryStream(directory, EXTENSIONS_ACCEPTED)) {
                    loadFile(properties, path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        return properties;
    }

    private void loadFile(Properties properties, Path path) throws IOException,
            InvalidPropertiesFormatException {
        if (isXmlExtension(path)) {
            properties.loadFromXML(Files.newInputStream(path));
        } else {
            properties.load(Files.newInputStream(path));
        }
}

    private boolean isXmlExtension(Path path) {
        return path.toString().toLowerCase().endsWith(".xml");
    }

    private boolean isPropertiesExtension(Path path) {
        return path.toString().toLowerCase().endsWith(".properties");
    }

    public boolean isObservavleFile(Path path) {
        return isPropertiesExtension(path) || isXmlExtension(path);
    }
}