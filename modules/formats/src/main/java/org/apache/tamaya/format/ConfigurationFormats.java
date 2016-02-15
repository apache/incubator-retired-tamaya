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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tamaya.spi.ServiceContextManager;

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
        return ServiceContextManager.getServiceContext().getServices(ConfigurationFormat.class);
    }

    /**
     * Get all currently available formats, ordered by priority.
     *
     * @param formatNames available formats to be ordered.
     * @return the currently available formats, never null.
     */
    public static List<ConfigurationFormat> getFormats(String... formatNames) {
        final List<ConfigurationFormat> result = new ArrayList<>();
        final Set<String> names = new HashSet<>(Arrays.asList(formatNames));
        for (final ConfigurationFormat f : getFormats()) {
            if (names.contains(f.getName())) {
                result.add(f);
            }
        }
        return result;
    }

    // Activate for JDK 8...
//    /**
//     * Get all currently available formats, ordered by priority.
//     *
//     * @return the currently available formats, never null.
//     */
//    public static List<ConfigurationFormat> getFormats(Predicate<String> namePredicate) {
//        List<ConfigurationFormat> result = new ArrayList<>();
//        for(ConfigurationFormat f:getFormats()){
//            if(namePredicate.test(f.getName()){
//                result.add(f);
//            }
//        }
//        return result;
//    }

    /**
     * Get all currently available formats, ordered by priority.
     *
     * @param url source to read configuration from.
     * @return the currently available formats, never null.
     */
    public static List<ConfigurationFormat> getFormats(final URL url) {
        final List<ConfigurationFormat> formats = getFormats();
        final List<ConfigurationFormat> result = new ArrayList<>();
        for (final ConfigurationFormat f : formats) {
            if (f.accepts(url)) {
                result.add(f);
            }
        }
        return result;
    }

    /**
     * Tries to read configuration data from a given URL, hereby traversing all known formats in order of precedence.
     * Hereby the formats are first filtered to check if the URL is acceptable, before the input is being parsed.
     *
     * @param url the url from where to read, not null.
     * @return the ConfigurationData read, or null.
     * @throws IOException if the resource cannot be read.
     */
    public static ConfigurationData readConfigurationData(final URL url) throws IOException {
        final List<ConfigurationFormat> formats = getFormats(url);
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
     * @param urls    the urls from where to read, not null.
     * @param formats the formats to try.
     * @return the {@link org.apache.tamaya.format.ConfigurationData} of the files successfully decoded by the
     * given formats.
     */
    public static Collection<ConfigurationData> getPropertySources(Collection<URL> urls, ConfigurationFormat... formats) {
        final List<ConfigurationData> dataRead = new ArrayList<>();
        for (final URL url : urls) {
            try {
                final ConfigurationData data = readConfigurationData(url, formats);
                if (data != null) {
                    dataRead.add(data);
                }
            } catch (final Exception e) {
                LOG.log(Level.SEVERE, "Error reading file: " + url.toExternalForm(), e);
            }
        }
        return dataRead;
    }

    /**
     * Tries to read configuration data from a given URL, hereby explicitly trying all given formats in order.
     *
     * @param resource    a descriptive name for the resource, since an InputStream does not have any
     * @param inputStream the inputStream from where to read, not null.
     * @param formats     the formats to try.
     * @return the ConfigurationData read, or null.
     * @throws IOException if the resource cannot be read.
     */
    public static ConfigurationData readConfigurationData(String resource, InputStream inputStream,
                                                          ConfigurationFormat... formats) throws IOException {
        Objects.requireNonNull(inputStream);
        Objects.requireNonNull(resource);
        try(InputStreamFactory isFactory = new InputStreamFactory(inputStream)) {
            for (final ConfigurationFormat format : formats) {
                try (InputStream is = isFactory.createInputStream()) {
                    final ConfigurationData data = format.readConfiguration(resource, is);
                    if (data != null) {
                        return data;
                    }
                } catch (final Exception e) {
                    LOG.log(Level.INFO,
                            "Format " + format.getClass().getName() + " failed to read resource " + resource, e);
                }
            }
        }
        return null;
    }


}
