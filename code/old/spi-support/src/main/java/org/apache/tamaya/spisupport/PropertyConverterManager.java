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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.ServiceContextManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager that deals with {@link PropertyConverter} instances.
 * This class is thread-safe.
 */
public class PropertyConverterManager {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(PropertyConverterManager.class.getName());
    /**
     * The registered converters.
     */
    private final Map<TypeLiteral<?>, List<PropertyConverter<?>>> converters = new ConcurrentHashMap<>();
    /**
     * The transitive converters.
     */
    private final Map<TypeLiteral<?>, List<PropertyConverter<?>>> transitiveConverters = new ConcurrentHashMap<>();
    /**
     * The lock used.
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private static final Comparator<Object> PRIORITY_COMPARATOR = new Comparator<Object>() {

        @Override
        public int compare(Object o1, Object o2) {
            int prio = PriorityServiceComparator.getPriority(o1) - PriorityServiceComparator.getPriority(o2);
            if (prio < 0) {
                return 1;
            } else if (prio > 0) {
                return -1;
            } else {
                return o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
            }
        }
    };

    /**
     * Constructor.
     */
    public PropertyConverterManager() {
        this(false);
    }

    public PropertyConverterManager(boolean init) {
        if (init) {
            initConverters();
        }
    }

    /**
     * Registers the default converters provided out of the box.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void initConverters() {
        for (PropertyConverter conv : ServiceContextManager.getServiceContext().getServices(PropertyConverter.class)) {
            Type type = TypeLiteral.getGenericInterfaceTypeParameters(conv.getClass(), PropertyConverter.class)[0];
            register(TypeLiteral.of(type), conv);
        }
    }

    /**
     * Registers a new converters instance.
     *
     * @param targetType the target type, not {@code null}.
     * @param converter  the converters, not {@code null}.
     * @param <T>        the type.
     */
    @SuppressWarnings("unchecked")
    public <T> void register(TypeLiteral<T> targetType, PropertyConverter<T> converter) {
        Objects.requireNonNull(converter);
        Lock writeLock = lock.writeLock();
        try {
            writeLock.lock();
			List<PropertyConverter<?>> converters = List.class.cast(this.converters.get(targetType));
            if(converters!=null && converters.contains(converter)){
                return;
            }
            List<PropertyConverter<?>> newConverters = new ArrayList<>();
            if (converters != null) {
                newConverters.addAll(converters);
            }
            if(!newConverters.contains(converter)) {
                newConverters.add(converter);
            }
            Collections.sort(newConverters, PRIORITY_COMPARATOR);
            this.converters.put(targetType, Collections.unmodifiableList(newConverters));
            // evaluate transitive closure for all inherited supertypes and implemented interfaces
            // direct implemented interfaces
            for (Class<?> ifaceType : targetType.getRawType().getInterfaces()) {
                converters = List.class.cast(this.transitiveConverters.get(TypeLiteral.of(ifaceType)));
                newConverters = new ArrayList<>();
                if (converters != null) {
                    newConverters.addAll(converters);
                }
                newConverters.add(converter);
                Collections.sort(newConverters, PRIORITY_COMPARATOR);
                this.transitiveConverters.put(TypeLiteral.of(ifaceType), Collections.unmodifiableList(newConverters));
            }
            Class<?> superClass = targetType.getRawType().getSuperclass();
            while (superClass != null && !superClass.equals(Object.class)) {
                converters = List.class.cast(this.transitiveConverters.get(TypeLiteral.of(superClass)));
                newConverters = new ArrayList<>();
                if (converters != null) {
                    newConverters.addAll(converters);
                }
                newConverters.add(converter);
                Collections.sort(newConverters, PRIORITY_COMPARATOR);
                this.transitiveConverters.put(TypeLiteral.of(superClass), Collections.unmodifiableList(newConverters));
                for (Class<?> ifaceType : superClass.getInterfaces()) {
                    converters = List.class.cast(this.transitiveConverters.get(TypeLiteral.of(ifaceType)));
                    newConverters = new ArrayList<>();
                    if (converters != null) {
                        newConverters.addAll(converters);
                    }
                    newConverters.add(converter);
                    Collections.sort(newConverters, PRIORITY_COMPARATOR);
                    this.transitiveConverters.put(TypeLiteral.of(ifaceType), Collections.unmodifiableList(newConverters));
                }
                superClass = superClass.getSuperclass();
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Allows to evaluate if a given target type is supported.
     *
     * @param targetType the target type, not {@code null}.
     * @return true, if a converter for the given type is registered or a default one can be created.
     */
    public boolean isTargetTypeSupported(TypeLiteral<?> targetType) {
        return converters.containsKey(targetType) || transitiveConverters.containsKey(targetType) || createDefaultPropertyConverter(targetType) != null;
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
        Lock readLock = lock.readLock();
        try {
            readLock.lock();
            return new HashMap<>(this.converters);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Get the list of all current registered converters for the given target type.
     *
     * <p>
     * If no converters are registered, the component tries to create and register dynamic
     * converters based on String constructor or static factory methods available.
     * The converters provided are of the following type and returned in the following order:
     * <ul>
     * <li>Converters mapped explicitly to the required target type are returned first, ordered
     * by decreasing priority. This means, if explicit converters are registered these are used
     * primarily for converting a value.</li>
     * <li>The target type of each explicitly registered converter can also be transitively mapped to
     * 1) all directly implemented interfaces, 2) all its superclasses (except Object), 3) all the interfaces
     * implemented by its superclasses. This group of transitive converters is returned similarly in the
     * order already mentioned, whereas here a priority based on decreasing ordering is applied.</li>
     * <li>java.lang wrapper classes and native types are automatically mapped.</li>
     * <li>If no explicit converters are registered, for Enum types a default implementation is provided that
     * compares the configuration values with the different enum members defined (case sensitive mapping).</li>
     * </ul>
     * <p>
     * The above list of directly registered mappings are always tried first, before any transitive mapping
     * should be used. Also in all cases @Priority annotations are honored for ordering of the converters in place.
     * Transitive conversion is supported for all directly implemented interfaces (including inherited ones) and
     * the inheritance hierarchy (exception Object). Superinterfaces of implemented interfaces are ignored.
     *
     * @param targetType the target type, not {@code null}.
     * @param <T>        the type class
     * @return the ordered list of converters (may be empty for not convertible types).
     * @see #createDefaultPropertyConverter(org.apache.tamaya.TypeLiteral)
     */
    public <T> List<PropertyConverter<T>> getPropertyConverters(TypeLiteral<T> targetType) {
        Lock readLock = lock.readLock();
        List<PropertyConverter<T>> converterList = new ArrayList<>();
        // direct mapped converters
        try {
            readLock.lock();
            addConvertersToList(List.class.cast(this.converters.get(targetType)), converterList);
            addConvertersToList(List.class.cast(this.transitiveConverters.get(targetType)), converterList);
        } finally {
            readLock.unlock();
        }
        // handling of java.lang wrapper classes
        TypeLiteral<T> boxedType = mapBoxedType(targetType);
        if (boxedType != null) {
            try {
                readLock.lock();
                addConvertersToList(List.class.cast(this.converters.get(boxedType)), converterList);
            } finally {
                readLock.unlock();
            }
        }
        if (converterList.isEmpty() && !TypeLiteral.of(String.class).equals(targetType)) {
            // adding any converters created on the fly, e.g. for enum types.
            PropertyConverter<T> defaultConverter = createDefaultPropertyConverter(targetType);
            if (defaultConverter != null) {
                register(targetType, defaultConverter);
                try {
                    readLock.lock();
                    addConvertersToList(List.class.cast(this.converters.get(targetType)), converterList);
                } finally {
                    readLock.unlock();
                }
            }
        }
        // check for parametrized types, ignoring param type
        // direct mapped converters
        if(targetType.getType()!=null) {
            try {
                readLock.lock();
                addConvertersToList(List.class.cast(this.converters.get(
                        TypeLiteral.of(targetType.getRawType()))), converterList);
            } finally {
                readLock.unlock();
            }
        }
        return converterList;
    }

    private <T> void addConvertersToList(Collection<PropertyConverter<T>> converters, List<PropertyConverter<T>> converterList) {
        if (converters != null) {
            for(PropertyConverter<T> conv:converters) {
                if(!converterList.contains(conv)) {
                    converterList.add(conv);
                }
            }
        }
    }

    /**
     * Maps native types to the corresponding boxed types.
     *
     * @param targetType the native type.
     * @param <T>        the type
     * @return the boxed type, or null.
     */
    @SuppressWarnings("unchecked")
	private <T> TypeLiteral<T> mapBoxedType(TypeLiteral<T> targetType) {
        Type parameterType = targetType.getType();
        if (parameterType == int.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Integer.class));
        }
        if (parameterType == short.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Short.class));
        }
        if (parameterType == byte.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Byte.class));
        }
        if (parameterType == long.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Long.class));
        }
        if (parameterType == boolean.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Boolean.class));
        }
        if (parameterType == char.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Character.class));
        }
        if (parameterType == float.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Float.class));
        }
        if (parameterType == double.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Double.class));
        }
        if (parameterType == int[].class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Integer[].class));
        }
        if (parameterType == short[].class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Short[].class));
        }
        if (parameterType == byte[].class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Byte[].class));
        }
        if (parameterType == long[].class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Long[].class));
        }
        if (parameterType == boolean.class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Boolean.class));
        }
        if (parameterType == char[].class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Character[].class));
        }
        if (parameterType == float[].class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Float[].class));
        }
        if (parameterType == double[].class) {
            return TypeLiteral.class.cast(TypeLiteral.of(Double[].class));
        }
        return null;
    }

    /**
     * Creates a dynamic {@link PropertyConverter} for the given target type.
     *
     * @param targetType the target type
     * @param <T>        the type class
     * @return a new converters, or null.
     */
    protected <T> PropertyConverter<T> createDefaultPropertyConverter(final TypeLiteral<T> targetType) {
        if (Enum.class.isAssignableFrom(targetType.getRawType())) {
            return new EnumConverter<>(targetType.getRawType());
        }
        PropertyConverter<T> converter = null;
        final Method factoryMethod = getFactoryMethod(targetType.getRawType(), "of", "valueOf", "instanceOf", "getInstance", "from", "fromString", "parse");
        if (factoryMethod != null) {
            converter = new DefaultPropertyConverter<>(factoryMethod, targetType.getRawType());
        }
        if (converter == null) {
            final Constructor<T> constr;
            try {
                constr = targetType.getRawType().getDeclaredConstructor(String.class);
            } catch (NoSuchMethodException e) {
                LOG.log(Level.FINEST, "No matching constrctor for " + targetType, e);
                return null;
            }
            converter = new PropertyConverter<T>() {
                    @Override
                    public T convert(String value, ConversionContext context) {
                        AccessController.doPrivileged(new PrivilegedAction<Object>() {
                            @Override
                            public Object run() {
                                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                                    @Override
                                    public Object run() {
                                        constr.setAccessible(true);
                                        return null;
                                    }
                                });
                                return null;
                            }
                        });
                        try {
                            return constr.newInstance(value);
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, "Error creating new PropertyConverter instance " + targetType, e);
                        }
                        return null;
                    }
                };
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
                LOG.finest("No such factory method found on type: " + type.getName() + ", methodName: " + name);
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyConverterManager)) {
            return false;
        }
        PropertyConverterManager that = (PropertyConverterManager) o;
        return converters.equals(that.converters);

    }

    @Override
    public int hashCode() {
        return converters.hashCode();
    }

    /**
     * Default converter implementation performing several lookups for String conversion
     * options.
     * @param <T>
     */
    private static class DefaultPropertyConverter<T> implements PropertyConverter<T> {

        private final Method factoryMethod;
        private final Class<T> targetType;

        DefaultPropertyConverter(Method factoryMethod, Class<T> targetType){
            this.factoryMethod = Objects.requireNonNull(factoryMethod);
            this.targetType =  Objects.requireNonNull(targetType);
        }

        @Override
        public T convert(String value, ConversionContext context) {
            context.addSupportedFormats(getClass(), "<String -> "+factoryMethod.toGenericString());

            if (!Modifier.isStatic(factoryMethod.getModifiers())) {
                throw new ConfigException(factoryMethod.toGenericString() +
                        " is not a static method. Only static " +
                        "methods can be used as factory methods.");
            }
            try {
                AccessController.doPrivileged(new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        factoryMethod.setAccessible(true);
                        return null;
                    }
                });
                Object invoke = factoryMethod.invoke(null, value);
                return targetType.cast(invoke);
            } catch (Exception e) {
                throw new ConfigException("Failed to decode '" + value + "'", e);
            }
        }
    }
}
