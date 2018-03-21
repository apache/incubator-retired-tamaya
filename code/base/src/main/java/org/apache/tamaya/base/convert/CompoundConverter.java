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
package org.apache.tamaya.base.convert;

import javax.config.spi.Converter;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter which implements a {@link Converter} based on multiple converter instances.
 * @param <C>
 */
public final class CompoundConverter<C> implements Converter<C> {

    private static final Logger LOG = Logger.getLogger(CompoundConverter.class.getName());

    /** The delegating converter list, not null.*/
    private List<Converter<C>> converters;

    /**
     * Creates a new converter.
     * @param converters the converter to delegate to in order of precedence (most significant first), not null.
     */
    public CompoundConverter(List<Converter<C>> converters) {
        this.converters = Objects.requireNonNull(converters);
    }


    @Override
    public C convert(String value) {
        for(Converter<C> converter:converters){
            try{
                C result = converter.convert(value);
                if(result!=null){
                    return result;
                }
            }catch(Exception e){
                LOG.log(Level.WARNING, e, () -> "Converter failed for value: " + value);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "CompoundConverter{" +
                "converters=" + converters +
                '}';
    }
}
