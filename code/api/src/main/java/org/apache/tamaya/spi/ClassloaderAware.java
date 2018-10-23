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



/**
 * <p>This interface models a provider that serves configuration properties that is also dependent on the
 * target classloader of the current configuration. </p>
 * @see PropertySource
 * @see PropertySourceProvider
 * <h3>Implementation Requirements</h3>
 * <p>Implementations of this interface must be</p>
 * <ul>
 * <li>Thread safe.</li>
 * </ul>
 */
public interface ClassloaderAware{

    /**
     * Initializes this instance with the classloader to be used.
     * This method is called by the implementation when the instance is loaded
     * through the {@link ServiceContextManager}.
     * @param classLoader the target classloader, not not null.
     */
    void init(ClassLoader classLoader);

    /**
     * Get the currently assigned cassloader instance.
     * @return the classloader, never null.
     */
    ClassLoader getClassLoader();

}
