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

import org.apache.tamaya.PropertyProvider;
import java.util.Collection;

/**
 * Service for accessing configuration. A configuration service is always base
 * on the environment definition provided by one instance of
 * {@link org.apache.tamaya.spi.EnvironmentManagerSingletonSpi}. It is possible to define multiple
 * {@link org.apache.tamaya.core.properties.PropertyProviderManager} instances, if required. <h3>Implementation
 * PropertyMapSpec</h3> Implementations of this interface must be
 * <ul>
 * <li>Thread safe.
 * </ul>
 */
public interface PropertyProviderManager{

	/**
	 * Access all defined {@link org.apache.tamaya.PropertyProvider} keys.
	 *
	 * @return all available PropertyMap keys, never{@code null}.
	 */
	Collection<String> getConfigMapKeys();

	/**
	 * Access a {@link org.apache.tamaya.PropertyProvider} by key.
	 *
	 * @param key
	 *            The key of the required {@link org.apache.tamaya.PropertyProvider}, not
	 *            {@code null}.
	 * @return the corresponding {@link org.apache.tamaya.PropertyProvider} corresponding to the
	 *         {@code key}.
	 * @throws org.apache.tamaya.ConfigException
	 *             if the required PropertyMap is not defined or not
	 *             available.
	 */
    PropertyProvider getConfigMap(String key);

	/**
	 * Allows to check if a {@link org.apache.tamaya.PropertyProvider} with the given key is
	 * defined.
	 *
	 * @param key
     *            The key of the required {@link org.apache.tamaya.PropertyProvider}, not
     *            {@code null}.
	 * @return true, if the given {@link org.apache.tamaya.PropertyProvider} is defined.
	 */
	boolean isConfigMapDefined(String key);

    /**
     * Reload a property set. This may trigger a PropertySetChanged event.
     * @param setId the set id.
     */
    public void reloadConfigMap(String setId);

}
