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

import org.apache.tamaya.spi.PropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Implementations current this class encapsulate the mechanism how to read a
 * resource including interpreting the format correctly (e.g. xml vs.
 * properties). In most cases file only contains entries of the same priority, which would then
 * result in only one {@link PropertySource}. Complex file formats, hoiwever, may contain entries
 * of different priorities. In this cases, each ordinal type found must be returned as a separate
 * {@link PropertySource} instance.
 */
@FunctionalInterface
public interface ConfigurationFormat {

    /**
     * Reads a list {@link org.apache.tamaya.spi.PropertySource} instances from a resource, using this format.
     * If the configuration format only contains entries of one ordinal type, normally only one single
     * instance of PropertySource is returned. Nevertheless custom formats may contain different sections or parts,
     * where each part maps to a different target rdinal (eg defaults, domain config and app config). In the
     * ladder case multiple PropertySources can be returned, each one with its own ordinal and the corresponding
     * entries.
     *
     * @param sourceName name to be used for constructing a useful name for the created
     *                   {@link org.apache.tamaya.spi.PropertySource} instances.
     * @param streamSupplier   the resource represented by a supplier of InputStream, not null
     * @return the corresponding {@link org.apache.tamaya.spi.PropertySource} instances, never {@code null}.
     */
    Collection<PropertySource> readConfiguration(String sourceName, Supplier<InputStream> streamSupplier)
            throws IOException;

}
