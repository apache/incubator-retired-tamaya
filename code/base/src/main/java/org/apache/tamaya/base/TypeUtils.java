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
package org.apache.tamaya.base;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Utility class for type handling.
 */
public final class TypeUtils {

    private static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    /**
     * Singleton constructor.
     */
    private TypeUtils() {
    }


    /**
     * Checks the current implemented generic interfaces and evaluates the given single type parameter.
     *
     * @param clazz         the class to check, not  {@code null}.
     * @param interfaceType the interface type to be checked, not {@code null}.
     * @return the generic type parameters, or an empty array, if it cannot be evaluated.
     */
    public static Type[] getInterfaceTypeParameters(Class<?> clazz, Class<?> interfaceType) {
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
        return EMPTY_TYPE_ARRAY;
    }

    /**
     * Method that checks the class's type for a generic interface implementation type.
     *
     * @param type         the type, not {@code null}.
     * @return the generic type parameter of the given single type generic interfaceType, or an empty array.
     */
    public static Type[] getTypeParameters(Type type) {
        Objects.requireNonNull(type, "Type must be given.");

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments();
        }
        return EMPTY_TYPE_ARRAY;
    }

    /**
     * Returns basic raw Java type.
     *
     * @return the actual type represented by this object
     */
    @SuppressWarnings("unchecked")
	public final Class getRawType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return (Class) pt.getRawType();
        } else if (type instanceof GenericArrayType) {
            return Object[].class;
        } else if (type instanceof Class) {
           return (Class) type;
        } else {
            throw new RuntimeException("Illegal type for the Type Literal Class");
        }
    }

    /**
     * Returns actual type arguments, if present.
     *
     * @return the actual type represented by defined class, or an empty array.
     */
    public static Type[] getActualTypeArguments(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            return pt.getActualTypeArguments();
        }
        return new Type[0];
    }


    /**
     * Get the actual parametrized types for a given parametrized super class type.
     * @param type the type, not null.
     * @param parametrizedClass the parametrized super class.
     * @return the types, or an empty array.
     * @throws IllegalArgumentException if no super class is an instance of the required parametrized type.
     */
    public static Type[] getActualTypeArguments(Class type, Class parametrizedClass) {
        if (type == null) {
            throw new RuntimeException("Class parameter type can not be null");
        }

        Type superClazz = type.getGenericSuperclass();

        if (superClazz instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) superClazz;
            return pt.getActualTypeArguments();
        } else if (superClazz.equals(Object.class)) {
            throw new IllegalArgumentException("Super class must be parametrized type");
        } else {
           return getActualTypeArguments((Class<?>) superClazz, parametrizedClass);
        }
    }


}
