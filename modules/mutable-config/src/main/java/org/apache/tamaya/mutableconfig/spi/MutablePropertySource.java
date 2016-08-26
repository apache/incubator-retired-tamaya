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
package org.apache.tamaya.mutableconfig.spi;

import org.apache.tamaya.spi.PropertySource;


/**
 * This interface models a writable backend for configuration data.
 *
 * As a consequence clients should first check, using the corresponding methods, if entries are to edited or removedProperties
 * actually are eligible for change/creation or removal.
 */
public interface MutablePropertySource extends PropertySource {

    /**
     * Puts all given configuration entries. This method should check that all given properties are
     * basically removable, as defined by #isWritable. If any of the passed keys is not writable during this initial
     * check, the operation should not perform any configuration changes and throw a {@link org.apache.tamaya.ConfigException}. If errors
     * occur afterwards, when the properties are effectively written back to the backends, the errors should be
     * collected and returned as part of the ConfigException payload. Nevertheless the operation should in that case
     * remove all entries as far as possible and abort the writing operation.
     *
     * @param configChange the {@link ConfigChangeRequest}, containing the transactionId used to isolate
     *                     the change, the properties to be added/overridden and the property keys
     *                     being removed.
     * @throws org.apache.tamaya.ConfigException if any of the given properties could not be written, or the request is read-only.
     */
    void applyChange(ConfigChangeRequest configChange);

}
