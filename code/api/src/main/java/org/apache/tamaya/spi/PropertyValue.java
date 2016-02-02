/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * Class modelling the result of a request for a property value.
 */
public final class PropertyValue {
    /** The requested key. */
    private String key;
    /** The value found. */
    private String value;
    /** Additional metadata provided by thhe provider. */
    private Map<String,String> contextData;

    PropertyValue(PropertyValueBuilder builder){
        this.key = builder.key;
        this.value = builder.value;
        if(builder.contextData!=null) {
            this.contextData = new HashMap<>(builder.contextData);
        }
    }

    /**
     * The requested key.
     * @return the, key never null.
     */
    public String getKey() {
        return key;
    }

    /**
     * THe value.
     * @return the value, in case a value is null it is valid to return {#code null} as result for
     * {@link PropertySource#get(String)}.
     */
    public String getValue() {
        return value;
    }

    public Map<String, String> getContextData() {
        return contextData;
    }

    /**
     * Creates a new builder instance.
     * @param key the key, not null.
     * @param value the value.
     * @return a new builder instance.
     */
    public static PropertyValueBuilder builder(String key, String value){
        return new PropertyValueBuilder(key, value);
    }
}
