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


import org.apache.tamaya.annotation.WithCodec;

/**
 * Interface for an codec that converts a configured String into something else and vice versa.
 * This is used for implementing type conversion from a property (String) to a certain target
 * type. Hereby the target type can be multivalued (eg eollections), complex or even contain
 * full subconfigurations, if needed. The operation converting from a type T to a String can be
 * used by mutable configuration/property sources, when applying a {@link org.apache.tamaya.ConfigChangeSet}
 * to render the correct String representation of a entry changed.
 */
public interface Codec<T>{

    /**
     * Adapt the given configuration keys to the required target type.
     * @param value the configuration keys
     * @return adapted keys
     */
    T deserialize(String value);

    /**
     * Adapt the given configuration keys to the required target type.
     * @param value the configuration keys
     * @return adapted keys
     */
    String serialize(T value);

    /**
     * Registers a new PropertyAdapter for the given target type, hereby replacing any existing adapter for
     * this type.
     * @param targetType The target class, not null.
     * @param adapter The adapter, not null.
     * @param <T> The target type
     * @return any adapter replaced with the new adapter, or null.
     */
    public static <T> Codec<T> register(Class<T> targetType, Codec<T> adapter){
        return Codecs.register(targetType, adapter);
    }

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @return true, if the given target type is supported.
     */
    public static boolean isTargetTypeSupported(Class<?> targetType){
        return Codecs.isTargetTypeSupported(targetType);
    }

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @param <T> the target type
     * @return the corresponding adapter, never null.
     * @throws ConfigException if the target type is not supported.
     */
    public static  <T> Codec<T> getInstance(Class<T> targetType){
        return Codecs.getCodec(targetType);
    }

    /**
     * Get an adapter converting to the given target type.
     * @param targetType the target type class
     * @param annotation the {@link org.apache.tamaya.annotation.WithCodec} annotation, or null. If the annotation is not null and
     *                   defines an overriding adapter, this instance is created and returned.
     * @param <T> the target type
     * @return the corresponding adapter, never null.
     * @throws ConfigException if the target type is not supported, or the overriding adapter cannot be
     * instantiated.
     */
    public static  <T> Codec<T> getInstance(Class<T> targetType, WithCodec annotation){
        return Codecs.getCodec(targetType, annotation);
    }

}
