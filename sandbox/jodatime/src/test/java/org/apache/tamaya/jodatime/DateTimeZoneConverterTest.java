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

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.ConversionContext.Builder;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

public class DateTimeZoneConverterTest {
    private DateTimeZoneConverter converter = new DateTimeZoneConverter();

    @Test
    public void canConvertDateTimeZoneInformation() {
        Object[][] inputResultPairs = {
             // Valid input for DateTimeZone.forOffsetHours
             {"1", DateTimeZone.forOffsetHours(1)},
             {"12", DateTimeZone.forOffsetHours(12)},
             {"13", DateTimeZone.forOffsetHours(13)},
             {"0", DateTimeZone.forOffsetHours(0)},
             {"-1 ", DateTimeZone.forOffsetHours(-1)},

             // Valid input for DateTimeZone.forID()
             {"Chile/EasterIsland", DateTimeZone.forID("Chile/EasterIsland")},
             {"UTC", DateTimeZone.forID("UTC")},
             {"+00", DateTimeZone.forID("+00:00")},
             {"+00:00", DateTimeZone.forID("+00:00")},
             {"+00:00 ", DateTimeZone.forID("+00:00")},
             {" +00:00 ", DateTimeZone.forID("+00:00")},
             {"+04:00", DateTimeZone.forID("+04:00")},
        };

        ConversionContext context = Mockito.mock(ConversionContext.class);

        for (Object[] pair : inputResultPairs) {
            DateTimeZone zone = converter.convert((String) pair[0], context);

            assertThat("Converter failed to convert input value " + pair[0], zone, notNullValue());
            assertThat(zone, equalTo((DateTimeZone)pair[1]));
        }
    }

    @Test
    public void invalidInputValuesResultInReturningNull() {
        String[] inputValues = {
             "2007-08-01T12:34:45.000+0:0",
             "2007-08-01T12:34:45.000+00:0",
             "2007-08-01T12:34:45.000+00:0",
             "2007-08-01T+00:00",
             "2007-08-01+00:00"
        };

        ConversionContext context = Mockito.mock(ConversionContext.class);

        for (String input : inputValues) {
            DateTimeZone date = converter.convert(input, context);

            assertThat(date, nullValue());
        }
    }

    @Test
    public void allSupportedFormatsAreAddedToTheConversionContext() {
        String firstFormat = "Time zone in the form [+-]hh:mm via the regex (\\+|-)?\\d+ (DateTimeZoneConverter)";
        String secondFormat = "All time zone ids supported by Joda Time (DateTimeZoneConverter)";

        ConversionContext context = new Builder(TypeLiteral.of(DateTimeZone.class)).build();

        DateTimeZone result = converter.convert("+01:00", context);

        assertThat(result, notNullValue());
        assertThat(context.getSupportedFormats(), hasSize(2));
        assertThat(context.getSupportedFormats(), hasItem(firstFormat));
        assertThat(context.getSupportedFormats(), hasItem(secondFormat));
    }

}
