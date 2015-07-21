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
package org.apache.tamaya.events.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.events.ConfigEventListener;
import org.apache.tamaya.events.delta.ConfigurationContextChange;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertySource;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default ConfigEventListener for ConfigurationContextChange events that updates the current context, if resources were
 * affected.
 */
public class DefaultConfigurationContextChangeListener implements ConfigEventListener<ConfigurationContextChange> {

    private static final Logger LOG = Logger.getLogger(DefaultConfigurationContextChangeListener.class.getName());

    @Override
    public void onConfigEvent(ConfigurationContextChange event) {
        ConfigurationContext context = ConfigurationProvider.getConfigurationContext();
        List<PropertySource> affectedPropertySources = new ArrayList<>();
        for (PropertySource ps : context.getPropertySources()) {
            if (event.isAffected(ps)) {
                affectedPropertySources.add(ps);
            }
        }
        ConfigurationContextBuilder newContextBuilder = ConfigurationProvider.getConfigurationContextBuilder()
                .setContext(context);
        if (!affectedPropertySources.isEmpty()) {
            Set<String> propertySourceNames = new HashSet<>();
            for (PropertySource removed : event.getRemovedPropertySources()) {
                propertySourceNames.add(removed.getName());
            }
            newContextBuilder.removePropertySources(propertySourceNames);
        }
        newContextBuilder.addPropertySources(event.getAddedPropertySources());
        newContextBuilder.addPropertySources(event.getUpdatedPropertySources());
        ConfigurationContext newContext = newContextBuilder.build();
        try {
            ConfigurationProvider.setConfigurationContext(newContext);
        } catch (Exception e) {
            LOG.log(Level.INFO, "Failed to update the current ConfigurationContext due to config model changes", e);
        }
    }
}
