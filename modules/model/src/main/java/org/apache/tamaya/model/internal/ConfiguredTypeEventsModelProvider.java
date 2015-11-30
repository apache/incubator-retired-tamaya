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

import org.apache.tamaya.model.ConfigModel;
import org.apache.tamaya.model.spi.ModelProviderSpi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Model provider that adds model definitions for the items published as
 * {@link org.apache.tamaya.inject.spi.ConfiguredType} events.
 */
public class ConfiguredTypeEventsModelProvider implements ModelProviderSpi {
    /** The collected models. */
    private static Collection<ConfigModel> configModels = new ArrayList<>();

    /**
     * Adds a model, called from the registered listener class.
     * @param configModel adds the config model.
     */
    static void addValidation(ConfigModel configModel){
        List<ConfigModel> newList = new ArrayList<>(configModels);
        newList.add(configModel);
        ConfiguredTypeEventsModelProvider.configModels = newList;
    }

    @Override
    public Collection<ConfigModel> getConfigModels() {
        return Collections.unmodifiableCollection(configModels);
    }
}
