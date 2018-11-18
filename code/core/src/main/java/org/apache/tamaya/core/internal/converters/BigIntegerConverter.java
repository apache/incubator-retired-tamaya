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

import java.math.BigInteger;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to BigInteger, the supported format is one of the following:
 * <ul>
 *     <li>0xFFFFFF</li>
 *     <li>0XFFFFAC</li>
 *     <li>23257352735276352753</li>
 *     <li>-0xFFFFFF</li>
 *     <li>-0XFFFFAC</li>
 *     <li>-23257352735276352753</li>
 * </ul>
 */
@Component(service = PropertyConverter.class)
public class BigIntegerConverter implements PropertyConverter<BigInteger> {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(BigIntegerConverter.class.getName());

    @Override
    public BigInteger convert(String value, ConversionContext ctx) {
        ctx.addSupportedFormats(getClass(), "[-]0X.. (hex)", "[-]0x... (hex)", "<bigint> -> new BigInteger(bigint)");
        if(value==null){
            return null;
        }
        String trimmed = value.trim();
        if(trimmed.startsWith("0x") || trimmed.startsWith("0X")){
            LOG.finest("Parsing Hex createValue to BigInteger: " + value);
            return new BigInteger(value.substring(2), 16);
        } else if(trimmed.startsWith("-0x") || trimmed.startsWith("-0X")){
            LOG.finest("Parsing Hex createValue to BigInteger: " + value);
            return new BigInteger('-' + value.substring(3), 16);
        }
        try{
            return new BigInteger(trimmed);
        } catch(Exception e){
            LOG.log(Level.FINEST, "Failed to parse BigInteger from: " + value, e);
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
