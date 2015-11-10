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
package org.apache.tamaya.integration.camel;

import org.apache.camel.component.properties.PropertiesFunction;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Objects;


/**
 * Implementation of the Camel Properties SPI using Tamaya configuration.
 */
public class TamayaPropertyResolver implements PropertiesFunction{

    private final String prefix;

    /**
     * Creates a new instance.
     * @param configPrefix the prefix to be registered for explicit resolution by this resolver function, not null.
     */
    public TamayaPropertyResolver(String configPrefix){
        this.prefix = Objects.requireNonNull(configPrefix);
    }

    @Override
    public String getName() {
        return prefix;
    }

    @Override
    public String apply(String remainder) {
        Configuration config = ConfigurationProvider.getConfiguration();
        return config.get(remainder);
    }
}
