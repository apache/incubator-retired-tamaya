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
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Logger;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.core.internal.converters.EnumConverter;
import org.apache.tamaya.PropertyConverter;
import org.apache.tamaya.spi.ServiceContextManager;

/**
 * Manager that deals with {@link org.apache.tamaya.PropertyConverter} instances.
 * This class is thread-safe.
 */
public class PropertyConverterManager {
    /** The logger used. */
    private static final Logger LOG = Logger.getLogger(PropertyConverterManager.class.getName());
    /** The registered converters. */
    private Map<TypeLiteral<?>, List<PropertyConverter<?>>> converters = new ConcurrentHashMap<>();
    /** The lock used. */
    private StampedLock lock = new StampedLock();
    /**
     * Constructor.
     */
    public PropertyConverterManager() {
        this(true);
    }

    public PropertyConverterManager(boolean init) {
        if (init) {
            initConverters();
        }
    }

    /**
     * Registers the default converters provided out of the box.
     */
    protected void initConverters() {
        for(PropertyConverter conv: ServiceContextManager.getServiceContext().getServices(PropertyConverter.class)){
            Type type = TypeLiteral.getTypeParameter(conv.getClass(), PropertyConverter.class);
            register(TypeLiteral.of(type), conv);
        }
    }

    /**
     * Registers a ew converter instance.
     *
     * @param targetType the target type, not null.
     * @param converter  the converter, not null.
     * @param <T>        the type.
     */
    public <T> void register(TypeLiteral<T> targetType, PropertyConverter<T> converter) {
        Objects.requireNonNull(converter);
        Lock writeLock = lock.asWriteLock();
        try {
            writeLock.lock();
            List converters = List.class.cast(this.converters.get(targetType));
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
    public boolean isTargetTypeSupported(TypeLiteral<?> targetType) {
        return converters.containsKey(targetType)
                || createDefaultPropertyConverter(targetType) != null;
    }

    /**
     * Get a map of all property converters currently registered. This will not contain the converters that
     * may be created, when an instance is adapted, which provides a String constructor or compatible
     * factory methods taking a single String instance.
     *
     * @return the current map of instantiated and registered converters.
     * @see #createDefaultPropertyConverter(org.apache.tamaya.TypeLiteral)
     */
    public Map<TypeLiteral<?>, List<PropertyConverter<?>>> getPropertyConverters() {
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
     * @see #createDefaultPropertyConverter(org.apache.tamaya.TypeLiteral)
     */
    public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> targetType) {
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
        TypeLiteral<T> boxedType = mapBoxedType(targetType);
        if(boxedType!=null){
            try {
                readLock.lock();
                converters = List.class.cast(this.converters.get(boxedType));
            } finally {
                readLock.unlock();
            }
            if (converters != null) {
                return converters;
            }
        }
        PropertyConverter<T> defaultConverter = createDefaultPropertyConverter(targetType);
        if (defaultConverter != null) {
            register(targetType, defaultConverter);
            try {
                readLock.lock();
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
     * Maps native types to the corresponding boxed types.
     * @param targetType the native type.
     * @param <T> the type
     * @return the boxed type, or null.
     */
    private <T> TypeLiteral<T> mapBoxedType(TypeLiteral<T> targetType) {
        Type parameterType = targetType.getType();
        if(parameterType == int.class){
            return TypeLiteral.of(Integer.class);
        }
        if(parameterType == short.class){
            return TypeLiteral.of(Short.class);
        }
        if(parameterType == byte.class){
            return TypeLiteral.of(Byte.class);
        }
        if(parameterType == long.class){
            return TypeLiteral.of(Long.class);
        }
        if(parameterType == boolean.class){
            return TypeLiteral.of(Boolean.class);
        }
        if(parameterType == char.class){
            return TypeLiteral.of(Character.class);
        }
        if(parameterType == float.class){
            return TypeLiteral.of(Float.class);
        }
        if(parameterType == double.class){
            return TypeLiteral.of(Double.class);
        }
        if(parameterType == int[].class){
            return TypeLiteral.of(Integer[].class);
        }
        if(parameterType == short[].class){
            return TypeLiteral.of(Short[].class);
        }
        if(parameterType == byte[].class){
            return TypeLiteral.of(Byte[].class);
        }
        if(parameterType == long[].class){
            return TypeLiteral.of(Long[].class);
        }
        if(parameterType == boolean.class){
            return TypeLiteral.of(Boolean.class);
        }
        if(parameterType == char[].class){
            return TypeLiteral.of(Character[].class);
        }
        if(parameterType == float[].class){
            return TypeLiteral.of(Float[].class);
        }
        if(parameterType == double[].class){
            return TypeLiteral.of(Double[].class);
        }
        return null;
    }

    /**
     * Creates a dynamic PropertyConverter for the given target type.
     *
     * @param targetType the target type
     * @param <T>        the type class
     * @return a new converter, or null.
     */
    protected <T> PropertyConverter<T> createDefaultPropertyConverter(TypeLiteral<T> targetType) {
        if(Enum.class.isAssignableFrom(targetType.getRawType())){
            return new EnumConverter<>(targetType.getRawType());
        }
        PropertyConverter<T> converter = null;
        Method factoryMethod = getFactoryMethod(targetType.getRawType(), "of", "valueOf", "instanceOf", "getInstance", "from", "fromString", "parse");
        if (factoryMethod != null) {
            converter = (value) -> {
                    try {
                        if (!Modifier.isStatic(factoryMethod.getModifiers())) {
                            throw new RuntimeException(factoryMethod.toGenericString() +
                                                       " is not a static method. Only static " +
                                                       "methods can be used as factory methods.");
                        }

                        factoryMethod.setAccessible(true);

                        Object invoke = factoryMethod.invoke(null, value);
                        return targetType.getRawType().cast(invoke);
                    } catch (Exception e) {
                        throw new ConfigException("Failed to decode '" + value + "'", e);
                    }
            };
        }
        if (converter == null) {
            try {
                Constructor<T> constr = targetType.getRawType().getDeclaredConstructor(String.class);
                converter = (value) -> {
                    try {
                        constr.setAccessible(true);
                        return constr.newInstance(value);
                    } catch (Exception e) {
                        throw new ConfigException("Failed to decode '" + value + "'", e);
                    }
                };
            } catch (Exception e) {
                LOG.finest(() -> "Failed to construct instance of type: " + targetType.getRawType().getName()+": " + e);
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
            } catch (NoSuchMethodException | RuntimeException e) {
                LOG.finest(() -> "No such factory method found on type: " + type.getName()+", methodName: " + name);
            }
        }
        return null;
    }

}
