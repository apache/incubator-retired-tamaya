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
package org.apache.tamaya.model.internal;

import org.apache.tamaya.events.ConfigEvent;
import org.apache.tamaya.events.ConfigEventListener;
import org.apache.tamaya.inject.spi.ConfiguredField;
import org.apache.tamaya.inject.spi.ConfiguredMethod;
import org.apache.tamaya.inject.spi.ConfiguredType;
import org.apache.tamaya.model.ConfigModelManager;
import org.apache.tamaya.model.spi.ParameterModel;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * Internal facade that registers all kind of injected fields as {@link org.apache.tamaya.model.ConfigModel} entries,
 * so all configured injection points are visible as documented configuration hooks.
 */
public final class ConfiguredTypeEventsModelPopulator implements ConfigEventListener {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(ConfiguredTypeEventsModelPopulator.class.getName());

    /** System property to be set to deactivate auto documentation of configured classes published thorugh
     * ConfiguredType events.
     */
    private static final String ENABLE_EVENT_DOC = "org.apache.tamaya.model.autoModelEvents";

    @Override
    public void onConfigEvent(ConfigEvent event) {
        if(event.getResourceType()!=ConfiguredType.class){
            return;
        }
        String value = System.getProperty(ENABLE_EVENT_DOC);
        if(value == null || Boolean.parseBoolean(value)) {
            ConfiguredType confType = (ConfiguredType)event.getResource();
            for (ConfiguredField field : confType.getConfiguredFields()) {
                Collection<String> keys = field.getConfiguredKeys();
                for (String key : keys) {
                    ParameterModel val = ConfigModelManager.getModel(key, ParameterModel.class);
                    if (val == null) {
                        ConfiguredTypeEventsModelProvider.addConfigModel(new ParameterModel.Builder(key)
                                .setType(field.getType().getName())
                                .setDescription("Injected field: " +
                                        field.getAnnotatedField().getDeclaringClass().getName() + '.' + field.toString() +
                                        ", \nconfigured with keys: " + keys)
                                .build());
                    }
                }
            }
            for (ConfiguredMethod method : confType.getConfiguredMethods()) {
                Collection<String> keys = method.getConfiguredKeys();
                for (String key : keys) {
                    ParameterModel val = ConfigModelManager.getModel(key, ParameterModel.class);
                    if (val == null) {
                        ConfiguredTypeEventsModelProvider.addConfigModel(new ParameterModel.Builder(key)
                                .setType(method.getParameterTypes()[0].getName())
                                .setDescription("Injected field: " +
                                        method.getAnnotatedMethod().getDeclaringClass().getName() + '.' + method.toString() +
                                        ", \nconfigured with keys: " + keys)
                                .build());
                    }
                }
            }
        }
    }


}
