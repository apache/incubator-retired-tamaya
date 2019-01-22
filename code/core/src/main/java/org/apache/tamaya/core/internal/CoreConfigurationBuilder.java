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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationBuilder;
import org.apache.tamaya.core.internal.converters.BigDecimalConverter;
import org.apache.tamaya.core.internal.converters.BigIntegerConverter;
import org.apache.tamaya.core.internal.converters.BooleanConverter;
import org.apache.tamaya.core.internal.converters.ByteConverter;
import org.apache.tamaya.core.internal.converters.CharConverter;
import org.apache.tamaya.core.internal.converters.ClassConverter;
import org.apache.tamaya.core.internal.converters.CurrencyConverter;
import org.apache.tamaya.core.internal.converters.DoubleConverter;
import org.apache.tamaya.core.internal.converters.FileConverter;
import org.apache.tamaya.core.internal.converters.FloatConverter;
import org.apache.tamaya.core.internal.converters.IntegerConverter;
import org.apache.tamaya.core.internal.converters.LongConverter;
import org.apache.tamaya.core.internal.converters.NumberConverter;
import org.apache.tamaya.core.internal.converters.PathConverter;
import org.apache.tamaya.core.internal.converters.ShortConverter;
import org.apache.tamaya.core.internal.converters.URIConverter;
import org.apache.tamaya.core.internal.converters.URLConverter;
import org.apache.tamaya.spisupport.DefaultConfigurationBuilder;
import org.apache.tamaya.spisupport.DefaultConfigurationContext;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;

/**
 * Default implementation of {@link ConfigurationBuilder}.
 */
public final class CoreConfigurationBuilder extends DefaultConfigurationBuilder {

    /**
     * Creates a new builder instance.
     */
    public CoreConfigurationBuilder() {
        super();
    }

    /**
     * Creates a new builder instance.
     * @param config the configuration to be used, not null.
     */
    public CoreConfigurationBuilder(Configuration config) {
        super(config);
    }

    /**
     * Creates a new builder instance initializing it with the given context.
     * @param context the context to be used, not null.
     */
    public CoreConfigurationBuilder(ConfigurationContext context) {
        super(context);
    }

    @SuppressWarnings("unchecked")
    protected void addCorePropertyConverters() {
        addPropertyConverters(TypeLiteral.<BigDecimal>of(BigDecimal.class), new BigDecimalConverter());
        addPropertyConverters(TypeLiteral.<BigInteger>of(BigInteger.class), new BigIntegerConverter());
        addPropertyConverters(TypeLiteral.<Boolean>of(Boolean.class), new BooleanConverter());
        addPropertyConverters(TypeLiteral.<Byte>of(Byte.class), new ByteConverter());
        addPropertyConverters(TypeLiteral.<Character>of(Character.class), new CharConverter());
        addPropertyConverters(TypeLiteral.<Class<?>>of(Class.class), new ClassConverter());
        addPropertyConverters(TypeLiteral.<Currency>of(Currency.class), new CurrencyConverter());
        addPropertyConverters(TypeLiteral.<Double>of(Double.class), new DoubleConverter());
        addPropertyConverters(TypeLiteral.<File>of(File.class), new FileConverter());
        addPropertyConverters(TypeLiteral.<Float>of(Float.class), new FloatConverter());
        addPropertyConverters(TypeLiteral.<Integer>of(Integer.class), new IntegerConverter());
        addPropertyConverters(TypeLiteral.<Long>of(Long.class), new LongConverter());
        addPropertyConverters(TypeLiteral.<Number>of(Number.class), new NumberConverter());
        addPropertyConverters(TypeLiteral.<Path>of(Path.class), new PathConverter());
        addPropertyConverters(TypeLiteral.<Short>of(Short.class), new ShortConverter());
        addPropertyConverters(TypeLiteral.<URI>of(URI.class), new URIConverter());
        addPropertyConverters(TypeLiteral.<URL>of(URL.class), new URLConverter());
    }

    @Override
    public Configuration build() {
        Configuration cfg = new CoreConfiguration(
                new DefaultConfigurationContext(
                        serviceContext,
                        this.propertyFilters,
                        this.propertySources,
                        this.propertyConverters,
                        this.metaDataProvider
                ));
        built = true;
        return cfg;
    }

}
