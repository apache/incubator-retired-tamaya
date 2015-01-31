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
package org.apache.tamaya.core.internal.converters;

import org.apache.tamaya.PropertyConverter;

import javax.annotation.CheckForNull;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Boolean.
 */
public class BooleanConverter implements PropertyConverter<Boolean> {

    private Logger LOG = Logger.getLogger(getClass().getName());

    @Override
    @CheckForNull
    public Boolean convert(String value) {
        String ignoreCaseValue = Objects.requireNonNull(value)
                                        .trim()
                                        .toLowerCase(Locale.ENGLISH);

        switch(ignoreCaseValue) {
            case "yes":
            case "y":
            case "true":
            case "t":
                return Boolean.TRUE;
            case "no":
            case "n":
            case "false":
            case "f":
                return Boolean.FALSE;
            default:
                LOG.warning("Unknown boolean value encountered: " + value);
                return null;
        }
    }
}
