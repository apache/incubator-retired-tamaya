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
package org.apache.tamaya.base.convert;

import org.apache.tamaya.base.FormatUtils;
import org.apache.tamaya.base.PriorityServiceComparator;
import org.apache.tamaya.spi.ConfigContext;
import org.apache.tamaya.spi.ConfigContextSupplier;
import org.apache.tamaya.spi.ServiceContext;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.config.Config;
import javax.config.spi.Converter;
import java.lang.reflect.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manager that deals with {@link Converter} instances.
 * This class is thread-safe.
 */
public class ConverterManager {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(ConverterManager.class.getName());

    /**
     * The registered converters.
     */
    private final Map<Type, List<Converter>> converters = new ConcurrentHashMap<>();
    /**
     * The transitive converters.
     */
    private final Map<Type, List<Converter>> transitiveConverters = new ConcurrentHashMap<>();

    private ClassLoader classloader = ServiceContext.defaultClassLoader();

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

    private static final ConverterManager INSTANCE = new ConverterManager()
            .addCoreConverters()
            .addDiscoveredConverters();


    /**
     * Access the shared common default instance, that can be used for conversion if the current {@link Config}
     * does not implement the {@link ConfigContextSupplier} interface.
     * @return the shared instance, not null.
     */
    public static ConverterManager defaultInstance(){
        return INSTANCE;
    }

    /**
     * Get the classloader used for instance creation.
     * @return the classloader, never null.
     */
    public ClassLoader getClassloader(){
        return classloader;
    }

    /**
     * Sets the classloader to use for loading of instances.
     * @param ClassLoader the classloader, not null.
     * @return this instance for chaining.
     */
    public ConverterManager setClassloader(ClassLoader ClassLoader){
        this.classloader = Objects.requireNonNull(classloader);
        return this;
    }

    /**
     * Checks the current implemented generic interfaces and evaluates the given single type parameter.
     *
     * @param clazz         the class to check, not  {@code null}.
     * @param interfaceType the interface type to be checked, not {@code null}.
     * @return the generic type parameters, or an empty array, if it cannot be evaluated.
     */
    private Type[] getGenericInterfaceTypeParameters(Class<?> clazz, Class<?> interfaceType) {
        Objects.requireNonNull(clazz, "Class parameter must be given.");
        Objects.requireNonNull(interfaceType, "Interface parameter must be given.");

        for (Type type : clazz.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if(parameterizedType.getRawType().equals(interfaceType)){
                    return parameterizedType.getActualTypeArguments();
                }
            }
        }
        return new Type[0];
    }

    /**
     * This method can be used for adding {@link Converter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert     the type for which the converters is for
     * @param converters the converters to add for this type
     * @return this builder, for chaining, never null.
     */
    public ConverterManager addConverter(Type typeToConvert, Converter... converters) {
        return addConverter(typeToConvert, Arrays.asList(converters));
    }

    /**
     * This method can be used for adding {@link Converter}s.
     * Converters are added at the end after any existing converters.
     * For converters already registered for the current target type the
     * method has no effect.
     *
     * @param typeToConvert     the type for which the converters is for
     * @param converters the converters to add for this type
     * @return this builder, for chaining, never null.
     */
    public ConverterManager addConverter(Type typeToConvert, Collection<Converter> converters) {
        Objects.requireNonNull(converters);
        List<Converter> converterList = List.class.cast(this.converters.get(typeToConvert));
        if(converterList==null){
            converterList = new ArrayList<>();
        }else{
            converterList = new ArrayList<>(converterList);
        }
        for(Converter converter:converters) {
            if (!converterList.contains(converter)) {
                converterList.add(converter);
            }
        }
        Collections.sort(converterList, PRIORITY_COMPARATOR);
        this.converters.put(typeToConvert, Collections.unmodifiableList(converterList));
        addTransitiveConverters(typeToConvert, Collection.class.cast(converters));
        return this;
    }

    private ConverterManager addTransitiveConverters(Type typeToConvert, Collection<Converter> converters) {
        // evaluate transitive closure for all inherited supertypes and implemented interfaces
        // direct implemented interfaces
        if(typeToConvert instanceof Class) {
            Class targetClass = (Class) typeToConvert;
            for (Class<?> ifaceType : targetClass.getInterfaces()) {
                List<Converter> converterList = List.class.cast(this.transitiveConverters.get(typeToConvert));
                if(converterList==null){
                    converterList = new ArrayList<>();
                }else{
                    converterList = new ArrayList<>(converterList);
                }
                for(Converter converter:converters){
                    if(!converterList.contains(converter)){
                        converterList.add(converter);
                    }
                }
                Collections.sort(converterList, PRIORITY_COMPARATOR);
                this.transitiveConverters.put(ifaceType, Collections.unmodifiableList(converterList));
            }
            Class<?> superClass = targetClass.getSuperclass();
            while (superClass != null && !superClass.equals(Object.class)) {
                List<Converter> converterList = List.class.cast(this.transitiveConverters.get(superClass));
                if(converterList==null){
                    converterList = new ArrayList<>();
                }else{
                    converterList = new ArrayList<>(converterList);
                }
                for(Converter converter:converters){
                    if(!converterList.contains(converter)){
                        converterList.add(converter);
                    }
                }
                Collections.sort(converterList, PRIORITY_COMPARATOR);
                this.transitiveConverters.put(superClass, Collections.unmodifiableList(converterList));
                addTransitiveConverters(superClass, converters);
                superClass = superClass.getSuperclass();
            }
        }
        return this;
    }

    public ConverterManager addConverters(Converter... converters){
        return addConverters(Arrays.asList(converters));
    }

    public ConverterManager addConverters(Collection<Converter> converters){
        for(Converter conv:converters) {
            addConverter(conv);
        }
        return this;
    }

    public ConverterManager addConverter(Converter conv) {
        for (Type type : conv.getClass().getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) type;
                if (Converter.class.equals(((ParameterizedType) type).getRawType())) {
                    Type target = pt.getActualTypeArguments()[0];
                    addConverter(target, conv);
                }
            }
        }
        return this;
    }

    public ConverterManager addDiscoveredConverters() {
        addCoreConverters();
        for(Map.Entry<Type, Collection<Converter<?>>> en:getDefaultConverters().entrySet()){
            for(Converter pc: en.getValue()) {
                addConverter(en.getKey(), pc);
            }
        }
        return this;
    }

    protected Map<Type, Collection<Converter<?>>> getDefaultConverters() {
        Map<Type, Collection<Converter<?>>> result = new HashMap<>();
        for (Converter<?> conv : ServiceContextManager.getServiceContext().getServices(
                Converter.class, classloader)) {
            addConverter(conv);
        }
        return result;
    }

    protected ConverterManager addCoreConverters() {
        // should be overridden by subclasses.
        return this;
    }

    /**
     * Removes the given PropertyConverter instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converters is for
     * @param converters    the converters to remove
     * @return this builder, for chaining, never null.
     */
    public ConverterManager removeConverters(Type typeToConvert,
                                             @SuppressWarnings("unchecked") Converter... converters){
        return removeConverters(typeToConvert, Arrays.asList(converters));
    }

    /**
     * Removes the given PropertyConverter instances for the given type,
     * if existing.
     *
     * @param typeToConvert the type which the converters is for
     * @param converters    the converters to remove
     * @return this builder, for chaining, never null.
     */
    public ConverterManager removeConverters(Type typeToConvert,
                                             Collection<Converter> converters){
        Objects.requireNonNull(converters);
        List<Converter> converterList = List.class.cast(this.converters.get(typeToConvert));
        if(converterList!=null){
            converterList = new ArrayList<>(converterList);
            converterList.removeAll(converters);
        }
        Collections.sort(converterList, PRIORITY_COMPARATOR);
        this.converters.put(typeToConvert, Collections.unmodifiableList(converterList));
        return this;
    }

    /**
     * Removes all converters for the given type, which actually renders a given type
     * unsupported for type conversion.
     *
     * @param typeToConvert the type which the converters is for
     * @return this builder, for chaining, never null.
     */
    public ConverterManager removeConverters(Type typeToConvert){
        this.converters.remove(typeToConvert);
        this.transitiveConverters.remove(typeToConvert);
        return this;
    }

    /**
     * Removes all contained items.
     * @return this instance for chaining.
     */
    public ConverterManager clear() {
        this.converters.clear();
        this.transitiveConverters.clear();
        return this;
    }

    /**
     * Allows to evaluate if a given target type is supported.
     *
     * @param targetType the target type, not {@code null}.
     * @return true, if a converters for the given type is registered, or a default one can be created.
     */
    public boolean isTargetTypeSupported(Type targetType) {
        return converters.containsKey(targetType) || transitiveConverters.containsKey(targetType) || createDefaultPropertyConverter(targetType) != null;
    }

    /**
     * Get a map of all property converters currently registered. This will not contain the converters that
     * may be created, when an instance is adapted, which provides a String constructor or compatible
     * factory methods taking a single String instance.
     *
     * @return the current map of instantiated and registered converters.
     * @see #createDefaultPropertyConverter(Type)
     */
    public Map<Type, List<Converter>> getConverters() {
        return new HashMap<>(this.converters);
    }

    /**
     * Get the list of all current registered converters for the given target type.
     * If not converters are registered, they component tries to create and addSources a dynamic
     * converters based on String constructor or static factory methods available.
     * The converters provided are of the following type and returned in the following order:
     * <ul>
     * <li>Converters mapped explicitly to the required target type are returned first, ordered
     * by decreasing priority. This means, if explicit converters are registered these are used
     * primarily for converting a value.</li>
     * <li>The target type of each explicitly registered converters also can be transitively mapped to
     * 1) all directly implemented interfaces, 2) all its superclasses (except Object), 3) all the interfaces
     * implemented by its superclasses. These groups of transitive converters is returned similarly in the
     * order as mentioned, whereas also here a priority based decreasing ordering is applied.</li>
     * <li>java.lang wrapper classes and native types are automatically mapped.</li>
     * <li>If no explicit converters are registered, for Enum types a default implementation is provided that
     * compares the configuration values with the different enum members defined (cases sensitive mapping).</li>
     * </ul>
     * <p>
     * So given that list above directly registered mappings always are tried first, before any transitive mapping
     * should be used. Also in all cases @Priority annotations are honored for ordering of the converters in place.
     * Transitive conversion is supported for all directly implemented interfaces (including inherited ones) and
     * the inheritance hierarchy (exception Object). Superinterfaces of implemented interfaces are ignored.
     *
     * @param targetType the target type, not {@code null}.
     * @return the ordered list of converters (may be empty for not convertible types).
     * @see #createDefaultPropertyConverter(Type)
     */
    public List<Converter> getConverters(Type targetType) {
        List<Converter> converterList = new ArrayList<>();
        addConvertersToList(List.class.cast(this.converters.get(targetType)), converterList);
        addConvertersToList(List.class.cast(this.transitiveConverters.get(targetType)), converterList);

        // handling of java.lang wrapper classes
        Type boxedType = mapBoxedType(targetType);
        if (boxedType != null) {
            addConvertersToList(List.class.cast(this.converters.get(boxedType)), converterList);
        }
        if (converterList.isEmpty() && !String.class.equals(targetType)) {
            // adding any converters created on the fly, e.g. for enum types.
            Converter defaultConverter = createDefaultPropertyConverter(targetType);
            if (defaultConverter != null) {
                addConverter(targetType, defaultConverter);
                addConvertersToList(List.class.cast(this.converters.get(targetType)), converterList);
            }
        }
        // check for parametrized types, ignoring param type
        // direct mapped converters
        if(targetType!=null) {
            addConvertersToList(List.class.cast(this.converters.get(
                        targetType)), converterList);
        }
        return converterList;
    }

    public <T> T convertValue(String value, Type type){
        return convertValue(value, type, this.getConverters(type));
    }

    private <T> void addConvertersToList(Collection<Converter> converters, List<Converter> converterList) {
        if (converters != null) {
            for(Converter<T> conv:converters) {
                if(!converterList.contains(conv)) {
                    converterList.add(conv);
                }
            }
        }
    }

    /**
     * Maps native types to the corresponding boxed types.
     *
     * @param parameterType the native type.
     * @param <T>        the type
     * @return the boxed type, or null.
     */
    @SuppressWarnings("unchecked")
	private <T> Type mapBoxedType(Type parameterType) {
        if (parameterType == int.class) {
            return Integer.class;
        }
        if (parameterType == short.class) {
            return Short.class;
        }
        if (parameterType == byte.class) {
            return Byte.class;
        }
        if (parameterType == long.class) {
            return Long.class;
        }
        if (parameterType == boolean.class) {
            return Boolean.class;
        }
        if (parameterType == char.class) {
            return Character.class;
        }
        if (parameterType == float.class) {
            return Float.class;
        }
        if (parameterType == double.class) {
            return Double.class;
        }
        if (parameterType == int[].class) {
            return Integer[].class;
        }
        if (parameterType == short[].class) {
            return Short[].class;
        }
        if (parameterType == byte[].class) {
            return Byte[].class;
        }
        if (parameterType == long[].class) {
            return Long[].class;
        }
        if (parameterType == boolean.class) {
            return Boolean.class;
        }
        if (parameterType == char[].class) {
            return Character[].class;
        }
        if (parameterType == float[].class) {
            return Float[].class;
        }
        if (parameterType == double[].class) {
            return Double[].class;
        }
        return null;
    }

    /**
     * Creates a dynamic PropertyConverter for the given target type.
     *
     * @param targetType the target type
     * @return a new converters, or null.
     */
    protected Converter createDefaultPropertyConverter(final Type targetType) {
        if(!(targetType instanceof Class)){
            return null;
        }
        Class targetClass = (Class)targetType;
        if (Enum.class.isAssignableFrom(targetClass)) {
            return new EnumConverter<>(targetClass);
        }
        Converter converter = null;
        final Method factoryMethod = getFactoryMethod(targetClass, "of", "valueOf", "instanceOf", "getInstance", "from", "fromString", "parse");
        if (factoryMethod != null) {
            converter = new DefaultPropertyConverter<>(factoryMethod, targetClass);
        }
        if (converter == null) {
            final Constructor constr;
            try {
                constr = targetClass.getDeclaredConstructor(String.class);
            } catch (NoSuchMethodException e) {
                LOG.log(Level.FINEST, "No matching constrctor for " + targetType, e);
                return null;
            }
            converter = new Converter() {
                    @Override
                    public Object convert(String value) {
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
        if (!(o instanceof ConverterManager)) {
            return false;
        }
        ConverterManager that = (ConverterManager) o;
        return converters.equals(that.converters);

    }

    @Override
    public int hashCode() {
        return converters.hashCode();
    }

    /**
     * Converts a given text input using the given config.
     * @param textValue the text value.
     * @param type the target type.
     * @param converters the converters to be used.
     * @param <T> the type
     * @return the converted type, or null.
     */
    public <T> T convertValue(String textValue, Type type, Iterable<Converter> converters) {
        if(textValue==null){
            return null;
        }
        for(Converter<T> converter:converters){
            try{
                T t = converter.convert(textValue);
                if(t!=null){
                    return t;
                }
            }catch(Exception e){
                LOG.log(Level.FINE, e, () -> "Could not parse config value: " + textValue);
            }
        }
        if(String.class.equals(type) || CharSequence.class.equals(type)){
            return (T)textValue;
        }
        throw new IllegalArgumentException("Uncovertible config value: '" + textValue + "', target type: " + type.getTypeName());
    }

    /**
     * Default converters imüöementation perfoming several lookups for String converion
     * option.
     * @param <T>
     */
    private static class DefaultPropertyConverter<T> implements Converter<T> {

        private final Method factoryMethod;
        private final Class<T> targetType;

        DefaultPropertyConverter(Method factoryMethod, Class<T> targetType){
            this.factoryMethod = Objects.requireNonNull(factoryMethod);
            this.targetType =  Objects.requireNonNull(targetType);
        }

        @Override
        public T convert(String value) {
            ConversionContext.getContext().addSupportedFormats(getClass(), "<String -> "+factoryMethod.toGenericString());

            if (!Modifier.isStatic(factoryMethod.getModifiers())) {
                throw new IllegalArgumentException(factoryMethod.toGenericString() +
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
                throw new IllegalArgumentException("Failed to decode '" + value + "'", e);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Converters\n");
        b.append("----------\n");
        b.append("  CLASS                         TYPE                          INFO\n\n");
        for(Map.Entry<Type, List<Converter>> converterEntry: getConverters().entrySet()){
            for(Converter converter: converterEntry.getValue()){
                b.append("  ");
                FormatUtils.appendFormatted(b, converter.getClass().getSimpleName(), 30);
                if(converterEntry.getKey() instanceof ParameterizedType){
                    ParameterizedType pt = (ParameterizedType)converterEntry.getKey();
                    FormatUtils.appendFormatted(b, pt.getRawType().getTypeName(), 30);
                }else{
                    FormatUtils.appendFormatted(b, converterEntry.getKey().getTypeName(), 30);
                }
                b.append(FormatUtils.removeNewLines(converter.toString()));
                b.append('\n');
            }
        }
        return b.toString();
    }
}
