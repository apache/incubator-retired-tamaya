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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.internal.resources.io.UrlResource;
import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.core.config.ConfigurationFormat;

import java.net.URL;
import java.util.*;

/**
 * Created by Anatole on 16.10.2014.
 */
final class URLBasedPropertySource extends AbstractPropertySource {

	private static final long serialVersionUID = 648272283683183532L;
	private List<URL> resources = new ArrayList<>();
    private Map<String,String> properties = new HashMap<>();
    private AggregationPolicy aggregationPolicy;

    public URLBasedPropertySource(String name, List<URL> resources, AggregationPolicy aggregationPolicy) {
        super(name);
        this.resources.addAll(Objects.requireNonNull(resources));
        this.aggregationPolicy = Objects.requireNonNull(aggregationPolicy);
        init();
    }

    private void init(){
        List<String> sources = new ArrayList<>();
        for(URL url : resources){
            Resource res = new UrlResource(url);
            ConfigurationFormat format = ConfigurationFormat.from(res);
            if(format != null){
                try{
                    Map<String, String> read = format.readConfiguration(res);
                    sources.add(res.toString());
                    read.forEach((k, v) -> {
                        String newValue = aggregationPolicy.aggregate(k, properties.get(k), v);
                        if(newValue==null) {
                            properties.remove(k);
                        }
                        else {
                            properties.put(k, newValue);
                        }
                    });
                }
                catch(ConfigException e){
                    throw e;
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
//        MetaInfoBuilder metaInfoBuilder = MetaInfoBuilder.of(getMetaInfo());
//        metaInfo = metaInfoBuilder
//                .setSources(sources.toString()).build();
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }
}
