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
package org.apache.tamaya.metamodel.simple.internal;


import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.format.FlattenedDefaultPropertySource;
import org.apache.tamaya.resource.AbstractPathPropertySourceProvider;
import org.apache.tamaya.spi.PropertySource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Anatole on 20.03.2015.
 */
public class MetainfConfigPropertySourceProvider extends AbstractPathPropertySourceProvider {

    public MetainfConfigPropertySourceProvider() {
        super("classpath:META-INF/config/**/*.*");
    }

    @Override
    protected Collection<PropertySource> getPropertySources(URL url) {
        try {
            ConfigurationData config = ConfigurationFormats.readConfigurationData(url);
            return asCollection(new FlattenedDefaultPropertySource(config));
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                    "Failed to read configuration from " + url, e);
            return Collections.emptySet();
        }
    }

    private Collection<PropertySource> asCollection(PropertySource propertySource) {
        List<PropertySource> result = new ArrayList<>(1);
        result.add(propertySource);
        return result;
    }
}
