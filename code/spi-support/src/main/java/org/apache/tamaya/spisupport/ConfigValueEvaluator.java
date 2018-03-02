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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyValue;

import java.util.Map;


/**
 * Component SPI which encapsulates the evaluation of a single or full <b>raw</b> value
 * for a {@link ConfigurationContext}.
 */
public interface ConfigValueEvaluator {

    /**
     * Evaluates single value using a {@link ConfigurationContext}.
     * @param key the config key, not null.
     * @param context the context, not null.
     * @return the value, or null.
     */
    PropertyValue evaluteRawValue(String key, ConfigurationContext context);

    /**
     * Evaluates all property values from a {@link ConfigurationContext}.
     * @param context the context, not null.
     * @return the value, or null.
     */
    Map<String, PropertyValue> evaluateRawValues(ConfigurationContext context);

}
