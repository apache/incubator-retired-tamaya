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

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Number. Valid inputs are:
 * <pre>
 *     POSITIVE_INFINITY -&gt; Double.POSITIVE_INFINITY
 *     NEGATIVE_INFINITY -&gt; Double.NEGATIVE_INFINITY
 *     NaN &gt; Double.NaN
 *     0xFFDCD3D2 -&gt; Long
 *     1234566789.23642327352735273752 -&gt; new BigDecimal(input)
 * </pre>
 */
@Component(service = PropertyConverter.class)
public class NumberConverter implements PropertyConverter<Number>{
    /** the logger. */
    private static final Logger LOGGER = Logger.getLogger(NumberConverter.class.getName());
    /** Converter used for trying to parse as an integral createValue. */
    private final LongConverter longConverter = new LongConverter();

    @Override
    public Number convert(String value, ConversionContext ctx) {
        ctx.addSupportedFormats(getClass(), "<double>, <long>", "0x (hex)", "0X... (hex)", "POSITIVE_INFINITY",
                "NEGATIVE_INFINITY", "NAN");

        if(value==null){
            return null;
        }
        String trimmed = value.trim();
        switch(trimmed.toUpperCase(Locale.ENGLISH)) {
            case "POSITIVE_INFINITY":
                return Double.POSITIVE_INFINITY;
            case "NEGATIVE_INFINITY":
                return Double.NEGATIVE_INFINITY;
            case "NAN":
                return Double.NaN;
            default:
                Long lVal = longConverter.convert(trimmed, ctx);
                if (lVal != null) {
                    return lVal;
                }
                try {
                    return new BigDecimal(trimmed);
                } catch(Exception e) {
                    LOGGER.finest("Unparseable Number: " + trimmed);
                    return null;
                }
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
