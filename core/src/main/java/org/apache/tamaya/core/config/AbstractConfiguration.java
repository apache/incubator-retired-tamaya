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

import org.apache.tamaya.*;
import org.apache.tamaya.core.properties.AbstractPropertySource;
import org.apache.tamaya.core.spi.AdapterProviderSpi;
import org.apache.tamaya.spi.ServiceContext;

import java.util.*;
import java.util.logging.Logger;

/**
 * Abstract implementation class for {@link org.apache.tamaya.Configuration}, which supports optimistic
 * locking and mutability.
 */
public abstract class AbstractConfiguration extends AbstractPropertySource implements Configuration{

    private static final Logger LOG = Logger.getLogger(AbstractConfiguration.class.getName());

    private static final long serialVersionUID = 503764580971917964L;

    private final Object LOCK = new Object();

    private String version = UUID.randomUUID().toString();

    protected AbstractConfiguration(MetaInfo metaInfo){
        super(metaInfo);
    }


    @Override
    public <T> Optional<T> get(String key, Class<T> type){
        AdapterProviderSpi as = ServiceContext.getInstance().getSingleton(AdapterProviderSpi.class);
        Codec<T> adapter = as.getAdapter(type);
        if(adapter == null){
            throw new ConfigException(
                    "Can not deserialize config property '" + key + "' to " + type.getName() + ": no such " +
                            "adapter.");
        }
        return getAdapted(key, adapter);
    }

    @Override
    public String getVersion(){
        return version;
    }

    /**
     * This method reloads the content current this PropertyMap by reloading the contents delegate.
     */
    protected ConfigChangeSet reload(){ return ConfigChangeSet.emptyChangeSet(this);}

    /**
     * This method reloads the content current this PropertyMap by reloading the contents delegate.
     */
    @Override
    public ConfigChangeSet load(){
        Configuration oldState;
        Configuration newState;
        ConfigChangeSet changeSet = null;
        synchronized(LOCK) {
            oldState = FreezedConfiguration.of(this);
            reload();
            newState = FreezedConfiguration.of(this);
            changeSet = ConfigChangeSetBuilder.of(oldState).addChanges(newState).build();
        }
        if(changeSet.isEmpty()){
            return ConfigChangeSet.emptyChangeSet(this);
        }
        this.version = UUID.randomUUID().toString();
        Configuration.publishChange(changeSet);
        return changeSet;
    }

}
