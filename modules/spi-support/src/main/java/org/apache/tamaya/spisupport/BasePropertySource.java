/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.PropertySource;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract {@link org.apache.tamaya.spi.PropertySource} that allows to set a default ordinal that will be used, if no
 * ordinal is provided with the config.
 */
public abstract class BasePropertySource implements PropertySource{
    /** default ordinal that will be used, if no ordinal is provided with the config. */
    private final int defaultOrdinal;

    /**
     * Constructor.
     * @param defaultOrdinal default ordinal that will be used, if no ordinal is provided with the config.
     */
    protected BasePropertySource(int defaultOrdinal){
        this.defaultOrdinal = defaultOrdinal;
    }

    /**
     * Constructor, using a default ordinal of 0.
     */
    protected BasePropertySource(){
        this(0);
    }

    @Override
    public int getOrdinal() {
        String configuredOrdinal = get(TAMAYA_ORDINAL);
        if(configuredOrdinal!=null){
            try{
                return Integer.parseInt(configuredOrdinal);
            } catch(Exception e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Configured Ordinal is not an int number: " + configuredOrdinal, e);
            }
        }
        return getDefaultOrdinal();
    }

    /**
     * Returns the  default ordinal used, when no ordinal is set, or the ordinal was not parseable to an int value.
     * @return the  default ordinal used, by default 0.
     */
    public int getDefaultOrdinal(){
        return defaultOrdinal;
    }

    @Override
    public String get(String key) {
        return getProperties().get(key);
    }

    @Override
    public boolean isScannable(){
        return true;
    }
}
