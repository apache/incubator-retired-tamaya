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
public class BigIntegerConverter implements PropertyConverter<BigInteger>{

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(BigIntegerConverter.class.getName());
    /** Converter used to decode hex, octal values. */
    private ByteConverter byteConverter = new ByteConverter();

    @Override
    public BigInteger convert(String value) {
        String trimmed = Objects.requireNonNull(value).trim();
        if(trimmed.startsWith("0x") || trimmed.startsWith("0X")){
            LOG.finer("Parsing Hex value to BigInteger: " + value);
            trimmed = trimmed.substring(2);
            StringBuilder decimal = new StringBuilder();
            for(int offset = 0;offset < trimmed.length();offset+=2){
                if(offset==trimmed.length()-1){
                    LOG.info("Invalid Hex-Byte-String: " + value);
                    return null;
                }
                byte val = byteConverter.convert("0x" + trimmed.substring(offset, offset + 2));
                if(val<10){
                    decimal.append('0').append(val);
                } else{
                    decimal.append(val);
                }
            }
            return new BigInteger(decimal.toString());
        } else if(trimmed.startsWith("-0x") || trimmed.startsWith("-0X")){
            LOG.finer("Parsing Hex value to BigInteger: " + value);
            trimmed = trimmed.substring(3);
            StringBuilder decimal = new StringBuilder();
            for(int offset = 0;offset < trimmed.length();offset+=2){
                if(offset==trimmed.length()-1){
                    LOG.info("Invalid Hex-Byte-String: " + trimmed);
                    return null;
                }
                byte val = byteConverter.convert("0x" + trimmed.substring(offset, offset + 2));
                if(val<10){
                    decimal.append('0').append(val);
                } else{
                    decimal.append(val);
                }
            }
            return new BigInteger('-' + decimal.toString());
        }
        try{
            return new BigInteger(trimmed);
        } catch(Exception e){
            LOG.log(Level.FINEST, "Failed to parse BigInteger from: " + value, e);
            return null;
        }
    }

}
