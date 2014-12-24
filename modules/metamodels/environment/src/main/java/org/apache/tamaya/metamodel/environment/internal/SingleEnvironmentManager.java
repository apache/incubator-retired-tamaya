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
package org.apache.tamaya.metamodel.environment.internal;


import org.apache.tamaya.metamodel.environment.Environment;
import org.apache.tamaya.metamodel.environment.EnvironmentBuilder;
import org.apache.tamaya.metamodel.environment.spi.EnvironmentProvider;
import org.apache.tamaya.metamodel.environment.spi.EnvironmentSpi;
import org.apache.tamaya.spi.ServiceContext;

import java.util.*;

/**
 * Service for accessing {@link org.apache.tamaya.metamodel.environment.Environment}. Environments are used to
 * access/determine configurations.<br/>
 * <h3>Implementation PropertyMapSpec</h3> This class is
 * <ul>
 * <li>thread safe,
 * <li>and behaves contextual.
 * </ul>
 */
public class SingleEnvironmentManager implements EnvironmentSpi {

    private final List<EnvironmentProvider> environmentProviders = loadEnvironmentProviders();
    private Environment rootEnvironment = getCurrentEnvironment();

    private List<EnvironmentProvider> loadEnvironmentProviders() {
        List<EnvironmentProvider> providerList = new ArrayList<>();
        for(EnvironmentProvider prov: ServiceContext.getInstance().getServices(EnvironmentProvider.class)){
            providerList.add(prov);
        }
        return providerList;
    }

    @Override
    public Environment getCurrentEnvironment(){
        EnvironmentBuilder b = EnvironmentBuilder.of();
        for(EnvironmentProvider prov: environmentProviders){
            if(prov.isActive()){
                if(prov.isActive()){
                    b.setAll(prov.getEnvironmentData());
                }
            }
        }
        return b.build();
    }

    @Override
    public Environment getRootEnvironment(){
        return rootEnvironment;
    }

}
