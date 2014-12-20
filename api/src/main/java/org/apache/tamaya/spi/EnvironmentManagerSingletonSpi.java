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


import org.apache.tamaya.Environment;

/**
 * Service for accessing {@link org.apache.tamaya.Environment}. Environments are used to
 * access/determine configurations.<br/>
 * <h3>Implementation PropertyMapSpec</h3> This class is
 * <ul>
 * <li>thread safe,
 * <li>and behaves contextual.
 * </ul>
 */
public interface EnvironmentManagerSingletonSpi{

    /**
     * Get the current environment current the given environment type.
     * @return the corresponding environment, never null.
     * @throws IllegalArgumentException if not such type is present or active.
     */
    Environment getCurrentEnvironment();

    /**
     * Get the current environment current the given environment type.
     * @return the corresponding environment, never null.
     * @throws IllegalArgumentException if not such type is present or active.
     */
    Environment getRootEnvironment();

}
