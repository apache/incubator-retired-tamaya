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

import java.lang.reflect.AnnotatedElement;
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

    private final Configuration configuration;
    private final String key;
    private final TypeLiteral<?> targetType;
    private final AnnotatedElement annotatedElement;
    private final List<String> supportedFormats = new ArrayList<>();
    private final ConfigurationContext configurationContext;

    /**
     * Private constructor used from builder.
     * @param builder the builder, not null.
     */
    protected ConversionContext(Builder builder){
        this.key = builder.key;
        this.annotatedElement = builder.annotatedElement;
        this.targetType = builder.targetType;
        this.supportedFormats.addAll(builder.supportedFormats);
        this.configuration = builder.configuration;
        this.configurationContext = builder.configurationContext;
    }

    /**
     * Get the key accessed. This information is very useful to evaluate additional metadata needed to determine/
     * control further aspects of the conversion.
     * @return the key. This may be null in case where a default value has to be converted and no unique underlying
     * key/value configuration is present.
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
     * Get the annotated element, if conversion is performed using injection mechanisms.
     * @return the annotated element, or null.
     */
    public AnnotatedElement getAnnotatedElement(){
        return annotatedElement;
    }

    /**
     * Get the configuration, which is targeted.
     * @return the configuration instance, or null.
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
                ", annotatedElement=" + annotatedElement +
                ", supportedFormats=" + supportedFormats +
                '}';
    }

    public ConfigurationContext getConfigurationContext() {
        return configurationContext;
    }

    /**
     * Builder to create new instances of {@link ConversionContext}.
     */
    public static final class Builder{
        /** The backing configuration. */
        private Configuration configuration;
        /** The configuration context. */
        private ConfigurationContext configurationContext;
        /** The accessed key, or null. */
        private String key;
        /** The target type. */
        private final TypeLiteral<?> targetType;
        /** The injection target (only set with injection used). */
        private AnnotatedElement annotatedElement;
        /** The ordered list of formats tried. */
        private final Set<String> supportedFormats = new HashSet<>();

        /**
         * Creates a new Builder instance.
         * @param targetType the target type
         */
        public Builder(TypeLiteral<?> targetType) {
            this(null, null, null, targetType);
        }

        /**
         * Creates a new Builder instance.
         * @param key the requested key, may be null.
         * @param targetType the target type
         */
        public Builder(String key, TypeLiteral<?> targetType) {
            this(null, null, key, targetType);
        }

        /**
         * Creates a new Builder instance.
         * @param configuration the configuration, not null.
         * @param configurationContext configuration context, not null.
         * @param key the requested key, may be null.
         * @param targetType the target type
         */
        public Builder(Configuration configuration, ConfigurationContext configurationContext, String key, TypeLiteral<?> targetType){
            this.key = key;
            this.configuration = configuration;
            this.configurationContext = configurationContext;
            this.targetType = Objects.requireNonNull(targetType);
        }

        /**
         * Sets the key.
         * @param key the key, not null.
         * @return the builder instance, for chaining
         */
        public Builder setKey(String key){
            this.key = Objects.requireNonNull(key);
            return this;
        }

        /**
         * Sets the configuration.
         * @param configuration the configuration, not null
         * @return the builder instance, for chaining
         */
        public Builder setConfiguration(Configuration configuration){
            this.configuration = Objects.requireNonNull(configuration);
            return this;
        }

        /**
         * Sets the configuration.
         * @param configurationContext the configuration, not null
         * @return the builder instance, for chaining
         */
        public Builder setConfigurationContext(ConfigurationContext configurationContext){
            this.configurationContext = Objects.requireNonNull(configurationContext);
            return this;
        }

        /**
         * Sets the annotated element, when configuration is injected.
         * @param annotatedElement the annotated element, not null
         * @return the builder instance, for chaining
         */
        public Builder setAnnotatedElement(AnnotatedElement annotatedElement){
            this.annotatedElement = Objects.requireNonNull(annotatedElement);
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
                    "context=" + configurationContext +
                    ", key='" + key + '\'' +
                    ", targetType=" + targetType +
                    ", annotatedElement=" + annotatedElement +
                    ", supportedFormats=" + supportedFormats +
                    '}';
        }
    }
}
