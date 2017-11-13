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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Converter, converting from String to Boolean.
 */
@Component(service = PropertyConverter.class)
public class OptionalConverter implements PropertyConverter<Optional> {

    @Override
    public Optional convert(String value, ConversionContext context) {
        if(value==null){
            return Optional.empty();
        }
        try{
            Type targetType = context.getTargetType().getType();
            ParameterizedType pt = (ParameterizedType) targetType;
            if(String.class.equals(pt.getActualTypeArguments()[0])){
                return Optional.of(value);
            }
            ConvertQuery converter = new ConvertQuery(value, TypeLiteral.of(pt.getActualTypeArguments()[0]));
            return Optional.ofNullable(context.getConfiguration().query(converter));
        }catch(Exception e){
            throw new ConfigException("Error evaluating config value.", e);
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
