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

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Double, using the Java number syntax:
 * (-)?[0-9]*\.[0-9]*. In case of error the value given also is tried being parsed as integral number using
 * {@link LongConverter}. Additionally the following values are supported:
 * <ul>
 * <li>NaN (ignoring case)</li>
 * <li>POSITIVE_INFINITY (ignoring case)</li>
 * <li>NEGATIVE_INFINITY (ignoring case)</li>
 * </ul>
 */
public class DoubleConverter implements PropertyConverter<Double> {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(DoubleConverter.class.getName());
    /**
     * The converter used, when floating point parse failed.
     */
    private final LongConverter integerConverter = new LongConverter();

    @Override
    public Double convert(String value, ConversionContext context) {
        context.addSupportedFormats(getClass(), "<double>", "MIN", "MIN_VALUE", "MAX", "MAX_VALUE", "POSITIVE_INFINITY", "NEGATIVE_INFINITY", "NAN");
        String trimmed = Objects.requireNonNull(value).trim();
        switch (trimmed.toUpperCase(Locale.ENGLISH)) {
            case "POSITIVE_INFINITY":
                return Double.POSITIVE_INFINITY;
            case "NEGATIVE_INFINITY":
                return Double.NEGATIVE_INFINITY;
            case "NAN":
                return Double.NaN;
            case "MIN_VALUE":
            case "MIN":
                return Double.MIN_VALUE;
            case "MAX_VALUE":
            case "MAX":
                return Double.MAX_VALUE;
            default:
                try {
                    return Double.valueOf(trimmed);
                } catch (Exception e) {
                    // OK perhaps we have an integral number that must be converted to the double type...
                    LOG.finest("Parsing of double as floating number failed, trying parsing integral" +
                            " number/hex instead...");
                }
                Long val = integerConverter.convert(trimmed, context);
                if(val!=null){
                    return val.doubleValue();
                }
                return null;
        }

    }

    @Override
    public boolean equals(Object o){
        return getClass().equals(o.getClass());
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}
