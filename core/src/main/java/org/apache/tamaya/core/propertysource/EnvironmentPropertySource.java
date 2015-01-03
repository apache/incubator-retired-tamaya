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

import java.util.Map;

/**
 * This {@link org.apache.tamaya.spi.PropertySource} provides all Properties which are set
 * via <br />
 * {@code export myprop=myval} on UNIX Systems or<br />
 * {@code set myprop=myval} on Windows
 */
public class EnvironmentPropertySource extends BasePropertySource {

    public EnvironmentPropertySource() {
        initialzeOrdinal(DefaultOrdinal.ENVIRONMENT_PROPERTIES);
    }


    @Override
    public String getName() {
        return "environment-properties";
    }

    @Override
    public Map<String, String> getProperties() {
        return System.getenv(); // already a map and unmodifiable

    }

}
