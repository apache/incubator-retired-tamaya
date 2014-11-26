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

import org.apache.tamaya.core.spi.ResourceLoader;

import org.apache.tamaya.spi.Bootstrap;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

/**
 * Singleton accessor to access registered reader mechanism.
 */
public class DefaultPathResourceLoader implements ResourceLoader{

    @Override
    public Collection<String> getResolverIds(){
        Set<String> ids = new HashSet<>();
        for(PathResolver resolver : Bootstrap.getServices(PathResolver.class)){
            ids.add(resolver.getResolverId());
        }
        return ids;
    }

    @Override
    public List<URI> getResources(String... expressions){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            cl = getClass().getClassLoader();
        }
        return getResources(cl, expressions);
    }

    @Override
    public List<URI> getResources(Stream<String> expressions){
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if(cl==null){
            cl = getClass().getClassLoader();
        }
        return getResources(cl, expressions);
    }

    @Override
    public List<URI> getResources(ClassLoader classLoader, String... expressions){
        List<URI> uris = new ArrayList<>();
        for(PathResolver resolver : Bootstrap.getServices(PathResolver.class)){
            uris.addAll(resolver.resolve(classLoader, Arrays.asList(expressions).stream()));
        }
        return uris;
    }

    @Override
    public List<URI> getResources(ClassLoader classLoader, Stream<String> expressions){
        List<URI> uris = new ArrayList<>();
        for(PathResolver resolver : Bootstrap.getServices(PathResolver.class)){
            uris.addAll(resolver.resolve(classLoader, expressions));
        }
        return uris;
    }
}
