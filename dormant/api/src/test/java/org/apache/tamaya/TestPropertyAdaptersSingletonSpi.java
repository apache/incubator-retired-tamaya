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
package org.apache.tamaya;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tamaya.annotation.WithPropertyAdapter;
import org.apache.tamaya.spi.PropertyAdapterSpi;

/**
 * Test implementation current {@link org.apache.tamaya.spi.PropertyAdapterSpi}, which provides propertyAdapters
 * for some basic types.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class TestPropertyAdaptersSingletonSpi implements PropertyAdapterSpi {

	private Map<Class, PropertyAdapter<?>> propertyAdapters = new ConcurrentHashMap<>();

    private TestPropertyAdaptersSingletonSpi(){
        register(char.class, (s) -> s.charAt(0));
        register(int.class, Integer::parseInt);
        register(byte.class, Byte::parseByte);
        register(short.class, Short::parseShort);
        register(boolean.class, Boolean::parseBoolean);
        register(float.class, Float::parseFloat);
        register(double.class, Double::parseDouble);

        register(Character.class, (s) -> s.charAt(0));
        register(Integer.class, Integer::valueOf);
        register(Byte.class, Byte::valueOf);
        register(Short.class, Short::valueOf);
        register(Boolean.class, Boolean::valueOf);
        register(Float.class, Float::valueOf);
        register(Double.class, Double::valueOf);
        register(BigDecimal.class, BigDecimal::new);
        register(BigInteger.class, BigInteger::new);

        register(Currency.class, Currency::getInstance);

        register(LocalDate.class, LocalDate::parse);
        register(LocalTime.class, LocalTime::parse);
        register(LocalDateTime.class, LocalDateTime::parse);
        register(ZoneId.class, ZoneId::of);
    }


	@Override
    public <T> PropertyAdapter<T> register(Class<T> targetType, PropertyAdapter<T> codec){
        Objects.requireNonNull(targetType);
        Objects.requireNonNull(codec);
        return (PropertyAdapter<T>) propertyAdapters.put(targetType, codec);
    }

    @Override
    public <T> PropertyAdapter<T> getPropertyAdapter(Class<T> targetType, WithPropertyAdapter annotation){
        if(annotation!=null){
            Class<?> adapterType = annotation.value();
            if(!adapterType.equals(PropertyAdapter.class)){
                try{
                    return (PropertyAdapter<T>)adapterType.newInstance();
                }
                catch(Exception e){
                    throw new ConfigException("Failed to load PropertyAdapter: " + adapterType, e);
                }
            }
        }
        return (PropertyAdapter<T>) propertyAdapters.get(targetType);
    }

    @Override
    public boolean isTargetTypeSupported(Class<?> targetType){
        return propertyAdapters.containsKey(targetType);
    }
}
