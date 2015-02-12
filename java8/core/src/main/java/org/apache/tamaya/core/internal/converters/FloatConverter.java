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

import org.apache.tamaya.PropertyConverter;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Float, using the Java number syntax:
 * (-)?[0-9]*\.[0-9]*. In case of error the value given also is tried being parsed as integral number using
 * {@link org.apache.tamaya.core.internal.converters.LongConverter}.
 */
public class FloatConverter implements PropertyConverter<Float> {
    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(FloatConverter.class.getName());
    /**
     * The converter used, when floating point parse failed.
     */
    private IntegerConverter integerConverter = new IntegerConverter();

    @Override
    public Float convert(String value) {
        String trimmed = Objects.requireNonNull(value).trim();
        switch(trimmed.toUpperCase(Locale.ENGLISH)){
            case "POSITIVE_INFINITY":
                return Float.POSITIVE_INFINITY;
            case "NEGATIVE_INFINITY":
                return Float.NEGATIVE_INFINITY;
            case "NAN":
                return Float.NaN;
            case "MIN_VALUE":
            case "MIN":
                return Float.MIN_VALUE;
            case "MAX_VALUE":
            case "MAX":
                return Float.MAX_VALUE;
            default:
                try {
                    return Float.valueOf(trimmed);
                } catch(Exception e){
                    // OK perhaps we have an integral number that must be converted to the double type...
                    LOG.finest(() -> "Parsing of float as floating number failed, trying parsing integral" +
                            " number/hex instead...");
                }
                Integer val = integerConverter.convert(trimmed);
                if(val!=null) {
                    return val.floatValue();
                }
                LOG.finest(() -> "Unparseable float value: " + trimmed);
                return null;
        }
    }
}
