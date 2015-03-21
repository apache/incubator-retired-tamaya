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
package org.apache.tamaya.format;

import org.apache.tamaya.spi.ServiceContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Small accessor and management class dealing with {@link org.apache.tamaya.format.ConfigurationFormat}
 * instances.
 */
public final class ConfigurationFormats {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(ConfigurationFormats.class.getName());

    /**
     * Singleton constructor.
     */
    private ConfigurationFormats() {
    }

    /**
     * Get all currently available formats, ordered by priority.
     *
     * @return the currently available formats, never null.
     */
    public static List<ConfigurationFormat> getFormats() {
        return ServiceContext.getInstance().getServices(ConfigurationFormat.class);
    }

    /**
     * Get all currently available formats, ordered by priority.
     *
     * @return the currently available formats, never null.
     */
    public static List<ConfigurationFormat> getFormats(String... formatNames) {
        Set<String> names = new HashSet<>(Arrays.asList(formatNames));
        return getFormats(f -> names.contains(f));
    }

    /**
     * Get all currently available formats, ordered by priority.
     *
     * @return the currently available formats, never null.
     */
    public static List<ConfigurationFormat> getFormats(Predicate<String> namePredicate) {
        return getFormats().stream().filter(f -> namePredicate.test(f.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Get all currently available formats, ordered by priority.
     *
     * @return the currently available formats, never null.
     */
    public static List<ConfigurationFormat> getFormats(URL url) {
        List<ConfigurationFormat> formats = getFormats();
        return formats.stream().filter(f -> f.accepts(url)).collect(Collectors.toList());
    }

    /**
     * Tries to read configuration data from a given URL, hereby traversing all known formats in order of precedence.
     * Hereby the formats are first filtered to check if the URL is acceptable, before the input is being parsed.
     *
     * @param url the url from where to read, not null.
     * @return the ConfigurationData read, or null.
     * @throws IOException if the resource cannot be read.
     */
    public static ConfigurationData readConfigurationData(final URL url) throws IOException{
        List<ConfigurationFormat> formats = getFormats(url);
        return readConfigurationData(url, formats.toArray(new ConfigurationFormat[formats.size()]));
    }

    /**
     * Tries to read configuration data from a given URL, hereby explicitly trying all given formats in order.
     *
     * @param url     the url from where to read, not null.
     * @param formats the formats to try.
     * @return the ConfigurationData read, or null.
     * @throws IOException if the resource cannot be read.
     */
    public static ConfigurationData readConfigurationData(URL url, ConfigurationFormat... formats) throws IOException {
        return readConfigurationData(url.toString(), url.openStream(), formats);
    }


    /**
     * Tries to read configuration data from a given URL, hereby explicitly trying all given formats in order.
     * @param resource a descriptive name for the resource, since an InputStream does not have any)
     * @param inputStream the inputStream from where to read, not null.
     * @param formats the formats to try.
     * @return the ConfigurationData read, or null.
     * @throws IOException if the resource cannot be read.
     */
    public static ConfigurationData readConfigurationData(String resource, InputStream inputStream,
                                                          ConfigurationFormat... formats) throws IOException {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(resource);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[256];
        try{
            int read = inputStream.read(bytes);
            while(read > 0){
                bos.write(bytes, 0, read);
                read = inputStream.read(bytes);
            }
        } finally{
            try {
                inputStream.close();
            } catch (IOException e) {
                LOG.log(Level.FINEST, e, () -> "Error closing stream: " + inputStream);
            }
        }
        ConfigurationData data;
        for (ConfigurationFormat format : formats) {
            try(ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray())) {
                data = format.readConfiguration(resource, bis);
                if (data != null) {
                    return data;
                }
            } catch (Exception e) {
                LOG.log(Level.INFO, e,
                        () -> "Format "+format.getClass().getName()+" failed to read resource " + resource);
            }
        }
        return null;
    }

}
