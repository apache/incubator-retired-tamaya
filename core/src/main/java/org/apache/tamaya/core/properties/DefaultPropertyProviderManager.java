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
package org.apache.tamaya.core.properties;

import org.apache.tamaya.PropertyProvider;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPropertyProviderManager implements PropertyProviderManager{

    private Map<String,PropertyProvider> propertyProviders = new ConcurrentHashMap<>();

//    @Inject
//    private MetaModelItemFactoryManagerSpi metaModelFactory;

    @Override
    public PropertyProvider getConfigMap(String setId){
        PropertyProvider propertyMap = propertyProviders.get(setId);
//        if(propertyMap == null){
//            MetaModel.Specification<PropertyMap> desc =
//                    metaModel.getSpecification(PropertyMap.class, setId);
//            if(desc == null){
//                throw new IllegalArgumentException("No such property set: " + setId);
//            }
//            try{
//                propertyMap = createSet(desc);
//                this.propertyProviders.put(setId, propertyMap);
//            }
//            catch(Exception e){
//                throw new IllegalStateException("Error creating property set: " + setId, e);
//            }
//        }
        return propertyMap;
    }

//    private ConfigyMap createSet(MetaModel.Specification<PropertyMap> config)
//            throws ClassNotFoundException, IllegalAccessException, InstantiationException{
//        return metaModelFactory.create(config);
//    }

    @Override
    public void reloadConfigMap(String setId){
        PropertyProvider unit = propertyProviders.get(setId);
        if(unit != null){
            unit.load();
        }
    }


    @Override
    public Collection<String> getConfigMapKeys(){
        return this.propertyProviders.keySet();
    }


    @Override
    public boolean isConfigMapDefined(String key){
        // TODO check meta model, load if necessary
        return this.propertyProviders.containsKey(key);
    }
}
