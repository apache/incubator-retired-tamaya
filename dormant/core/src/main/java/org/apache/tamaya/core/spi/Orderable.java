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
package org.apache.tamaya.core.spi;

/**
 * Interface that can be optionally implemented by SPI components to be loaded into
 * the Tamaya's ServiceContext. The ordinal provided will be used to determine
 * priority and precedence, when multiple components implement the same
 * service interface.
 */
@FunctionalInterface
public interface Orderable {
    /**
     * Get the ordinal keys for the component, by default 0.
     * @return the ordinal keys
     */
    int order();
}
