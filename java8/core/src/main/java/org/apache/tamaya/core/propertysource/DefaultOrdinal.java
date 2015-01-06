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
package org.apache.tamaya.core.propertysource;


/**
 * This interface defines the default ordinals for the 'standard'
 * {@link org.apache.tamaya.spi.PropertySource}s
 *
 * DefaultOrdinals can be overwritten via {@link org.apache.tamaya.spi.PropertySource#TAMAYA_ORDINAL}
 */
public final class DefaultOrdinal {

    /** Private constructor. */
    private DefaultOrdinal(){}

    /**
     * default ordinal for {@link org.apache.tamaya.core.propertysource.BasePropertySource} if
     * not overriden in each class
     */
    public static final int PROPERTY_SOURCE = 1000;

    /**
     * default ordinal for {@link org.apache.tamaya.core.propertysource.SystemPropertySource}
     */
    public static final int SYSTEM_PROPERTIES = 400;

    /**
     * default ordinal for {@link org.apache.tamaya.core.propertysource.EnvironmentPropertySource}
     */
    public static final int ENVIRONMENT_PROPERTIES = 300;

    /**
     * default ordinal for {@link org.apache.tamaya.core.propertysource.PropertiesFilePropertySource}
     */
    public static final int FILE_PROPERTIES = 100;

}
