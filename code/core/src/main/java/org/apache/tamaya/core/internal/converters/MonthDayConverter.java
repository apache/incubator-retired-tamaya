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

import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;

import java.time.MonthDay;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Year.
 */
@Component(service = PropertyConverter.class)
public class MonthDayConverter implements PropertyConverter<MonthDay> {

    private static final Logger LOG = Logger.getLogger(MonthDayConverter.class.getName());

    @Override
    public MonthDay convert(String value, ConversionContext ctx) {
        ctx.addSupportedFormats(getClass(), MonthDay.now().toString());
        if(value==null){
            return null;
        }
        try{
            return MonthDay.parse(value);
        }catch(Exception e){
            LOG.log(Level.FINEST, e, () -> "Cannot parse MonthDay: " + value);
            return null;
        }
    }

    @Override
    public boolean equals(Object o){
        return Objects.nonNull(o) && getClass().equals(o.getClass());
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}
