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

import java.util.OptionalInt;

/**
 * The ordinal provider is an optional component that provides an abstraction for ordering/prioritizing
 * services loaded. This can be used to determine, which SPI should be used, if multiple instances are
 * available, or for ordering chain of services.
 * @see ServiceContext
 */
public interface OrdinalProvider {
    /**
     * Evaluate the ordinal number for the given type.
     * @param type the target type, not null.
     * @return the ordinal, if not defined, 0 should be returned.
     */
     OptionalInt getOrdinal(Class<?> type);

}
