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
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import java.util.Objects;

/**
 * Converter, converting from {@code String} to Joda-Time's
 * {@code DateTime}.
 *
 * The converter supports the following formats for the provided
 * time information:
 *
 * <ul>
 *     <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSSZ}</li>
 *     <li>{@code yyyy-MM-dd'T'HH:mm:ss.SSSz}</li>
 *     <li>{@code yyyy-MM-dd'T'HH:mm:ssZ}</li>
 *     <li>{@code yyyy-MM-dd'T'HH:mm:ssz}</li>
 *     <li>{@code yyyy-MM-dd'T'HH:mmZ}</li>
 *     <li>{@code yyyy-MM-dd'T'HH:mmz}</li>
 *     <li>{@code yyyy-MM-dd'T'HHZ}</li>
 *     <li>{@code yyyy-MM-dd'T'HHz}</li>
 * </ul>
 */
public class DateTimeConverter implements PropertyConverter<DateTime> {
    // The DateTimeFormatter returned by ISODateTimeFormat are thread safe
    // according to the JavaDoc of JodaTime
    private final static DateTimeParser FORMATS[] = {
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS z").getParser(),

         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssz").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss z").getParser(),

         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mmZ").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mmz").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm z").getParser(),

         DateTimeFormat.forPattern("yyyy-MM-dd'T'HHZ").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HHz").getParser(),
         DateTimeFormat.forPattern("yyyy-MM-dd'T'HH z").getParser(),
    };

    protected static final DateTimeFormatter formatter;

    static {
        formatter = new DateTimeFormatterBuilder().append(null, FORMATS).toFormatter();
    }

    @Override
    public DateTime convert(String value) {
        String trimmed = Objects.requireNonNull(value).trim();
        DateTime result = null;

        try {
            result = formatter.parseDateTime(trimmed);
        } catch (RuntimeException e) {
            // Ok, go on and try the next parser
        }

        return result;
    }
}
