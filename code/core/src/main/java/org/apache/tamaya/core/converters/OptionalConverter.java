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
import java.util.Optional;

/**
 * Converter, converting from String to Boolean.
 */
@Component(service = Converter.class)
public class OptionalConverter implements Converter<Optional> {

    @Override
    public Optional convert(String value) {
        ConversionContext context = ConversionContext.getContext();
        if(value==null){
            return Optional.empty();
        }
        try{
            if(context==null){
                throw new IllegalStateException("Failed to evaluate target type, context == null.");
            }
            Type targetType = context.getTargetType();
            if(Optional.class.equals(targetType)){
                ParameterizedType pt = (ParameterizedType) targetType;
                if(String.class.equals(pt.getActualTypeArguments()[0])){
                    return Optional.of(value);
                }
                else{
                    targetType = pt.getActualTypeArguments()[0];
                }
            }
            if(context.getConfiguration()==null){
                throw new IllegalStateException("Parametrized converters require a configuration for accessing the converters of their" +
                        "child elements.");
            }
            ConvertQuery converter = new ConvertQuery(value, targetType);
            return Optional.ofNullable(converter.apply(context.getConfiguration()));
        }catch (IllegalStateException ise){
            throw ise;
        }catch(Exception e){
            throw new IllegalArgumentException("Error evaluating config value.", e);
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
