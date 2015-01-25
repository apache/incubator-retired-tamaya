/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.tamaya;

import java.io.Serializable;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * <p>Class for instantiation of objects that represent parameterized types
 * with current parameters.</p>
 * <p>
 * <p>An object that represents a parameterized type may be obtained by
 * subclassing <tt>TypeLiteral</tt>.</p>
 * <p>
 * <pre>
 * TypeLiteral&lt;List&lt;Integer&gt;&gt; stringListType = new TypeLiteral&lt;List&lt;Integer&gt;&gt;() {};
 * </pre>
 *
 * @param <T> the type, including all type parameters
 */
public class TypeLiteral<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Type type;

    protected TypeLiteral(Type type) {
        this.type = type;
    }

    protected TypeLiteral() { }

    public static <L> TypeLiteral<L> of(Type type){
        return new TypeLiteral<>(type);
    }

    public static <L> TypeLiteral<L> of(Class<L> type){
        return new TypeLiteral<>(type);
    }

    private static Class<?> getTypeLiteralSubclass(Class<?> clazz) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass.equals(TypeLiteral.class)) {
            return clazz;
        } else if (superclass.equals(Object.class)) {
            return null;
        } else {
            return (getTypeLiteralSubclass(superclass));
        }
    }

    private static Type getTypeParameter(Class<?> superclass) {
        Type type = superclass.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            if (parameterizedType.getActualTypeArguments().length == 1) {
                return parameterizedType.getActualTypeArguments()[0];
            }
        }
        return null;
    }

    /**
     * @return the actual type represented by this object
     */
    public final Type getType() {
        if (type == null) {
            Class<?> typeLiteralSubclass = getTypeLiteralSubclass(this.getClass());
            if (typeLiteralSubclass == null) {
                throw new RuntimeException(getClass() + " is not a subclass of TypeLiteral");
            }
            type = getTypeParameter(typeLiteralSubclass);
            if (type == null) {
                throw new RuntimeException(getClass() + " does not specify the type parameter T of TypeLiteral<T>");
            }
        }
        return type;
    }

    /**
     * @return the raw type represented by this object
     */
    @SuppressWarnings("unchecked")
    public final Class<T> getRawType() {
        Type type = getType();
        if (type instanceof Class) {
            return (Class<T>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            return (Class<T>) Object[].class;
        } else {
            throw new RuntimeException("Illegal type");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof TypeLiteral)){
            return false;
        }
        TypeLiteral that = (TypeLiteral) o;
        if (type != null ? !type.equals(that.type) : that.type != null){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "TypeLiteral{" +
                "type=" + type +
                '}';
    }

}
