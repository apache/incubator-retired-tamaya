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
import org.apache.tamaya.ConfigQuery;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.internal.PropertyConverterManager;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Boolean.
 */
@Component(service = PropertyConverter.class)
public class OptionalConverter implements PropertyConverter<Optional> {

    private static final Logger LOG = Logger.getLogger(OptionalConverter.class.getName());

    @Override
    public Optional convert(String value, ConversionContext context) {
        try{
            Type targetType = context.getTargetType().getType();
            ParameterizedType pt = (ParameterizedType) targetType;
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


    private static final class ConvertQuery<T> implements ConfigQuery<T>{

        private String rawValue;
        private TypeLiteral<T> type;

        public ConvertQuery(String rawValue, TypeLiteral<T> type) {
            this.rawValue = Objects.requireNonNull(rawValue);
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public T query(Configuration config) {
            List<PropertyConverter<T>> converters = config.getContext().getPropertyConverters(type);
            ConversionContext context = new ConversionContext.Builder(type).setConfigurationContext(config.getContext())
                    .setConfiguration(config).setKey(ConvertQuery.class.getName()).build();
            for(PropertyConverter<?> conv: converters) {
                try{
                    if(conv instanceof OptionalConverter){
                        continue;
                    }
                    T result = (T)conv.convert(rawValue, context);
                    if(result!=null){
                        return result;
                    }
                }catch(Exception e){
                    LOG.log(Level.FINEST,  e, () -> "Converter "+ conv +" failed to convert to " + type);
                }
            }
            return null;
        }
    }
}
