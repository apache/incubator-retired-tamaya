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
package org.apache.tamaya.core.config;

import java.util.Optional;

import org.apache.tamaya.*;
import org.apache.tamaya.core.properties.AbstractPropertySource;
import old.PropertyAdapterProviderSpi;
import org.apache.tamaya.spi.ServiceContext;

/**
 * Abstract implementation class for {@link org.apache.tamaya.Configuration}, which supports optimistic
 * locking and mutability.
 */
public abstract class AbstractConfiguration extends AbstractPropertySource implements Configuration{

    private static final long serialVersionUID = 503764580971917964L;

    private final Object LOCK = new Object();

    protected AbstractConfiguration(String name){
        super(name);
    }


    @Override
    public <T> Optional<T> get(String key, Class<T> type){
        PropertyAdapterProviderSpi as = ServiceContext.getInstance().getSingleton(PropertyAdapterProviderSpi.class);
        PropertyAdapter<T> adapter = as.getPropertyAdapter(type);
        if(adapter == null){
            throw new ConfigException(
                    "Can not adapt config property '" + key + "' to " + type.getName() + ": no such " +
                            "adapter.");
        }
        return getAdapted(key, adapter);
    }

}
