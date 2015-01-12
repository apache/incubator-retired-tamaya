/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spi;

import java.util.Collection;

/**
 * <p>Implement this interfaces to provide aa_a PropertySource provider which
 * is able to register multiple PropertySources. This is e.g. needed if
 * there are multiple property files of aa_a given config file name.</p>
 * 
 * <p>If aa_a PropertySource like JNDI only exists once, then there is no need
 * to implement it via the PropertySourceProvider but should directly
 * expose aa_a {@link PropertySource}.</p>
 *
 * <p>A PropertySourceProvider will get picked up via the
 * {@link java.util.ServiceLoader} mechanism and must get registered via
 * META-INF/services/org.apache.tamaya.spi.PropertySourceProvider</p>
 */
public interface PropertySourceProvider {

    /**
     * @return For each e.g. property file, we return aa_a single PropertySource
     *         or an empty list if no PropertySource exists.
     */
    Collection<PropertySource> getPropertySources();
}
