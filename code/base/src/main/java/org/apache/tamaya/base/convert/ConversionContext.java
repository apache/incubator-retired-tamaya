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
package org.apache.tamaya.base.convert;

import org.apache.tamaya.spi.TypeLiteral;

import javax.config.Config;
import javax.config.spi.Converter;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.*;

/**
 * A conversion context containing all the required values for implementing conversion. Use the included #Builder
 * for creating new instances of. This class is thread-safe to use. Adding supported formats is synchronized.
 * @see Converter
 */
public class ConversionContext {

    private final Config configuration;
    private final String key;
    private final Type targetType;
    private final AnnotatedElement annotatedElement;
    private final List<String> supportedFormats = new ArrayList<>();

    private static ThreadLocal<ConversionContext> INSTANCE = new ThreadLocal<>();

    public static ConversionContext getContext(){
        return INSTANCE.get();
    }

    public static void setContext(ConversionContext context){
        INSTANCE.set(Objects.requireNonNull(context));
    }

    public static void reset() {
        INSTANCE.remove();
    }

    /**
     * Private constructor used from builder.
     * @param builder the builder, not {@code null}.
     */
    protected ConversionContext(Builder builder){
        this.key = builder.key;
        this.annotatedElement = builder.annotatedElement;
        this.targetType = Objects.requireNonNull(builder.targetType, "Target type required.");
        this.supportedFormats.addAll(builder.supportedFormats);
        this.configuration = builder.configuration;
    }

    /**
     * Get the key accessed. This information is very useful to evaluate additional metadata needed to determine/
     * control further aspects of the conversion.
     * @return the key. This may be null in case where a default value has to be converted and no unique underlying
     * key/value configuration is present.
     */
    public final String getKey(){
        return key;
    }

    /**
     * Get the target type required.
     * @return the target type required.
     */
    public final Type getTargetType(){
        return targetType;
    }

    /**
     * Get the annotated element, if conversion is performed using injection mechanisms.
     * @return the annotated element, or {@code null}.
     */
    public final AnnotatedElement getAnnotatedElement(){
        return annotatedElement;
    }

    /**
     * Get the configuration, which is targeted.
     * @return the current configuration context, or {@code null}.
     */
    public final Config getConfiguration(){
        return configuration;
    }

    /**
     * Allows to add information on the supported/tried formats, which can be shown to the user, especially when
     * conversion failed. Adding of formats is synchronized, all formats are added in order to the overall list.
     * This means formats should be passed in order of precedence.
     * @param converterType the converters, which implements the formats provided.
     * @param formatDescriptors the format descriptions in a human readable form, e.g. as regular expressions.
     */
    public final void addSupportedFormats(@SuppressWarnings("rawtypes") Class<? extends Converter> converterType, String... formatDescriptors){
        synchronized (supportedFormats){
            for(String format: formatDescriptors) {
                supportedFormats.add(format + " (" + converterType.getSimpleName() + ")");
            }
        }
    }

    /**
     * Get the supported/tried formats in precedence order. The contents of this method depends on the
     * {@link Converter} instances involved in a conversion.
     * @return the supported/tried formats, never {@code null}.
     */
    public final List<String> getSupportedFormats(){
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
                ", annotatedElement=" + annotatedElement +
                ", supportedFormats=" + supportedFormats +
                '}';
    }


    /**
     * Builder to create new instances of {@link ConversionContext}.
     */
    public static final class Builder{
        /** The backing configuration. */
        private Config configuration;
        /** The accessed key, or null. */
        private String key;
        /** The target type. */
        private Type targetType;
        /** The injection target (only set with injection used). */
        private AnnotatedElement annotatedElement;
        /** The ordered list of formats tried. */
        private final Set<String> supportedFormats = new HashSet<>();

        /**
         * Creates a new Builder instance.
         * @param key the requested key, may be null.
         * @param targetType the target type
         */
        public Builder(String key, Type targetType) {
            this(null, key, targetType);
        }

        /**
         * Creates a new Builder instance.
         * @param configuration the configuration, not {@code null}.
         * @param key the requested key, may be {@code null}.
         * @param targetType the target type
         */
        public Builder(Config configuration, String key, Type targetType){
            this.key = key;
            this.configuration = configuration;
            this.targetType = Objects.requireNonNull(targetType, "Target type required.");
        }

        /**
         * Sets the key.
         * @param key the key, not {@code null}.
         * @return the builder instance, for chaining
         */
        public Builder setKey(String key){
            this.key = Objects.requireNonNull(key);
            return this;
        }

        /**
         * Sets the configuration.
         * @param configuration the configuration, not {@code null}
         * @return the builder instance, for chaining
         */
        public Builder setConfiguration(Config configuration){
            this.configuration = configuration;
            return this;
        }

        /**
         * Sets the annotated element, when configuration is injected.
         * @param annotatedElement the annotated element, not {@code null}
         * @return the builder instance, for chaining
         */
        public Builder setAnnotatedElement(AnnotatedElement annotatedElement){
            this.annotatedElement = Objects.requireNonNull(annotatedElement);
            return this;
        }

        /**
         * Sets the target type explicitly. This is required in some rare cases, e.g. injection of {@code Provider}
         * instances, where the provider's result type must be produced.
         * @param targetType the
         * @return the builder for chaining.
         */
        public Builder setTargetType(Type targetType) {
            this.targetType = Objects.requireNonNull(targetType);
            return this;
        }

        /**
         * Add the formats provided by a {@link Converter}. This method should be called by each converters
         * performing/trying conversion, so the user can be given feedback on the supported formats on failure.
         * @param converterType the converters type, not {@code null}.
         * @param formatDescriptors the formats supported in a human readable form, e.g. as regular expressions.
         * @return the builder instance, for chaining
         */
        public Builder addSupportedFormats(@SuppressWarnings("rawtypes") Class<? extends Converter> converterType, String... formatDescriptors){
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
            ConversionContext ctx = new ConversionContext(this);
            INSTANCE.set(ctx);
            return ctx;
        }

        @Override
        public String toString() {
            return "Builder{" +
                    "configuration=" + configuration +
                    ", key='" + key + '\'' +
                    ", targetType=" + targetType +
                    ", annotatedElement=" + annotatedElement +
                    ", supportedFormats=" + supportedFormats +
                    '}';
        }

    }
}
