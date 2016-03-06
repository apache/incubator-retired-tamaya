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


import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;

public class DateTimeZoneConverterIT {
    @Test
    public void dateTimeZoneConverterCanBeFoundAsServiceProvider() {
        List<PropertyConverter> formats = ServiceContextManager.getServiceContext()
                                                               .getServices(PropertyConverter.class);

        PropertyConverter<?> converter = null;

        for (PropertyConverter format : formats) {
            if (format instanceof DateTimeZoneConverter) {
                converter = format;
                break;
            }
        }

        assertThat("Converter not found via service context.", converter, notNullValue());
        assertThat(converter, instanceOf(DateTimeZoneConverter.class));
    }

}
