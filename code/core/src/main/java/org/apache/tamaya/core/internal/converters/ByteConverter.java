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

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Byte, the supported format is one of the following:
 * <ul>
 *     <li>123 (byte createValue)</li>
 *     <li>0xFF (byte createValue)</li>
 *     <li>0XDF (byte createValue)</li>
 *     <li>0D1 (byte createValue)</li>
 *     <li>-123 (byte createValue)</li>
 *     <li>-0xFF (byte createValue)</li>
 *     <li>-0XDF (byte createValue)</li>
 *     <li>-0D1 (byte createValue)</li>
 *     <li>MIN_VALUE (ignoring case)</li>
 *     <li>MIN (ignoring case)</li>
 *     <li>MAX_VALUE (ignoring case)</li>
 *     <li>MAX (ignoring case)</li>
 * </ul>
 */
@Component(service = PropertyConverter.class)
public class ByteConverter implements PropertyConverter<Byte>{

    private final Logger LOG = Logger.getLogger(getClass().getName());

    @Override
    public Byte convert(String value, ConversionContext ctx) {
        ctx.addSupportedFormats(getClass(),"<byte>", "MIN_VALUE", "MIN", "MAX_VALUE", "MAX");
        if(value==null){
            return null;
        }
        String trimmed = value.trim();
        switch(trimmed.toUpperCase(Locale.ENGLISH)){
            case "MIN_VALUE":
            case "MIN":
                return Byte.MIN_VALUE;
            case "MAX_VALUE":
            case "MAX":
                return Byte.MAX_VALUE;
            default:
                try {
                    return Byte.decode(trimmed);
                } catch(Exception e) {
                    LOG.log(Level.FINEST, "Unparseable Byte: " + value);
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
