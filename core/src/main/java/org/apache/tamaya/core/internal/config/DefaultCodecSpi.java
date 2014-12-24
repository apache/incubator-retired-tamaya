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
package org.apache.tamaya.core.internal.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.apache.tamaya.Codec;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.annotation.WithCodec;
import org.apache.tamaya.spi.CodecSpi;

/**
 * Default codecs singleton, which provides default codesc for all kind of classes out of the box, which will be
 * instantiatable from configuration, if one of the following is given:
 * <ul>
 *     <li>static factory methods using a String as simgle argument, called {@code of, valueOf, getInstance, instance, parse}</li>
 *     <li>have constructors taking a single String</li>
 * </ul>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultCodecSpi implements CodecSpi {


	private Map<Class,Codec> adapters = new ConcurrentHashMap<>();

    public DefaultCodecSpi(){
        // Add default adapters
        register(char.class, (s) -> s.charAt(0), (ch) -> String.valueOf(ch));
        register(byte.class, Byte::parseByte, Object::toString);
        register(short.class, Short::parseShort, Object::toString);
        register(int.class, Integer::parseInt, Object::toString);
        register(long.class, Long::parseLong, Object::toString);
        register(boolean.class, Boolean::parseBoolean, b -> String.valueOf(b));
        register(float.class, Float::parseFloat, f -> String.valueOf(f));
        register(double.class, Double::parseDouble, d -> String.valueOf(d));

        register(Character.class, (s) -> s.charAt(0), Object::toString);
        register(Byte.class, Byte::valueOf, Object::toString);
        register(Short.class, Short::valueOf, String::valueOf);
        register(Integer.class, Integer::valueOf, Object::toString);
        register(Long.class, Long::valueOf, Object::toString);
        register(Boolean.class, Boolean::valueOf, b -> String.valueOf(b));
        register(Float.class, Float::valueOf, f -> String.valueOf(f));
        register(Double.class, Double::valueOf, d -> String.valueOf(d));
        register(BigDecimal.class, BigDecimal::new, String::valueOf);
        register(BigInteger.class, BigInteger::new, String::valueOf);

        register(Currency.class, Currency::getInstance, Object::toString);

        register(LocalDate.class, LocalDate::parse, Object::toString);
        register(LocalTime.class, LocalTime::parse, Object::toString);
        register(LocalDateTime.class, LocalDateTime::parse, Object::toString);
        register(ZoneId.class, ZoneId::of, ZoneId::getId);
    }

	@Override
    public <T> Codec<T> register(Class<T> targetType, Codec<T> adapter){
        return adapters.put(targetType, adapter);
    }

    @Override
    public <T> Codec<T> getCodec(Class<T> targetType, WithCodec adapterAnnot){
        Codec codec = null;
        Class<? extends Codec> configuredCodec = null;
        if(adapterAnnot != null){
            configuredCodec = adapterAnnot.value();
            if(!configuredCodec.equals(Codec.class)){
                try{
                    codec = configuredCodec.newInstance();
                }
                catch(Exception e){
                    throw new ConfigException("Invalid codec configured.", e);
                }
            }
        }
        if(codec == null){
            codec = adapters.get(targetType);
        }
        if(codec == null){
            codec = getDefaultCodec(targetType);
        }
        if(codec == null){
            throw new ConfigException("No Codec found for " + targetType.getName());
        }
        return codec;
    }

    private <T> Codec getDefaultCodec(Class<T> targetType) {
        Function<String, T> decoder = null;
        Method factoryMethod = getFactoryMethod(targetType, "of", "valueOf", "instanceOf", "getInstance", "from", "parse");
        if(factoryMethod!=null){
            decoder = (s) -> {
                try{
                    factoryMethod.setAccessible(true);
                    return targetType.cast(factoryMethod.invoke(s));
                }
                catch (Exception e){
                    throw new ConfigException("Failed to decode '"+s+"'", e);
                }
            };
        }
        if(decoder==null) {
            try {
                Constructor<T> constr = targetType.getDeclaredConstructor(String.class);
                decoder = (s) -> {
                    try{
                        constr.setAccessible(true);
                        return constr.newInstance(s);
                    }
                    catch (Exception e){
                        throw new ConfigException("Failed to decode '"+s+"'", e);
                    }
                };
            } catch (Exception e) {
                // ignore, TODO log finest
            }
        }
        if(decoder!=null) {
            return register(targetType, decoder, String::valueOf);
        }
        return null;
    }

    private Method getFactoryMethod(Class<?> type, String... methodNames) {
        Method m;
        for(String name:methodNames){
            try{
                m  = type.getDeclaredMethod(name, String.class);
                return m;
            }
            catch(Exception e){
                // ignore, TODO log finest
            }
        }
        return null;
    }

    @Override
    public boolean isTargetTypeSupported(Class<?> targetType){
        return adapters.containsKey(targetType);
    }
}
