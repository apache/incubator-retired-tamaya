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
package org.apache.tamaya.core.internal.properties;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.PropertyAdapter;
import org.apache.tamaya.mapping.WithPropertyAdapter;
import org.apache.tamaya.spi.PropertyAdaptersSingletonSpi;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anatole on 30.09.2014.
 */
public class DefaultPropertyAdaptersSingletonSpi implements PropertyAdaptersSingletonSpi{

    private Map<Class,PropertyAdapter> adapters = new ConcurrentHashMap<>();

    public DefaultPropertyAdaptersSingletonSpi(){
        // Add default adapters
        register(char.class, (s) -> s.charAt(0));
        register(int.class, Integer::parseInt);
        register(byte.class, Byte::parseByte);
        register(short.class, Short::parseShort);
        register(boolean.class, Boolean::parseBoolean);
        register(float.class, Float::parseFloat);
        register(double.class, Double::parseDouble);

        register(Character.class, (s) -> s.charAt(0));
        register(Integer.class, Integer::parseInt);
        register(Byte.class, Byte::parseByte);
        register(Short.class, Short::parseShort);
        register(Boolean.class, Boolean::parseBoolean);
        register(Float.class, Float::parseFloat);
        register(Double.class, Double::parseDouble);
        register(BigDecimal.class, BigDecimal::new);
        register(BigInteger.class, BigInteger::new);

        register(Currency.class, Currency::getInstance);

        register(LocalDate.class, LocalDate::parse);
        register(LocalTime.class, LocalTime::parse);
        register(LocalDateTime.class, LocalDateTime::parse);
        register(ZoneId.class, ZoneId::of);
    }

    @Override
    public <T> PropertyAdapter<T> register(Class<T> targetType, PropertyAdapter<T> adapter){
        return adapters.put(targetType, adapter);
    }

    @Override
    public <T> PropertyAdapter<T> getAdapter(Class<T> targetType, WithPropertyAdapter adapterAnnot){
        PropertyAdapter adapter = null;
        Class<? extends PropertyAdapter> configuredAdapter = null;
        if(adapterAnnot != null){
            configuredAdapter = adapterAnnot.value();
            if(!configuredAdapter.equals(PropertyAdapter.class)){
                try{
                    adapter = configuredAdapter.newInstance();
                }
                catch(Exception e){
                    throw new ConfigException("Invalid adapter configured.", e);
                }
            }
        }
        if(adapter == null){
            adapter = adapters.get(targetType);
        }
        return adapter;
    }

    @Override
    public boolean isTargetTypeSupported(Class<?> targetType){
        return adapters.containsKey(targetType);
    }
}
