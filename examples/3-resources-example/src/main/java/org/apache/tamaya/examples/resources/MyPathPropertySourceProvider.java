package org.apache.tamaya.examples.resources;/*
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

import org.apache.tamaya.core.propertysource.SimplePropertySource;
import org.apache.tamaya.resource.AbstractPathPropertySourceProvider;
import org.apache.tamaya.spi.PropertySource;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Anatole on 20.03.2015.
 */
public class MyPathPropertySourceProvider extends AbstractPathPropertySourceProvider {

    public MyPathPropertySourceProvider(){
        super("cfgOther/**/*.properties", "META-INF/MyOtherConfigProperties.*");
    }

    @Override
    protected Collection<PropertySource> getPropertySources(URL url) {
        return Arrays.asList(new PropertySource[]{new SimplePropertySource(url)});
    }
}
