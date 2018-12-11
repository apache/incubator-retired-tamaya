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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;

import java.util.Map;

/**
 * This interface allows to plugin different metadata mechanism. The default implementation
 * does load metadata information along the same property sources hierarchy as configuration.
 * MetaData entries are in the format {@code [(META)key].metakey=metavalue}. Alternate implementations can
 * choose whatever is appropriate, including loading metadata from external sources.
 */
public interface MetadataProvider {

    /**
     * Initializes the provider with the given context. Each context must manage it's own
     * provider instance.
     * @param context the target context, never null.
     * @return this instance, for chaining.
     */
    MetadataProvider init(ConfigurationContext context);

    /**
     * Access the current metadata for the given configuration context and key. The MetaData will be
     * accessible from {@link ConfigurationContext#getMetaData(String)}. Note that the metadata must not
     * to be cached by it's consumers, so caching/optimazitation is delegated to this implementation.
     * @param key the property key, not null.
     * @return the (immutable) metadata of this configuration context.
     */
    Map<String,String> getMetaData(String key);

    /**
     * Adds additional metadata. This metadata entries typically override all entries
     * from alternate sources.
     *
     * @param property the property key, not null.
     * @param key the metadata key, not null.
     * @param value the metadata value, not null.
     * @return this instance, for chaining.
     */
    MetadataProvider setMeta(String property, String key, String value);

    /**
     * Adds additional metadata. This metadata entries typically override all entries
     * from alternate sources.
     *
     * @param property the property key, not null.
     * @param metaData the metadata to set/replace.
     * @return this instance, for chaining.
     */
    MetadataProvider setMeta(String property, Map<String, String> metaData);

    /**
     * Resets metadata for a property, which means it reloads metadata based on the given context and
     *
     * param property the property key, not null.
     * @return this instance, for chaining.
     */
    MetadataProvider reset(String property);

    /**
     * Resets this instance, which means it reloads metadata based on the given context and
     *
     * @return this instance, for chaining.
     */
    MetadataProvider reset();

}
