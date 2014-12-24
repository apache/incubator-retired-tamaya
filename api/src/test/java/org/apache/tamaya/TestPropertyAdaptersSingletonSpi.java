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

import org.apache.tamaya.annotation.WithCodec;
import org.apache.tamaya.spi.CodecSpi;

/**
 * Test implementation current {@link org.apache.tamaya.spi.CodecSpi}, which provides codecs
 * for some basic types.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class TestPropertyAdaptersSingletonSpi implements CodecSpi {

	private Map<Class, Codec<?>> codecs = new ConcurrentHashMap<>();

    private TestPropertyAdaptersSingletonSpi(){
        register(char.class, (s) -> s.charAt(0), (ch) -> String.valueOf(ch));
        register(int.class, Integer::parseInt, Object::toString);
        register(byte.class, Byte::parseByte, Object::toString);
        register(short.class, Short::parseShort, Object::toString);
        register(boolean.class, Boolean::parseBoolean, b -> String.valueOf(b));
        register(float.class, Float::parseFloat, f -> String.valueOf(f));
        register(double.class, Double::parseDouble, d -> String.valueOf(d));

        register(Character.class, (s) -> s.charAt(0), Object::toString);
        register(Integer.class, Integer::valueOf, Object::toString);
        register(Byte.class, Byte::valueOf, Object::toString);
        register(Short.class, Short::valueOf, String::valueOf);
        register(Boolean.class, Boolean::valueOf, String::valueOf);
        register(Float.class, Float::valueOf, String::valueOf);
        register(Double.class, Double::valueOf, String::valueOf);
        register(BigDecimal.class, BigDecimal::new, String::valueOf);
        register(BigInteger.class, BigInteger::new, String::valueOf);

        register(Currency.class, Currency::getInstance, Object::toString);

        register(LocalDate.class, LocalDate::parse, Object::toString);
        register(LocalTime.class, LocalTime::parse, Object::toString);
        register(LocalDateTime.class, LocalDateTime::parse, Object::toString);
        register(ZoneId.class, ZoneId::of, ZoneId::getId);
    }


	@Override
    public <T> Codec<T> register(Class<T> targetType, Codec<T> codec){
        Objects.requireNonNull(targetType);
        Objects.requireNonNull(codec);
        return (Codec<T>) codecs.put(targetType, codec);
    }

    @Override
    public <T> Codec<T> getCodec(Class<T> targetType, WithCodec annotation){
        if(annotation!=null){
            Class<?> adapterType = annotation.value();
            if(!adapterType.equals(Codec.class)){
                try{
                    return (Codec<T>)adapterType.newInstance();
                }
                catch(Exception e){
                    throw new ConfigException("Failed to load PropertyAdapter: " + adapterType, e);
                }
            }
        }
        return (Codec<T>) codecs.get(targetType);
    }

    @Override
    public boolean isTargetTypeSupported(Class<?> targetType){
        return codecs.containsKey(targetType);
    }
}
