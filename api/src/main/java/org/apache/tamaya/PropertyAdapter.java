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


import org.apache.tamaya.annotation.WithPropertyAdapter;
import org.apache.tamaya.spi.PropertyAdapterSpi;
import org.apache.tamaya.spi.ServiceContext;

/**
 * Interface for an property that converts a configured String into something else.
 * This is used for implementing type conversion from a property (String) to a certain target
 * type. Hereby the target type can be multivalued (eg eollections), complex or even contain
 * full subconfigurations, if needed.
 */
@FunctionalInterface
public interface PropertyAdapter<T>{

    /**
     * Adapt the given configuration keys to the required target type.
     * @param value the configuration keys
     * @return adapted keys
     */
    T adapt(String value);


    /**
     * Registers a new PropertyAdapter for the given target type, hereby replacing any existing adapter for
     * this type.
     * @param targetType The target class, not null.
     * @param adapter The adapter, not null.
     * @param <T> The target type
     * @return any adapter replaced with the new adapter, or null.
     */
    public static <T> PropertyAdapter<T> register(Class<T> targetType, PropertyAdapter<T> adapter){
        return ServiceContext.getInstance().getSingleton(PropertyAdapterSpi.class).register(targetType, adapter);
    }

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @return true, if the given target type is supported.
     */
    public static boolean isTargetTypeSupported(Class<?> targetType){
        return ServiceContext.getInstance().getSingleton(PropertyAdapterSpi.class).isTargetTypeSupported(targetType);
    }

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @param <T> the target type
     * @return the corresponding adapter, never null.
     * @throws org.apache.tamaya.ConfigException if the target type is not supported.
     */
    public static  <T> PropertyAdapter<T> getInstance(Class<T> targetType){
        return ServiceContext.getInstance().getSingleton(PropertyAdapterSpi.class).getPropertyAdapter(targetType, null);
    }

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @param annotation the {@link org.apache.tamaya.annotation.WithPropertyAdapter} annotation, or null. If the annotation is not null and
     *                   defines an overriding adapter, this instance is created and returned.
     * @param <T> the target type
     * @return the corresponding adapter, never null.
     * @throws org.apache.tamaya.ConfigException if the target type is not supported, or the overriding adapter cannot be
     * instantiated.
     */
    public static  <T> PropertyAdapter<T> getInstance(Class<T> targetType, WithPropertyAdapter annotation){
        return ServiceContext.getInstance().getSingleton(PropertyAdapterSpi.class).getPropertyAdapter(targetType, annotation);
    }

}
