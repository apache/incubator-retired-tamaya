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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertyValue;

import java.util.Arrays;
import java.util.List;

/**
 * Predicate filtering using a regex expression operating on the key. It allows either
 * to define the target keys to be selected (includes), or to be excluded (excludes).
 */
public final class RegexPropertyFilter implements PropertyFilter {
    /** The expression used to include entries that match. */
    private List<String> includes;
    /** The expression used to exclude entries that match. */
    private List<String> excludes;

    /**
     * Sets the regex expression to be applied on the key to filter the corresponding entry
     * if matching.
     * @param expressions the regular expression for inclusion, not null.
     */
    public void setIncludes(String... expressions){
        this.includes = Arrays.asList(expressions);
    }

    /**
     * Sets the regex expression to be applied on the key to remove the corresponding entries
     * if matching.
     * @param expressions the regular expressions for exclusion, not null.
     */
    public void setExcludes(String... expressions){
        this.excludes= Arrays.asList(expressions);
    }

    @Override
    public PropertyValue filterProperty(PropertyValue valueToBeFiltered) {
        if(includes!=null){
            for(String expression:includes){
                if(valueToBeFiltered.getQualifiedKey().matches(expression)){
                    return valueToBeFiltered;
                }
            }
            return null;
        }
        if(excludes!=null){
            for(String expression:excludes){
                if(valueToBeFiltered.getQualifiedKey().matches(expression)){
                    return null;
                }
            }
        }
        return valueToBeFiltered;
    }

    @Override
    public String toString() {
        return "RegexPropertyFilter{" +
                "includes='" + includes + '\'' +
                "excludes='" + excludes + '\'' +
                '}';
    }

}
