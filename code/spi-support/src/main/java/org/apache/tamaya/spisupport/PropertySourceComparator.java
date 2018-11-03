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

import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import javax.annotation.Priority;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Comparator for ordering of {@link PropertySource}s based on their ordinal method and class name.
 */
public class PropertySourceComparator implements Comparator<PropertySource>, Serializable {
    /** serial version UID. */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = Logger.getLogger(PropertySourceComparator.class.getName());

    private static final PropertySourceComparator INSTANCE = new PropertySourceComparator();

    private String alternativeOrdinalKey;

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
     * Order property source reversely, the most important comes first.
     *
     * @param source1 the first PropertySource
     * @param source2 the second PropertySource
     * @return the comparison result.
     */
    private int comparePropertySources(PropertySource source1, PropertySource source2) {
        if (getOrdinal(source1) < getOrdinal(source2)) {
            return -1;
        } else if (getOrdinal(source1) > getOrdinal(source2)) {
            return 1;
        } else {
            return source1.getClass().getName().compareTo(source2.getClass().getName());
        }
    }

    /**
     * Evaluates an ordinal createValue from a {@link PropertySource}, whereby the ordinal of type {@code int}
     * is evaluated as follows:
     * <ol>
     *     <li>It evaluates the {@code String} createValue for {@link PropertySource#TAMAYA_ORDINAL} and tries
     *     to convert it to an {@code int} createValue, using {@link Integer#parseInt(String)}.</li>
     *     <li>It tries to find and evaluate a method {@code int getOrdinal()}.</li>
     *     <li>It tries to find and evaluate a static field {@code int ORDINAL}.</li>
     *     <li>It tries to find and evaluate a class level {@link Priority} annotation.</li>
     *     <li>It uses the default priority ({@code 0}.</li>
     * </ol>
     * @param propertySource the property source, not {@code null}.
     * @return the ordinal createValue to compare the property source.
     */
    public static int getOrdinal(PropertySource propertySource) {
        return getOrdinal(propertySource, null);
    }

    public static int getOrdinal(PropertySource propertySource, String alternativeOrdinalKey) {
        if(alternativeOrdinalKey!=null) {
            PropertyValue ordinalValue = propertySource.get(alternativeOrdinalKey);
            if (ordinalValue != null) {
                try {
                    return Integer.parseInt(ordinalValue.getValue().trim());
                } catch (Exception e) {
                    LOG.finest("Failed to parse ordinal from " + alternativeOrdinalKey +
                            " in " + propertySource.getName() + ": " + ordinalValue.getValue());
                }
            }
        }
        return propertySource.getOrdinal();
    }

    /**
     * Overrides/adds the key to evaluate/override a property source ordinal.
     * @param ordinalKey sets the alternative ordinal key, if null default
     *                   behaviour will be active.
     * @return the instance for chaining.
     */
    public PropertySourceComparator setOrdinalKey(String ordinalKey) {
        this.alternativeOrdinalKey = ordinalKey;
        return this;
    }

    @Override
    public int compare(PropertySource source1, PropertySource source2) {
        return comparePropertySources(source1, source2);
    }

}
