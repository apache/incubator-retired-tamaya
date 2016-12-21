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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.spi.PropertySource;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for ordering of PropertySources based on their ordinal method and class name.
 */
public class PropertySourceComparator implements Comparator<PropertySource>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final PropertySourceComparator INSTANCE = new PropertySourceComparator();

    /** Singleton constructor. */
    private PropertySourceComparator(){}

    /**
     * Get the shared instance of the comparator.
     * @return the shared instance, never null.
     */
    public static PropertySourceComparator getInstance(){
        return INSTANCE;
    }

    /**
     * Order property source reversely, the most important come first.
     *
     * @param source1 the first PropertySource
     * @param source2 the second PropertySource
     * @return the comparison result.
     */
    private int comparePropertySources(PropertySource source1, PropertySource source2) {
        if (source1.getOrdinal() < source2.getOrdinal()) {
            return -1;
        } else if (source1.getOrdinal() > source2.getOrdinal()) {
            return 1;
        } else {
            return source1.getClass().getName().compareTo(source2.getClass().getName());
        }
    }

    @Override
    public int compare(PropertySource source1, PropertySource source2) {
        return comparePropertySources(source1, source2);
    }
}
