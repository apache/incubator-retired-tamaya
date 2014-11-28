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
package org.apache.tamaya;

import org.apache.tamaya.spi.EnvironmentManagerSingletonSpi;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Anatole on 12.09.2014.
 */
public class TestEnvironmentManagerSingleton implements EnvironmentManagerSingletonSpi{
    @Override
    public Environment getEnvironment(){
        return null;
    }

    @Override
    public Environment getRootEnvironment(){
        return null;
    }

    @Override
    public Optional<Environment> getEnvironment(String environmentType, String contextId) {
        return null;
    }

    @Override
    public Set<String> getEnvironmentContexts(String environmentType) {
        return Collections.emptySet();
    }

    @Override
    public List<String> getEnvironmentTypeOrder() {
        return Collections.emptyList();
    }
}
