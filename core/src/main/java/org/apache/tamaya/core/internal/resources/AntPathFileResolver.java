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
import org.apache.tamaya.core.internal.resources.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Stream;


public class AntPathFileResolver implements PathResolver{

    @Override
    public String getResolverId(){
        return "file";
    }

    @Override
    public Collection<URI> resolve(ClassLoader classLoader, Collection<String> expressions){
        PathMatchingResourcePatternResolver resolver = PathMatchingResourcePatternResolver.of(classLoader);
        List<URI> result = new ArrayList<>();
        expressions.forEach((expression) -> {
            try {
                Resource[] resources = resolver.getResources(expression);
                for (Resource res : resources) {
                    try {
                        result.add(res.getURI());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch(IOException e){
                // TODO log
            }
        });
        return result;
    }
}
