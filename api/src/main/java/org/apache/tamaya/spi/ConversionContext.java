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
package org.apache.tamaya.spi;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A conversion context containing all the required values for implementing conversion. Use the included #Builder
 * for creating new instances of. This class is thread-safe to use. Adding supported formats is synchronized.
 * @see PropertyConverter
 */
public class ConversionContext {

    private Configuration configuration;
    private String key;
    private TypeLiteral<?> targetType;
    private Member injectionTarget;
    private List<String> supportedFormats = new ArrayList<>();

    /**
     * Private constructor used from builder.
     * @param builder the builder, not null.
     */
    protected ConversionContext(Builder builder){
        this.key = builder.key;
        this.injectionTarget = builder.injectionTarget;
        this.targetType = builder.targetType;
        this.supportedFormats.addAll(builder.supportedFormats);
        this.configuration = builder.configuration;
    }

    /**
     * Get the key accessed. This information is very useful to evaluate additional metadata needed to determine/
     * control further aspects of the conversion.
     * @return the key. This may be null in case where a default value has to be converted and no unique underlying
     * key/value configuration is present..
     */
    public String getKey(){
        return key;
    }

    /**
     * Get the target type required.
     * @return the target type required.
     */
    public TypeLiteral<?> getTargetType(){
        return targetType;
    }

    /**
     * Get the injection target, if conversion is performed using injection mechanisms.
     * @return the injection target, either a {@link Field} or {@link Method}, or null.
     */
    public Member getInjectionTarget(){
        return injectionTarget;
    }

    /**
     * Get the configuration, which is targeted.
     * @return the configuration instance.
     */
    public Configuration getConfiguration(){
        return configuration;
    }

    /**
     * Allows to add information on the supported/tried formats, which can be shown to the user, especially when
     * conversion failed. Adding of formats is synchronized, all formats are added in order to the overall list.
     * This means formats should be passed in order of precedence.
     * @param converterType the converter, which implements the formats provided.
     * @param formatDescriptors the format descriptions in a human readable form, e.g. as regular expressions.
     */
    public void addSupportedFormats(Class<? extends PropertyConverter> converterType, String... formatDescriptors){
        synchronized (supportedFormats){
            for(String format: formatDescriptors) {
                supportedFormats.add(format + " (" + converterType.getSimpleName() + ")");
            }
        }
    }

    /**
     * Get the supported/tried formats in precedence order. The contents of this method depends on the
     * {@link PropertyConverter} instances involved in a conversion.
     * @return the supported/tried formats, never null.
     */
    public List<String> getSupportedFormats(){
        synchronized (supportedFormats){
            return new ArrayList<>(supportedFormats);
        }
    }

    @Override
    public String toString() {
        return "ConversionContext{" +
                "configuration=" + configuration +
                ", key='" + key + '\'' +
                ", targetType=" + targetType +
                ", injectionTarget=" + injectionTarget +
                ", supportedFormats=" + supportedFormats +
                '}';
    }

    /**
     * Builder to create new instances of {@link ConversionContext}.
     */
    public static final class Builder{
        /** The backing configuration. */
        private Configuration configuration;
        /** The accessed key, or null. */
        private String key;
        /** The target type. */
        private TypeLiteral<?> targetType;
        /** The injection target (only set with injection used). */
        private Member injectionTarget;
        /** The ordered list of formats tried. */
        private Set<String> supportedFormats = new HashSet<>();

        /**
         * Creates a new Builder instance.
         * @param configuration the configuration, not null.
         * @param key the requested key, may be null.
         */
        public Builder(Configuration configuration, String key){
            this.key = key;
            this.configuration = Objects.requireNonNull(configuration);
        }

        /**
         * Sets the injected field, when configuration is injected.
         * @param field the field, not null
         * @return the builder instance, for chaining
         */
        public Builder setInjectedField(Field field){
            this.injectionTarget = Objects.requireNonNull(field);
            this.targetType = TypeLiteral.of(field.getType());
            return this;
        }

        /**
         * Sets the injected setter method, when configuration is injected.
         * @param method the method, not null
         * @return the builder instance, for chaining
         */
        public Builder setInjectedMethod(Method method){
            this.injectionTarget = Objects.requireNonNull(method);
            this.targetType = TypeLiteral.of(method.getParameterTypes()[0]);
            return this;
        }

        /**
         * Sets the required target type resulting from the conversion.
         * @param typeLiteral the target type, not null.
         * @return the builder instance, for chaining
         */
        public Builder setTargetType(TypeLiteral typeLiteral){
            this.targetType = Objects.requireNonNull(typeLiteral);
            return this;
        }

        /**
         * Add the formats provided by a {@link PropertyConverter}. This method should be called by each converter
         * performing/trying conversion, so the user can be given feedback on the supported formats on failure.
         * @param converterType the converter type, not null.
         * @param formatDescriptors the formats supported in a human readable form, e.g. as regular expressions.
         * @return the builder instance, for chaining
         */
        public Builder addSupportedFormats(Class<? extends PropertyConverter> converterType, String... formatDescriptors){
            for(String format: formatDescriptors) {
                supportedFormats.add(format + " (" + converterType.getSimpleName() + ")");
            }
            return this;
        }

        /**
         * Builds a new context instance.
         * @return a new context, never null.
         */
        public ConversionContext build(){
            return new ConversionContext(this);
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "configuration=" + configuration +
                    ", key='" + key + '\'' +
                    ", targetType=" + targetType +
                    ", injectionTarget=" + injectionTarget +
                    ", supportedFormats=" + supportedFormats +
                    '}';
        }
    }
}
