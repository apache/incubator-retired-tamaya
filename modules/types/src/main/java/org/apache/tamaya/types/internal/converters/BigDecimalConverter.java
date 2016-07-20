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
package org.apache.tamaya.types.internal.converters;

import org.apache.tamaya.types.spi.ConversionContext;
import org.apache.tamaya.types.spi.PropertyConverter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to BigDecimal, the supported format is one of the following:
 * <ul>
 *     <li>232573527352.76352753</li>
 *     <li>-23257352.735276352753</li>
 *     <li>-0xFFFFFF (integral numbers only)</li>
 *     <li>-0XFFFFAC (integral numbers only)</li>
 *     <li>0xFFFFFF (integral numbers only)</li>
 *     <li>0XFFFFAC (integral numbers only)</li>
 * </ul>
 */
public class BigDecimalConverter implements PropertyConverter<BigDecimal> {
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(BigDecimalConverter.class.getName());
    /** Converter to be used if the format is not directly supported by BigDecimal, e.g. for integral hex values. */
    private final BigIntegerConverter integerConverter = new BigIntegerConverter();

    @Override
    public BigDecimal convert(String value, ConversionContext context) {
        context.addSupportedFormats(getClass(), "<bigDecimal> -> new BigDecimal(String)");

        String trimmed = Objects.requireNonNull(value).trim();
        try{
            return new BigDecimal(trimmed);
        } catch(Exception e){
            LOG.finest("Parsing BigDecimal failed, trying BigInteger for: " + value);
            BigInteger bigInt = integerConverter.convert(value, context);
            if(bigInt!=null){
                return new BigDecimal(bigInt);
            }
            LOG.finest("Failed to parse BigDecimal from: " + value);
            return null;
        }
    }
}
