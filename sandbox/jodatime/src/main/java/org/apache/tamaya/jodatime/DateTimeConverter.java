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

import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
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
    static final String PARSER_FORMATS[] = {
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSSz",
        "yyyy-MM-dd'T'HH:mm:ss.SSS z",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ssz",
        "yyyy-MM-dd'T'HH:mm:ss z",
        "yyyy-MM-dd'T'HH:mmZ",
        "yyyy-MM-dd'T'HH:mmz",
        "yyyy-MM-dd'T'HH:mm z",
        "yyyy-MM-dd'T'HHZ",
        "yyyy-MM-dd'T'HHz",
        "yyyy-MM-dd'T'HH z",
    };


    // The DateTimeFormatter returned by ISODateTimeFormat are thread safe
    // according to the JavaDoc of JodaTime
    final static DateTimeParser FORMATS[] = {
         DateTimeFormat.forPattern(PARSER_FORMATS[0]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[1]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[2]).getParser(),

         DateTimeFormat.forPattern(PARSER_FORMATS[3]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[4]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[5]).getParser(),

         DateTimeFormat.forPattern(PARSER_FORMATS[6]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[7]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[8]).getParser(),

         DateTimeFormat.forPattern(PARSER_FORMATS[9]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[10]).getParser(),
         DateTimeFormat.forPattern(PARSER_FORMATS[11]).getParser(),
    };

    protected static final DateTimeFormatter formatter;

    static {
        formatter = new DateTimeFormatterBuilder().append(null, FORMATS).toFormatter();
    }

    @Override
    public DateTime convert(String value, ConversionContext context) {
        context.addSupportedFormats(DateTimeConverter.class, PARSER_FORMATS);

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
