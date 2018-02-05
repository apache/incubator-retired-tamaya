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
import org.apache.tamaya.base.ConfigContext;
import org.apache.tamaya.base.ConfigContextSupplier;

import javax.config.Config;
import javax.config.spi.Converter;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Query to convert a String value.
 * @param <T> the target type.
 */
final class ConvertQuery<T> implements Function<Config, T> {

    private static final Logger LOG = Logger.getLogger(ConvertQuery.class.getName());

    private String rawValue;
    private Type type;

    public ConvertQuery(String rawValue, Type type) {
        this.rawValue = Objects.requireNonNull(rawValue);
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public T apply(Config config) {
        if(!(config instanceof ConfigContextSupplier)){
            throw new IllegalArgumentException("Config must implement ConfigContextSupplier");
        }
        ConfigContext ctx = ((ConfigContextSupplier)config).getConfigContext();
        List<Converter> converters = ctx.getConverters(type);
        ConversionContext context = new ConversionContext.Builder("<nokey>", type)
                .setConfiguration(config)
                .setKey(ConvertQuery.class.getName())
                .build();
        ConversionContext.setContext(context);
        try {
            for (Converter<?> conv : converters) {
                try {
                    if (conv instanceof OptionalConverter) {
                        continue;
                    }
                    T result = (T) conv.convert(rawValue);
                    if (result != null) {
                        return result;
                    }
                } catch (Exception e) {
                    LOG.log(Level.FINEST, e, () -> "Converter " + conv + " failed to convert to " + type);
                }
            }
        }finally{
            ConversionContext.reset();
        }
        return null;
    }
}
