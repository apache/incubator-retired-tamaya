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
package org.apache.tamaya.core.spi;

import org.apache.tamaya.core.resource.Resource;

import java.util.Map;

/**
 * Implementations current this class encapsulate the mechanism how to read a
 * resource URI including interpreting the format correctly (e.g. xml vs.
 * properties).
 */
public interface ConfigurationFormat{

    /**
     * Returns a unique identifier that identifies each format.
     *
     * @return the unique format id, mever null.
     */
    public String getFormatName();

    /**
     * Check if the given {@link java.net.URI} and path xpression qualify that this format should be
     * able to read them, e.g. checking for compatible file endings.
     *
     * @param resource   the configuration location, not null
     * @return {@code true} if the given resource is in a format supported by
     * this instance.
     */
    boolean isAccepted(Resource resource);

    /**
     * Reads a {@link org.apache.tamaya.PropertySource} fromMap the given URI, using this format.
     *
     * @param resource    the configuration location, not null
     * @return the corresponding {@link java.util.Map}, never {@code null}.
     */
    Map<String,String> readConfiguration(Resource resource);

}
