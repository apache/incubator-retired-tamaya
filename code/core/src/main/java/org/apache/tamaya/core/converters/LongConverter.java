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
package org.apache.tamaya.core.converters;

import org.apache.tamaya.base.convert.ConversionContext;
import org.osgi.service.component.annotations.Component;

import javax.config.spi.Converter;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Long, the supported format is one of the following:
 * <ul>
 *     <li>123 (byte value)</li>
 *     <li>0xFF (byte value)</li>
 *     <li>0XDF (byte value)</li>
 *     <li>0D1 (byte value)</li>
 *     <li>-123 (byte value)</li>
 *     <li>-0xFF (byte value)</li>
 *     <li>-0XDF (byte value)</li>
 *     <li>-0D1 (byte value)</li>
 *     <li>MIN_VALUE (ignoring case)</li>
 *     <li>MIN (ignoring case)</li>
 *     <li>MAX_VALUE (ignoring case)</li>
 *     <li>MAX (ignoring case)</li>
 * </ul>
 */
@Component(service = Converter.class)
public class LongConverter implements Converter<Long> {

    private static final Logger LOGGER = Logger.getLogger(LongConverter.class.getName());

    @Override
    public Long convert(String value) {
        ConversionContext context = ConversionContext.getContext();
        if(context!=null) {
            context.addSupportedFormats(getClass(), "<long>", "MIN", "MIN_VALUE", "MAX", "MAX_VALUE");
        }

        String trimmed = Objects.requireNonNull(value).trim();
            switch (trimmed.toUpperCase(Locale.ENGLISH)) {
                case "MIN_VALUE":
                case "MIN":
                    return Long.MIN_VALUE;
                case "MAX_VALUE":
                case "MAX":
                    return Long.MAX_VALUE;
                default:
                    try {
                        return Long.decode(trimmed);
                    }
                    catch(Exception e){
                        LOGGER.finest("Unable to parse Long value: " + value);
                        return null;
                    }
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
