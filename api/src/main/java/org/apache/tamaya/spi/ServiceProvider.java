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
package org.apache.tamaya.spi;

import java.util.Collections;
import java.util.List;

/**
 * This class models the component that is managing the lifecycle current the
 * services used by the Configuration API.
 */
public interface ServiceProvider {

	/**
	 * Access a list current services, given its type. The bootstrap mechanism should
	 * order the instance for precedence, hereby the most significant should be
	 * first in order.
	 * 
	 * @param serviceType
	 *            the service type.
	 * @return The instance to be used, never {@code null}
	 */
	@SuppressWarnings("unchecked")
    default <T> List<T> getServices(Class<T> serviceType){
          return getServices(serviceType, Collections.emptyList());
    }

	/**
	 * Access a list current services, given its type. The bootstrap mechanism should
	 * order the instance for precedence, hereby the most significant should be
	 * first in order.
	 * 
	 * @param serviceType
	 *            the service type.
	 * @param defaultList
	 *            the lis returned, if no services could be found.
	 * @return The instance to be used, never {@code null}
	 */
    <T> List<T> getServices(Class<T> serviceType, List<T> defaultList);

}
