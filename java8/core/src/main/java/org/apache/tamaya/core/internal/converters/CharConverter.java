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

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Character.
 */
public class CharConverter implements PropertyConverter<Character>{

    private static final Logger LOG = Logger.getLogger(CharConverter.class.getName());

    @Override
    public Character convert(String value) {
        String trimmed = Objects.requireNonNull(value).trim();
        if(trimmed.isEmpty()){
            return null;
        }
        if(trimmed.startsWith("'")) {
            try {
                trimmed = trimmed.substring(1, trimmed.length() - 1);
                if (trimmed.isEmpty()) {
                    return null;
                }
                return trimmed.charAt(0);
            } catch (Exception e) {
                LOG.warning("Invalid character format encountered: '" + value + "', valid formats are 'a', 101 and a.");
                return null;
            }
        }
        try {
            Integer val = Integer.parseInt(trimmed);
            return (char) val.shortValue();
        } catch (Exception e) {
            LOG.finest("Character format is not numeric: '" + value + "', using first character.");
            return trimmed.charAt(0);
        }
    }
}
