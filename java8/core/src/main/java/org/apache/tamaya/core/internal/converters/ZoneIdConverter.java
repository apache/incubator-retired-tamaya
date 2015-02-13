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
package org.apache.tamaya.core.internal.converters;

import org.apache.tamaya.PropertyConverter;

import java.time.ZoneId;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to ZoneId. Valid inputs are as defined by {@link ZoneId#of(String)}.
 */
public class ZoneIdConverter implements PropertyConverter<ZoneId> {

    /** the logger. */
    private static final Logger LOG = Logger.getLogger(ShortConverter.class.getName());

    @Override
    public ZoneId convert(String value) {
        String trimmed = Objects.requireNonNull(value).trim();

        try {
            return ZoneId.of(trimmed);
        } catch (Exception e) {
            LOG.finest("Unparseable ZoneId: " + trimmed);
            return null;
        }
    }
}
