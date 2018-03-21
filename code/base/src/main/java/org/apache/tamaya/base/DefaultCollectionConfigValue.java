/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.base;

import javax.config.spi.Converter;
import java.util.*;
import java.util.function.Supplier;

/**
 * Implementation of {@link ConfigValue}, which parses and returns a collection of items.
 * @param <T> the collection type.
 * @param <S> the item type.
 */
public class DefaultCollectionConfigValue<S, T extends Collection<S>> extends DefaultConfigValue<T> {

    /** Supplier that creates the collection instances to use. */
    private Supplier<T> collectionSupplier;

    /**
     * Create a new instance.
     * @param configValue the original config value.
     * @param targetClass the containing item class, used for converter lookup if no custom converter
     *                    is set.
     */
    public DefaultCollectionConfigValue(DefaultConfigValue<T> configValue, Class targetClass, Supplier<T> collectionSupplier) {
        super(configValue, targetClass);
        this.collectionSupplier = Objects.requireNonNull(collectionSupplier);
    }

    @Override
    public Optional<T> getOptionalValue() {
        String value = textValue;
        if(value==null){
            if(defaultValue!=null){
                value = defaultTextValue;
            }
        }
        if(value==null) {
            return Optional.ofNullable(defaultValue);
        }
        String[] parsedItems = parseItems(value);
        T items = collectionSupplier.get();
        Converter converter = getConverter(targetClass);
        for(String itemValue:parsedItems){
            items.add((S)converter.convert(itemValue));
        }
        return Optional.of(items);
    }

    /**
     * Parses the input value as a comma separated list.
     * @param value
     * @return
     */
    private static String[] parseItems(String value) {
        return value.split(",");
    }

}
