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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

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

        for (Object[] pair : inputResultPairs) {
            DateTimeZone zone = converter.convert((String) pair[0]);

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

        for (String input : inputValues) {
            DateTimeZone date = converter.convert(input);

            assertThat(date, nullValue());
        }
    }
}
