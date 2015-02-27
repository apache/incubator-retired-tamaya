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
import org.apache.tamaya.events.Listener;
import org.apache.tamaya.events.delta.ConfigurationContextChange;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Default Listener for ConfigurationContextChange events that updates the current context, if resources were
 * affected.
 */
public class DefaultConfigurationContextChangeListener implements Listener<ConfigurationContextChange> {

    private static final Logger LOG = Logger.getLogger(DefaultConfigurationContextChangeListener.class.getName());

    @Override
    public void onEvent(ConfigurationContextChange event) {
        ConfigurationContext context = ConfigurationProvider.getConfigurationContext();
        Collection<PropertySource> affectedPropertySources = context.getPropertySources(ps ->
                event.isAffected(ps));
        if(!affectedPropertySources.isEmpty()){
            ConfigurationContext newContext = context.toBuilder()
                    .removePropertySources(event.getRemovedPropertySources().stream()
                            .map(ps -> ps.getName()).collect(Collectors.toSet()))
                    .addPropertySources(event.getAddedPropertySources())
                    .addPropertySources(event.getUpdatedPropertySources())
                    .build();
            try {
                ConfigurationProvider.setConfigurationContext(newContext);
            }
            catch(Exception e){
                LOG.log(Level.INFO, "Failed to update the current ConfigurationContext due to config model changes", e);
            }
        }

    }
}
