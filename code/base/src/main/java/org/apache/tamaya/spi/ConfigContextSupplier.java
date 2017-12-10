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
package org.apache.tamaya.spi;

import jdk.nashorn.internal.objects.annotations.Function;

import javax.config.spi.ConfigSource;
import javax.config.spi.Converter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Central SPI for programmatically dealing with the setup of the configuration system.
 * This includes adding and enlisting {@link ConfigSource}s,
 * managing {@link Converter}s, ConfigFilters, etc.
 */
@FunctionalInterface
public interface ConfigContextSupplier {

    /**
     * Make an instance of a configuration accessible for use with Apache Tamaya specific extensions.
     * In most cases it should be sufficient to implement this interfance on your implementation of
     * {@link javax.config.Config}.
     *
     * @return the corresponding configuration context, never null.
     */
    ConfigContext getConfigContext();

}

