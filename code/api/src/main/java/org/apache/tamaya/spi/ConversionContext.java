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
import java.util.*;

/**
 * A conversion context containing all the required values for implementing conversion. Use the included #Builder
 * for creating new instances of. This class is thread-safe to use. Adding supported formats is synchronized.
 * @see PropertyConverter
 */
public class ConversionContext {

    public static final ConversionContext EMPTY = new ConversionContext.Builder(TypeLiteral.of(String.class)).build();
    private final Configuration configuration;
    private final String key;
    private final List<PropertyValue> values;
    private final TypeLiteral<?> targetType;
    private final AnnotatedElement annotatedElement;
    private final Set<String> supportedFormats = new LinkedHashSet<>();

    /**
     * Private constructor used from builder.
     * @param builder the builder, not {@code null}.
     */
    protected ConversionContext(Builder builder){
        this.key = builder.key;
        this.annotatedElement = builder.annotatedElement;
        this.targetType = builder.targetType;
        this.supportedFormats.addAll(builder.supportedFormats);
        this.configuration = builder.configuration;
        List<PropertyValue> tempValues = new ArrayList<>();
        tempValues.addAll(builder.values);
        this.values = Collections.unmodifiableList(tempValues);
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
     * Get the correspnoding underlying property values matching the given key, in order of significance
     * (most significant last).
     * @return the underlying values evaluated, never null.
     */
    public List<PropertyValue> getValues(){
        return this.values;
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
     * @return the annotated element, or {@code null}.
     */
    public AnnotatedElement getAnnotatedElement(){
        return annotatedElement;
    }

    /**
     * Get the configuration, which is targeted.
     * @return the configuration instance, or {@code null}.
     */
    public Configuration getConfiguration(){
        return configuration;
    }


    /**
     * Evaluate the current metadata from the given values. Later values hereby are more significant.
     * @return the evaluated meta data map.
     */
    public Map<String, String> getMeta() {
        Map<String, String> metaMap = new HashMap<>();
        if(values.size()>0){
            String baseKey = values.get(0).getQualifiedKey()+".";

            values.forEach(val -> this.getConfiguration().getContext().getMetaData().entrySet().forEach(
                    en -> {
                        if(en.getKey().startsWith(baseKey)) {
                            metaMap.put(en.getKey().substring(baseKey.length()), en.getValue());
                        }
                    }));
        }
        return metaMap;
    }

    /**
     * Allows to add information on the supported/tried formats, which can be shown to the user, especially when
     * conversion failed. Adding of formats is synchronized, all formats are added in order to the overall createList.
     * This means formats should be passed in order of precedence.
     * @param converterType the converters, which implements the formats provided.
     * @param formatDescriptors the format descriptions in a human readable form, e.g. as regular expressions.
     */
    public void addSupportedFormats(@SuppressWarnings("rawtypes") Class<?> converterType, String... formatDescriptors){
        synchronized (supportedFormats){
            for(String format: formatDescriptors) {
                supportedFormats.add(format + " (" + converterType.getSimpleName() + ")");
            }
        }
    }

    /**
     * Get the supported/tried formats in precedence order. The contents of this method depends on the
     * {@link PropertyConverter} instances involved in a conversion.
     * @return the supported/tried formats, never {@code null}.
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

    /**
     * Get the current configuration context.
     * @deprecated Use {@link #getConfiguration()} and {@link Configuration#getContext()}.
     * @return the current configuration context.
     */
    @Deprecated
    public ConfigurationContext getConfigurationContext() {
        return getConfiguration().getContext();
    }

    /**
     * Builder to create new instances of {@link ConversionContext}.
     */
    public static final class Builder{
        /** The backing configuration. */
        private Configuration configuration;
        /** The accessed key, or null. */
        private String key;
        /** The corresponding property values, as delivered from the corresponding property sources,
         * in order of significance (highest last).
         */
        private final List<PropertyValue> values = new ArrayList<>();
        /** The target type. */
        private TypeLiteral<?> targetType;
        /** The injection target (only setCurrent with injection used). */
        private AnnotatedElement annotatedElement;
        /** The ordered setCurrent of formats tried. */
        private final Set<String> supportedFormats = new LinkedHashSet<>();

        /**
         * Creates a new Builder instance.
         * @param targetType the target type
         */
        public Builder(TypeLiteral<?> targetType) {
            this(null,  null, targetType);
        }

        /**
         * Creates a new Builder instance.
         * @param key the requested key, may be null.
         * @param targetType the target type
         */
        public Builder(String key, TypeLiteral<?> targetType) {
            this(null,  key, targetType);
        }

        /**
         * Creates a new Builder instance.
         * @param configuration the configuration, not {@code null}.
         * @param key the requested key, may be {@code null}.
         * @param targetType the target type
         */
        public Builder(Configuration configuration, String key, TypeLiteral<?> targetType){
            this.key = key;
            this.configuration = configuration;
            this.targetType = Objects.requireNonNull(targetType);
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
         * Sets the underlying values evaluated.
         * @param values the values, not {@code null}.
         * @return the builder instance, for chaining
         */
        public Builder setValues(List<PropertyValue> values){
            this.values.addAll(values);
            return this;
        }

        /**
         * Sets the underlying values evaluated.
         * @param values the values, not {@code null}.
         * @return the builder instance, for chaining
         */
        public Builder setValues(PropertyValue... values){
            this.values.addAll(Arrays.asList(values));
            return this;
        }

        /**
         * Sets the configuration.
         * @param configuration the configuration, not {@code null}
         * @return the builder instance, for chaining
         */
        public Builder setConfiguration(Configuration configuration){
            this.configuration = Objects.requireNonNull(configuration);
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
        public Builder setTargetType(@SuppressWarnings("rawtypes") TypeLiteral targetType) {
            this.targetType = Objects.requireNonNull(targetType);
            return this;
        }

        /**
         * Add the formats provided by a {@link PropertyConverter}. This method should be called by each converters
         * performing/trying conversion, so the user can be given feedback on the supported formats on failure.
         * @param converterType the converters type, not {@code null}.
         * @param formatDescriptors the formats supported in a human readable form, e.g. as regular expressions.
         * @return the builder instance, for chaining
         */
        public Builder addSupportedFormats(@SuppressWarnings("rawtypes") Class<?> converterType, String... formatDescriptors){
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
                    ", annotatedElement=" + annotatedElement +
                    ", supportedFormats=" + supportedFormats +
                    '}';
        }

    }
}
