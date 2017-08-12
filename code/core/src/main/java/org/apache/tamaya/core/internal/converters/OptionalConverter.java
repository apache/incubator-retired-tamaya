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

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.internal.PropertyConverterManager;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;

import java.lang.reflect.Type;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Boolean.
 */
public class OptionalConverter implements PropertyConverter<Optional> {

    private final Logger LOG = Logger.getLogger(getClass().getName());

    @Override
    public Optional<?> convert(String value, ConversionContext context) {
        TypeLiteral<Optional> target = (TypeLiteral<Optional>)context.getTargetType();
        Object result = null;
        Type targetType = TypeLiteral.getTypeParameters(target.getType())[0];
        if(String.class.equals(targetType)){
            result = value;
        }
        for(PropertyConverter pv:context.getConfigurationContext().getPropertyConverters(
                TypeLiteral.of(targetType))){
            result = pv.convert(value, context);
            if(result!=null){
                return Optional.of(result);
            }
        }
        return Optional.ofNullable(result);
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
