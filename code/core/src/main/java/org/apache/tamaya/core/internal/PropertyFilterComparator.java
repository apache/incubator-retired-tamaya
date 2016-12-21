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

import org.apache.tamaya.spi.PropertyFilter;

import javax.annotation.Priority;
import java.io.Serializable;
import java.util.Comparator;

/**
 * Comparator for PropertyFilters based on their priority annotations.
 */
public class PropertyFilterComparator implements Comparator<PropertyFilter>, Serializable {

    private static final long serialVersionUID = 1L;

    private static final PropertyFilterComparator INSTANCE = new PropertyFilterComparator();

    /** Singleton constructor. */
    private PropertyFilterComparator(){}

    /**
     * Get the shared instance of the comparator.
     * @return the shared instance, never null.
     */
    public static PropertyFilterComparator getInstance(){
        return INSTANCE;
    }

    /**
     * Compare 2 filters for ordering the filter chain.
     *
     * @param filter1 the first filter
     * @param filter2 the second filter
     * @return the comparison result
     */
    private int comparePropertyFilters(PropertyFilter filter1, PropertyFilter filter2) {
        Priority prio1 = filter1.getClass().getAnnotation(Priority.class);
        Priority prio2 = filter2.getClass().getAnnotation(Priority.class);
        int ord1 = prio1 != null ? prio1.value() : 0;
        int ord2 = prio2 != null ? prio2.value() : 0;

        if (ord1 < ord2) {
            return -1;
        } else if (ord1 > ord2) {
            return 1;
        } else {
            return filter1.getClass().getName().compareTo(filter2.getClass().getName());
        }
    }

    @Override
    public int compare(PropertyFilter filter1, PropertyFilter filter2) {
        return comparePropertyFilters(filter1, filter2);
    }
}
