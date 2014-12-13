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
package org.apache.tamaya;


/**
 * Stage which should be supported by all layers, users may still add sub stages by adding additional properties
 * to the current environment.
 */
public enum Stage {

    /**
     * Get the default stage for develpment.
     * @return the default stage, never null.
     */
    DEVELOPMENT,

    /**
     * Get the default stage for (component) testing.
     * @return the default stage, never null.
     */
    TEST,

    /**
     * Get the default stage for integration (testing).
     * @return the default stage, never null.
     */
    INTEGRATION,

    /**
     * Get the default stage for staging.
     * @return the default stage, never null.
     */
    STAGING,

    /**
     * Get the default stage for production.
     * @return the default stage, never null.
     */
    PRODUCTION
}


