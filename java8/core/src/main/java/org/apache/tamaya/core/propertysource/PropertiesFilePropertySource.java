/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.propertysource;

import org.apache.tamaya.core.internal.PropertiesFileLoader;

import java.net.URL;

/**
 * {@link org.apache.tamaya.spi.PropertySource} for properties-files
 */
public class PropertiesFilePropertySource extends PropertiesPropertySource {


    private String fileName;


    public PropertiesFilePropertySource(URL propertiesFile) {
        super(PropertiesFileLoader.load(propertiesFile));

        initializeOrdinal(DefaultOrdinal.FILE_PROPERTIES);
        this.fileName = propertiesFile.toExternalForm();
    }


    @Override
    public String getName() {
        return fileName;
    }

}
