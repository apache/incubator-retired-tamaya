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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.joda.time.format.ISODateTimeFormat.basicDateTime;
import static org.joda.time.format.ISODateTimeFormat.dateTime;

public class FullStackIT {
    @Test
    public void retrieveJodaTimeValuesFromConfiguration() {

        Configuration configuration = ConfigurationProvider.getConfiguration();

        String dateTimeString = configuration.get("dateTimeValue");
        DateTime dateTimeValue = configuration.get("dateTimeValue", DateTime.class);

        assertThat(dateTimeString, notNullValue());
        assertThat(dateTimeString, equalTo("2010-08-08T14:00:15.5+10:00"));
        assertThat(dateTimeValue, notNullValue());
        assertThat(dateTimeValue, equalTo(dateTime().parseDateTime("2010-08-08T14:00:15.5+10:00")));
    }
}
