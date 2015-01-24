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
package org.apache.tamaya.core.testdata;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.PropertyFilter;

import javax.annotation.Priority;

/**
 * Simple PropertyFilter that filters exact one value, registered using ServiceLoader.
 */
@Priority(200)
public class TestPropertyFilterRemoving implements PropertyFilter{
    @Override
    public String filterProperty(String key, String valueToBeFiltered) {
        if("name5".equals(key)){
            return null;
        }
        else if("name3".equals(key)){
            return "Mapped to name: " + ConfigurationProvider.getConfiguration().get("name");
        }
        return valueToBeFiltered;
    }
}
