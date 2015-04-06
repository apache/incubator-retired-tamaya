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
package org.apache.tamaya.jodatime;

import org.apache.tamaya.PropertyConverter;
import org.joda.time.DateTimeZone;

import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

public class DateTimeZoneConverter implements PropertyConverter<DateTimeZone> {
    private static final Pattern IS_INTEGER_VALUE = Pattern.compile("(\\+|-)?\\d+");

    @Override
    public DateTimeZone convert(String value) {
        String trimmed = requireNonNull(value).trim();

        DateTimeZone result = null;

        try {
            if (isSingleIntegerValue(trimmed)) {
                int offset = Integer.parseInt(trimmed);
                result = DateTimeZone.forOffsetHours(offset);
            } else { // Let us assume a string id
                result = DateTimeZone.forID(trimmed);
            }

        } catch (Exception e) {
            result = null; // Give the next converter a change. Read the JavaDoc of convert
        }

        return result;
    }

    private boolean isSingleIntegerValue(String value) {
        boolean match = IS_INTEGER_VALUE.matcher(value).matches();

        return match;
    }
}
