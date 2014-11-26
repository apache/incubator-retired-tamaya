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

import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.core.config.ConfigurationFormats;
import org.apache.tamaya.spi.Bootstrap;
import org.apache.tamaya.core.spi.ConfigurationFormat;

import java.net.URI;
import java.util.*;

/**
 * Created by Anatole on 16.10.2014.
 */
final class URIBasedPropertyProvider extends AbstractPropertyProvider {

    private List<URI> uris = new ArrayList<>();
    private Map<String,String> properties = new HashMap<>();

    public URIBasedPropertyProvider(MetaInfo metaInfo, Collection<URI> uris) {
        super(metaInfo);
        Objects.requireNonNull(uris);
        this.uris.addAll(uris);
        init();
    }

    private void init(){
        List<String> sources = new ArrayList<>();
        for(URI uri : uris){
            ConfigurationFormat format = ConfigurationFormats.getFormat(uri);
            if(format != null){
                try{
                    properties.putAll(format.readConfiguration(uri));
                    sources.add(uri.toString());
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        MetaInfoBuilder metaInfoBuilder = MetaInfoBuilder.of(getMetaInfo());
        super.metaInfo = metaInfoBuilder
                .setSources(sources.toString()).build();
    }

    @Override
    public Map<String, String> toMap() {
        return properties;
    }
}
