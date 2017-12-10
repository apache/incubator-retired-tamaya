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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Boolean.
 */
@Component(service = Converter.class)
public class SupplierConverter implements Converter<Supplier> {

    private static final Logger LOG = Logger.getLogger(SupplierConverter.class.getName());

    @Override
    public Supplier convert(String value) {
        ConversionContext context = ConversionContext.getContext();
        return () -> {
            try{
                Type targetType = context.getTargetType();
                ParameterizedType pt = (ParameterizedType) targetType;
                if(String.class.equals(pt.getActualTypeArguments()[0])){
                    return value;
                }
                ConvertQuery converter = new ConvertQuery(value, pt.getActualTypeArguments()[0]);
                Object o = converter.apply(context.getConfiguration());
                if(o==null){
                    throw new IllegalArgumentException("No such value: " + context.getKey());
                }
                return o;
            }catch(Exception e){
                throw new IllegalArgumentException("Error evaluating config value.", e);
            }
        };
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
