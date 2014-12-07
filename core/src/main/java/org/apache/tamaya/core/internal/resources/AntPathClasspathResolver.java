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

import org.apache.tamaya.core.internal.resources.io.PathMatchingResourcePatternResolver;
import org.apache.tamaya.core.spi.PathResolver;
import org.apache.tamaya.core.resource.Resource;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntPathClasspathResolver implements PathResolver {

    private static final Logger LOG = Logger.getLogger(AntPathClasspathResolver.class.getName());

    @Override
    public String getResolverId(){
        return "classpath";
    }

    @Override
    public Collection<Resource> resolve(ClassLoader classLoader, Collection<String> expressions){
        PathMatchingResourcePatternResolver resolver = PathMatchingResourcePatternResolver.of(classLoader);
        List<Resource> result = new ArrayList<>();
        expressions.forEach((expression) -> {
            try {
                Resource[] resources = resolver.getResources(expression);
                for (Resource res : resources) {
                    try {
                        result.add(res);
                    } catch (Exception e) {
                        LOG.log(Level.FINEST, "URI could not be extracted from Resource: " + res.toString(), e);
                    }
                }
            }
            catch(IOException e){
                LOG.log(Level.FINE, "Failed to load resource expression: " + expression, e);
            }
        });
        return result;
    }
}
