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
package org.apache.tamaya.dsl.internal;

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.staged.spi.DSLPropertySourceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * DSL provider implementation that allows to use registered {@link PropertySource} instances
 * by matching {@link PropertySource#getName()} with the configured value and overriding the
 * {@link PropertySource#getOrdinal()} method.
 */
public class NamedDSLPropertySourceProvider implements DSLPropertySourceProvider{

    private static final Logger LOG = Logger.getLogger(NamedDSLPropertySourceProvider.class.getName());

    @Override
    public List<PropertySource> resolve(String sourceExpression, Map<String, PropertySource> defaultPropertySources) {
        List<PropertySource> result = new ArrayList<>();
        PropertySource ps = defaultPropertySources.get(sourceExpression);
        if(ps==null){
            LOG.warning("No such property source provider found: " + sourceExpression);
        }
        result.add(ps);
        return result;
    }

    @Override
    public String getKey() {
        return "named:";
    }
}
