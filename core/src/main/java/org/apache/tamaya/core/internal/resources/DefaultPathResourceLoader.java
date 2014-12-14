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
package org.apache.tamaya.core.internal.resources;

import org.apache.tamaya.core.spi.PathResolver;
import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.core.resource.ResourceLoader;

import org.apache.tamaya.spi.ServiceContext;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Singleton accessor to access registered reader mechanism.
 */
public class DefaultPathResourceLoader implements ResourceLoader{

    private static final Logger LOG = Logger.getLogger(DefaultPathResourceLoader.class.getName());

    @Override
    public Collection<String> getResolverIds(){
        return ServiceContext.getInstance().getServices(PathResolver.class).stream().map(PathResolver::getResolverId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Resource> getResources(String... expressions){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            cl = getClass().getClassLoader();
        }
        return getResources(cl, Arrays.asList(expressions));
    }

    @Override
    public List<Resource> getResources(Collection<String> expressions){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            cl = getClass().getClassLoader();
        }
        return getResources(cl, expressions);
    }

    @Override
    public List<Resource> getResources(ClassLoader classLoader, String... expressions) {
        return getResources(classLoader, Arrays.asList(expressions));
    }

    @Override
    public List<Resource> getResources(ClassLoader classLoader, Collection<String> expressions){
        List<Resource> resources = new ArrayList<>();
        for(PathResolver resolver : ServiceContext.getInstance().getServices(PathResolver.class)){
            try{
                resources.addAll(resolver.resolve(classLoader, expressions));
            }
            catch(Exception e){
                LOG.log(Level.FINEST, e, () -> "Resource not found: " + expressions.toString());
            }
        }
        return resources;
    }
}
