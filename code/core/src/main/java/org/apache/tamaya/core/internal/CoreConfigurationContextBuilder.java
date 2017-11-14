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

import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.core.internal.converters.*;
import org.apache.tamaya.spisupport.DefaultConfigurationContextBuilder;
import org.apache.tamaya.spisupport.PropertySourceComparator;
import org.apache.tamaya.spisupport.propertysource.CLIPropertySource;
import org.apache.tamaya.spisupport.propertysource.EnvironmentPropertySource;
import org.apache.tamaya.spisupport.propertysource.JavaConfigurationPropertySource;
import org.apache.tamaya.spisupport.propertysource.SystemPropertySource;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

/**
 * Default implementation of {@link ConfigurationContextBuilder}.
 */
public final class CoreConfigurationContextBuilder extends DefaultConfigurationContextBuilder {

    /**
     * Creates a new builder instance.
     */
    public CoreConfigurationContextBuilder() {
    }

    /**
     * Creates a new builder instance initializing it with the given context.
     * @param context the context to be used, not null.
     */
    public CoreConfigurationContextBuilder(ConfigurationContext context) {
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
    public ConfigurationContext build() {
        return new CoreConfigurationContext(this);
    }
}
