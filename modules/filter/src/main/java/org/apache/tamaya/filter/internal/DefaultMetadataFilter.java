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
package org.apache.tamaya.filter.internal;

import org.apache.tamaya.filter.ConfigurationFilter;
import org.apache.tamaya.spi.FilterContext;
import org.apache.tamaya.spi.PropertyFilter;

/**
 * Default property filter that hides metadta entries starting with an '_', similar ti {@code etcd}.
 */
public final class DefaultMetadataFilter implements PropertyFilter{
    @Override
    public String filterProperty(String valueToBeFiltered, FilterContext context) {
        if(context.isSinglePropertyScoped()){
            // When accessing keys explicitly, do not hide anything.
            return valueToBeFiltered;
        }
        if(ConfigurationFilter.THREADED_METADATA_FILTERED.get()) {
            if (context.getKey().startsWith("_")) {
                // Hide metadata entries.
                return null;
            }
        }
        return valueToBeFiltered;
    }
}
