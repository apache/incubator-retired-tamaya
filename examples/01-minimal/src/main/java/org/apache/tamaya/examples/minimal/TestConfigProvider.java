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
package org.apache.tamaya.examples.minimal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.internal.DefaultConfigurationProvider;

/**
 * Configuration provider that allows to set and reset a configuration
 * different per thread.
 */
public class TestConfigProvider extends DefaultConfigurationProvider{

    private ThreadLocal<Configuration> threadedConfig = new ThreadLocal<>();

    @Override
    public Configuration getConfiguration() {
        Configuration config = threadedConfig.get();
        if(config!=null){
            return config;
        }
        return super.getConfiguration();
    }

    @Override
    public void setConfiguration(Configuration config) {
        if(config==null){
            threadedConfig.remove();
        }else {
            threadedConfig.set(config);
        }
    }
}
