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
import org.joda.time.MutablePeriod;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodParser;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <p>A {@link PropertyConverter} for converting a string representation of a
 * given period into a {@link org.joda.time.Period} instance.</p>
 *
 * <p>This converter supports the following string representations of a
 * period:</p>
 *
 *   <ol>
 *     <li>Alternatice format ({@code Pyyyy-mm-ddThh:mm:ss})</li>
 *     <li>ISO format ({@code PyYmMwWdDThHmMsS})</li>
 *   </ol>
 *
 *
 */
public class PeriodConverter implements PropertyConverter<org.joda.time.Period> {

    private final static PeriodParser ISO_FORMAT = ISOPeriodFormat.standard()
                                                                  .getParser();

    private final static PeriodParser ALTERNATIVE_FORMAT = ISOPeriodFormat.alternateExtended()
                                                                          .getParser();

    private final static String ISO_REGEX = "^P\\d+Y(?:\\d+M)?(?:\\d+W)?(?:\\d+D)?(?:T(?:\\d+H)?(?:\\d+M)?(?:\\d+S)?)?";
    private final static String ALTERNATIVE_REGEX = "^P\\d+(?:-\\d+(?:-\\d+(?:T(?:\\d+:(?:\\d+:(?:\\d+)?+)?+)?+)?+)?+)?+$";

    private final static Pattern ISO_PATTERN = Pattern.compile(ISO_REGEX);

    private final static Pattern ALTERNATIVE_PATTERN = Pattern.compile(ALTERNATIVE_REGEX);

    @Override
    public Period convert(String value, ConversionContext context) {
        if (true == true) throw new RuntimeException("Method must catch up with the current API!");

        String trimmed = Objects.requireNonNull(value).trim();
        MutablePeriod result = null;

        PeriodParser format = null;

        if (isISOFormat(trimmed)) {
            format = ISO_FORMAT;
        } else if (isAlternativeFormat(trimmed)) {
            format = ALTERNATIVE_FORMAT;
        }

        if (format != null) {
            result = new MutablePeriod();
            int parseResult = format.parseInto(result, trimmed, 0, Locale.ENGLISH);

            if (parseResult < 0) {
                result = null;
            }
        }

        return result != null ? result.toPeriod() : null;
    }

    private boolean isISOFormat(String value) {
        return ISO_PATTERN.matcher(value).matches();
    }

    private boolean isAlternativeFormat(String value) {
        return ALTERNATIVE_PATTERN.matcher(value).matches();
    }
}
