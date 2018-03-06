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

import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Character, the supported format is one of the following:
 * <ul>
 *     <li>'a'</li>
 *     <li>123 (byte value)</li>
 *     <li>0xFF (byte value)</li>
 *     <li>0XDF (byte value)</li>
 *     <li>0D1 (byte value)</li>
 * </ul>
 */
@Component(service = PropertyConverter.class)
public class CharConverter implements PropertyConverter<Character>{

    private static final Logger LOG = Logger.getLogger(CharConverter.class.getName());

    @Override
    public Character convert(String value, ConversionContext context) {
        context.addSupportedFormats(getClass(),"\\'<char>\\'", "<char>", "<charNum>");
        String trimmed = Objects.requireNonNull(value).trim();
        if(trimmed.isEmpty()){
            return null;
        }
        if(trimmed.startsWith("'")) {
            try {
                if (trimmed.length() == 1){
                    return '\'';
                }
                trimmed = trimmed.substring(1, trimmed.length() - 1);
                if (trimmed.isEmpty()) {
                    return null;
                }
                return trimmed.charAt(0);
            } catch (Exception e) {
                LOG.finest("Invalid character format encountered: '" + value + "', valid formats are 'a', 101 and a.");
                return null;
            }
        }
        try {
            Integer val = Integer.parseInt(trimmed);
            return (char) val.shortValue();
        } catch (Exception e) {
            LOG.finest("Character format is not numeric: '" + value + "', using first character.");
            return trimmed.charAt(0);
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
