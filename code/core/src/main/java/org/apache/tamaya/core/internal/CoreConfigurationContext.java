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
import org.apache.tamaya.core.internal.converters.*;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spisupport.DefaultConfigurationContext;
import org.apache.tamaya.spisupport.DefaultConfigurationContextBuilder;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Currency;

/**
 * Default implementation of {@link ConfigurationContextBuilder}.
 */
public final class CoreConfigurationContext extends DefaultConfigurationContext {

    /**
     * Creates a new builder instance.
     */
    public CoreConfigurationContext(CoreConfigurationContextBuilder builder) {
        super(builder);
    }

    @Override
    public ConfigurationContextBuilder toBuilder() {
        return new CoreConfigurationContextBuilder(this);
    }
}
