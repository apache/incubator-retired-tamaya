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
package org.apache.tamaya.core.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Logger;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertyConverter;

/**
 * Manager that deals with {@link org.apache.tamaya.spi.PropertyConverter} instances.
 * This class is thread-safe.
 */
public class PropertyConverterManager {
    /** The logger used. */
    private static final Logger LOG = Logger.getLogger(PropertyConverterManager.class.getName());
    /** The registered converters. */
    private Map<Class<?>, List<PropertyConverter<?>>> converters = new ConcurrentHashMap<>();
    /** The lock used. */
    private StampedLock lock = new StampedLock();

    /**
     * Constructor.
     */
    public PropertyConverterManager() {
        initDefaultConverters();
    }

    /**
     * Registers the default converters provided out of the box.
     */
    protected void initDefaultConverters() {
        // Add default converters
        register(char.class, (s) -> s != null ? s.charAt(0) : null);
        register(byte.class, Byte::parseByte);
        register(short.class, Short::parseShort);
        register(int.class, Integer::parseInt);
        register(long.class, Long::parseLong);
        register(boolean.class, Boolean::parseBoolean);
        register(float.class, Float::parseFloat); //X TODO not good enough as this is Locale dependent!
        register(double.class, Double::parseDouble); //X TODO not good enough as this is Locale dependent!

        register(Character.class, (s) -> s != null ? s.charAt(0) : null );
        register(Byte.class, Byte::valueOf);
        register(Short.class, Short::valueOf);
        register(Integer.class, Integer::valueOf);
        register(Long.class, Long::valueOf);
        register(Boolean.class, Boolean::valueOf);
        register(Float.class, Float::valueOf); //X TODO not good enough as this is Locale dependent!
        register(Double.class, Double::valueOf); //X TODO not good enough as this is Locale dependent!
        register(BigDecimal.class, BigDecimal::new); //X TODO not good enough as this is Locale dependent!
        register(BigInteger.class, BigInteger::new); //X TODO not good enough as this is Locale dependent!

        register(Currency.class, Currency::getInstance);

        register(LocalDate.class, LocalDate::parse);
        register(LocalTime.class, LocalTime::parse);
        register(LocalDateTime.class, LocalDateTime::parse);
        register(ZoneId.class, ZoneId::of);
    }

    /**
     * Registers a ew converter instance.
     *
     * @param targetType the target type, not null.
     * @param converter  the converter, not null.
     * @param <T>        the type.
     */
    public <T> void register(Class<T> targetType, PropertyConverter<T> converter) {
        Objects.requireNonNull(converter);
        Lock writeLock = lock.asWriteLock();
        try {
            writeLock.lock();
            List<PropertyConverter<T>> converters = List.class.cast(this.converters.get(targetType));
            List<PropertyConverter<T>> newConverters = new ArrayList<>();
            if (converters != null) {
                newConverters.addAll(converters);
            }
            newConverters.add(converter);
            this.converters.put(targetType, Collections.unmodifiableList(newConverters));
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Allows to evaluate if a given target type is supported.
     *
     * @param targetType the target type, not null.
     * @return true, if a converter for the given type is registered, or a default one can be created.
     */
    public boolean isTargetTypeSupported(Class<?> targetType) {
        return converters.containsKey(targetType)
                || createDefaultPropertyConverter(targetType) != null;
    }

    /**
     * Get a map of all property converters currently registered. This will not contain the converters that
     * may be created, when an instance is adapted, which provides a String constructor or compatible
     * factory methods taking a single String instance.
     *
     * @return the current map of instantiated and registered converters.
     * @see #createDefaultPropertyConverter(Class)
     */
    public Map<Class<?>, List<PropertyConverter<?>>> getPropertyConverters() {
        Lock readLock = lock.asReadLock();
        try {
            readLock.lock();
            return new HashMap<>(this.converters);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get the list of all current registered converters for the given target type.
     * If not converters are registered, they component tries to create and register a dynamic
     * converter based on String costructor or static factory methods available.
     *
     * @param targetType the target type, not null.
     * @param <T>        the type class
     * @return the ordered list of converters (may be empty for not convertible types).
     * @see #createDefaultPropertyConverter(Class)
     */
    public <T> List<PropertyConverter<T>> getPropertyConverters(Class<T> targetType) {
        Lock readLock = lock.asReadLock();
        List<PropertyConverter<T>> converters;
        try {
            readLock.lock();
            converters = List.class.cast(this.converters.get(targetType));
        } finally {
            readLock.unlock();
        }
        if (converters != null) {
            return converters;
        }
        PropertyConverter<T> defaultConverter = createDefaultPropertyConverter(targetType);
        if (defaultConverter != null) {
            register(targetType, defaultConverter);
            try {
                converters = List.class.cast(this.converters.get(targetType));
            } finally {
                readLock.unlock();
            }
        }
        if (converters != null) {
            return converters;
        }
        return Collections.emptyList();
    }

    /**
     * Creates a dynamic PropertyConverter for the given target type.
     *
     * @param targetType the target type
     * @param <T>        the type class
     * @return a new converter, or null.
     */
    protected <T> PropertyConverter<T> createDefaultPropertyConverter(Class<T> targetType) {
        PropertyConverter<T> converter = null;
        Method factoryMethod = getFactoryMethod(targetType, "of", "valueOf", "instanceOf", "getInstance", "from", "fromString", "parse");
        if (factoryMethod != null) {
            converter = (s) -> {
                try {
                    factoryMethod.setAccessible(true);
                    return targetType.cast(factoryMethod.invoke(s));
                } catch (Exception e) {
                    throw new ConfigException("Failed to decode '" + s + "'", e);
                }
            };
        }
        if (converter == null) {
            try {
                Constructor<T> constr = targetType.getDeclaredConstructor(String.class);
                converter = (s) -> {
                    try {
                        constr.setAccessible(true);
                        return constr.newInstance(s);
                    } catch (Exception e) {
                        throw new ConfigException("Failed to decode '" + s + "'", e);
                    }
                };
            } catch (Exception e) {
                LOG.finest(() -> "Failed to construct instance of type: " + targetType.getName()+": " + e);
            }
        }
        return converter;
    }

    /**
     * Tries to evaluate a factory method that can be used to create an instance based on a String.
     *
     * @param type        the target type
     * @param methodNames the possible static method names
     * @return the first method found, or null.
     */
    private Method getFactoryMethod(Class<?> type, String... methodNames) {
        Method m;
        for (String name : methodNames) {
            try {
                m = type.getDeclaredMethod(name, String.class);
                return m;
            } catch (Exception e) {
                LOG.finest(() -> "No such factory method found on type: " + type.getName()+", methodName: " + name);
            }
        }
        return null;
    }

}
