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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class AntPathClasspathsResolver implements PathResolver{

    @Override
    public String getResolverId(){
        return "classpath*";
    }

    @Override
    public Collection<URI> resolve(ClassLoader classLoader, Stream<String> expressions){
        List<URI> result = new ArrayList<>();
        Objects.requireNonNull(classLoader);
        expressions.forEach((expression) -> {
            if(expression.startsWith("classpath*:")){
                String exp = expression.substring("classpath*:".length());
                Enumeration<URL> urls;
                try{
                    urls = classLoader.getResources(exp);
                    while(urls.hasMoreElements()){
                        URL url = (URL) urls.nextElement();
                        try{
                            result.add(url.toURI());
                        }
                        catch(URISyntaxException e){
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                catch(IOException e1){
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        return result;
    }
}
