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

import java.io.InputStream;
import java.net.URL;

/**
 * Implementations current this class encapsulate the mechanism how to read a
 * resource including interpreting the format correctly (e.g. xml vs.
 * properties vs. ini). In most cases file only contains entries of the same priority, which would then
 * result in only one {@link org.apache.tamaya.spi.PropertySource}. Complex file formats, however, may contain entries
 * of different priorities. In this cases, each ordinal type found typically is returned as a separate section so the
 * consuming {@link org.apache.tamaya.spi.PropertySourceProvider} implementation can distribute the different part to
 * individual {@link org.apache.tamaya.spi.PropertySource}s.<p>
 * <h3>Implementation Requirements</h3>
 * Implementations of this type must be
 * <ul>
 *     <li>thread-safe</li>
 * </ul>
 */
public interface ConfigurationFormat {

    /**
     * Allows the format to examine the given resource, e.g. for a matching file ending. Only, if a format accepts an
     * URL, it will be tried for reading the configuration.
     * @param url the url to read the configuration data from (could be a file, a remote location, a classpath
     *            resource or something else, not null.
     * @return true, if this format accepts the given URL for reading.
     */
    boolean accepts(URL url);

    /**
     * Reads a configuration from an URL, hereby parsing the given {@link java.io.InputStream}. Dependening on
     * the capabilities of the format the returned {@link org.apache.tamaya.format.ConfigurationData} may contain
     * different levels of data:
     * <ul>
     *     <li>Only a <i>default</i> section is returned, since the configuration format does not support
     *     hierarchies. This is the case for properties and xml properties.</li>
     *     <li>Hierarchical formats such as INI, XML and JSON can map each node to a section. Each section
     *     can have its own key/value pairs. This allows to map also complex formats in a generic way. A
     *     format implementation should then additionally flatten the whole data and store it in a section
     *     identified by {@link ConfigurationData#FLATTENED_SECTION_NAME}. This allows to use the flattened
     *     section as inout to a default mapping, which is always appropriate as long as no other semnatics
     *     are defined in the concrete target scenario.</li>
     *     <li>More complex custom scenarios should map their configuration data read into different
     *     sections. Typically the data will be mapped into different {@link org.apache.tamaya.spi.PropertySource}
     *     instances with different ordinal levels. As an example imagine a custom format that contains sections
     *     'defaults', 'global-defaults', 'application', 'server-overrides'.</li>
     *     <li>Alternate formats</li>
     * </ul>
     *
     * Summarizing implementations common formats should always provide
     * <ul>
     *     <li>the data organized in sections as useful for the given format. If data is organized in one section, it
     *     should be mapped into the DEFAULT section.</li>
     *     <li>Formats that do provide multiple sections, should always provide a FLATTENED section as well, where
     *     all the data is organized as a flattened key/value pairs, enabling a generic mapping to a
     *     {@link org.apache.tamaya.spi.PropertySource}.</li>
     * </ul>
     *
     * If the configuration format only contains entries of one ordinal type, normally only one single
     * instance of PropertySource is returned (the corresponding key/values should end up in the DEFAULT section).
     * Nevertheless custom formats may contain different sections or parts,
     * where each part maps to a different target ordinal (eg defaults, domain config and app config). In the
     * ladder case multiple PropertySources can be returned, each one with its own ordinal and the corresponding
     * entries.
     * @see org.apache.tamaya.spi.PropertySource
     * @param resource a descriptive name for the resource, since an InputStream does not have any)
     * @param inputStream the inputStream to read the configuration data from (could be a file, a remote location, a classpath
     *            resource or something else.
     * @return the corresponding {@link ConfigurationData} containing sections/properties read, never {@code null}.
     * @throws org.apache.tamaya.ConfigException if parsing of the input fails.
     */
    ConfigurationData readConfiguration(String resource, InputStream inputStream);

    //X TODO Add support to access a default format to see a correct formatting
    //X String getFormatExample();

}
