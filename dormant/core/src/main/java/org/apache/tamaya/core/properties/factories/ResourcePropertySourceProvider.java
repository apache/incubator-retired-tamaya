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

import java.io.IOException;
import java.io.InputStream;
import java.lang.Override;
import java.lang.String;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourcePropertySourceProvider implements PropertySourceProvider {

	private static final Logger LOG = Logger.getLogger(ResourcePropertySourceProvider.class.getName());

    private int baseOrdinal;

	private Collection<PropertySource> propertySources;

    private Resource resource;

	public ResourcePropertySourceProvider(int baseOrdinal, String baseName, Resource resource) {
        this.resource = Objects.requireNonNull(resource);
        this.baseOrdinal = baseOrdinal;
        List<ConfigFormat> formats = ConfigFormat.getFormats(resource);
        for(ConfigFormat format: formats){
            try{
                propertySources = format.readConfiguration(baseOrdinal, baseName, resource);
            }
            catch(Exception e){
                LOG.info(() -> "Format was not matching: " + format.getFormatName() + " for resource: " + resource.getName());
            }
        }
	}

	public Resource getResource(){
        return this.resource;
    }


    @Override
    public String toString() {
        return "ResourcePropertySourceProvider{" +
                "resource=" + resource +
                ", propertySources=" + propertySources +
                '}';
    }

}
