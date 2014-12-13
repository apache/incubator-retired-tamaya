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

import org.apache.tamaya.*;
import org.apache.tamaya.core.config.ConfigurationFormats;
import org.apache.tamaya.core.properties.AbstractPropertySource;
import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.core.spi.ConfigurationFormat;
import org.apache.tamaya.core.resource.ResourceLoader;

import java.util.*;

/**
 * Created by Anatole on 16.10.2014.
 */
final class PathBasedPropertySource extends AbstractPropertySource {

    private List<String> paths = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private AggregationPolicy aggregationPolicy;

    public PathBasedPropertySource(MetaInfo metaInfo, Collection<String> paths, AggregationPolicy aggregationPolicy) {
        super(metaInfo);
        this.paths.addAll(Objects.requireNonNull(paths));
        this.aggregationPolicy = Objects.requireNonNull(aggregationPolicy);
        init();
    }

    @Override
    public Map<String, String> toMap() {
        return this.properties;
    }

    private void init() {
        List<String> sources = new ArrayList<>();
        List<String> effectivePaths = new ArrayList<>();
        paths.forEach((path) -> {
            effectivePaths.add(path);
            for (Resource res : Bootstrap.getService(ResourceLoader.class).getResources(path)) {
                ConfigurationFormat format = ConfigurationFormats.getFormat(res);
                if (format != null) {
                    try {
                        Map<String, String> read = format.readConfiguration(res);
                        sources.add(res.toString());
                        read.forEach((k, v) -> {
                            String valueToAdd = aggregationPolicy.aggregate(k,properties.get(k),v);
                            if(valueToAdd==null) {
                                properties.remove(k);
                            }
                            else{
                                properties.put(k, valueToAdd);
                            }
                        });
                    }
                    catch(ConfigException e){
                        throw e;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        metaInfo = MetaInfoBuilder.of(getMetaInfo())
                .setSourceExpressions(new String[effectivePaths.size()])
                .set("sources", sources.toString()).build();
    }
}
