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
package org.apache.tamaya.environment.internal;


import org.apache.tamaya.ConfigException;
import org.apache.tamaya.environment.RuntimeContext;
import org.apache.tamaya.environment.RuntimeContextBuilder;
import org.apache.tamaya.environment.spi.ContextProviderSpi;
import org.apache.tamaya.spi.ServiceContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for accessing {@link org.apache.tamaya.environment.RuntimeContext}. Environments are used to
 * access/determine configurations.<br/>
 * <h3>Implementation PropertyMapSpec</h3> This class is
 * <ul>
 * <li>thread safe,
 * <li>and behaves contextual.
 * </ul>
 */
public class SingleEnvironmentManager implements org.apache.tamaya.environment.spi.ContextSpi {

    private final List<ContextProviderSpi> environmentProviders = loadEnvironmentProviders();

    private List<ContextProviderSpi> loadEnvironmentProviders() {
        List<ContextProviderSpi> providerList = new ArrayList<>();
        for (ContextProviderSpi prov : ServiceContext.getInstance().getServices(ContextProviderSpi.class)) {
            providerList.add(prov);
        }
        return providerList;
    }

    @Override
    public RuntimeContext getCurrentContext() {
        RuntimeContextBuilder builder = RuntimeContextBuilder.of("unknown");
        for (ContextProviderSpi prov : environmentProviders) {
            prov.setupContext(builder);
        }
        return builder.build();
    }

}
