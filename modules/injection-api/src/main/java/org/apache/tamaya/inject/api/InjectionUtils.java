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
package org.apache.tamaya.inject.api;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * Utility class with several commonly used functions.
 */
public final class InjectionUtils {

    private InjectionUtils(){}


    /**
     * Collects all keys to be be accessed as defined by any annotations of type
     * {@link ConfigDefaultSections}, {@link Config}.
     * @param field the (optionally) annotated field instance
     * @return the regarding key list to be accessed fomr the {@link org.apache.tamaya.Configuration}.
     */
    public static List<String> getKeys(Field field) {
        ConfigDefaultSections areasAnnot = field.getDeclaringClass().getAnnotation(ConfigDefaultSections.class);
        return InjectionUtils.evaluateKeys(field, areasAnnot, field.getAnnotation(Config.class));
    }

    /**
     * Collects all keys to be be accessed as defined by any annotations of type
     * {@link ConfigDefaultSections}, {@link Config}.
     * @param method the (optionally) annotated method instance
     * @return the regarding key list to be accessed fomr the {@link org.apache.tamaya.Configuration}.
     */
    public static List<String> getKeys(Method method) {
        ConfigDefaultSections areasAnnot = method.getDeclaringClass().getAnnotation(ConfigDefaultSections.class);
        return InjectionUtils.evaluateKeys(method, areasAnnot, method.getAnnotation(Config.class));
    }

    /**
     * Evaluates all absolute configuration key based on the member name found.
     *
     * @param areasAnnot the (optional) annotation definining areas to be looked up.
     * @return the list current keys in order how they should be processed/looked up.
     */
    public static List<String> evaluateKeys(Member member, ConfigDefaultSections areasAnnot) {
        List<String> keys = new ArrayList<>();
        String name = member.getName();
        String mainKey;
        if (name.startsWith("get") || name.startsWith("set")) {
            mainKey = Character.toLowerCase(name.charAt(3)) + name.substring(4);
        } else {
            mainKey = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        keys.add(mainKey);
        if (areasAnnot != null) {
            // Add prefixed entries, including absolute (root) entry for "" area keys.
            for (String area : areasAnnot.value()) {
                if (!area.isEmpty()) {
                    keys.add(area + '.' + mainKey);
                }
            }
        } else { // add package name
            keys.add(member.getDeclaringClass().getName() + '.' + mainKey);
        }
        return keys;
    }

    /**
     * Evaluates all absolute configuration key based on the annotations found on a class.
     *
     * @param areasAnnot         the (optional) annotation definining areas to be looked up.
     * @param propertyAnnotation the annotation on field/method level that may defined one or
     *                           several keys to be looked up (in absolute or relative form).
     * @return the list current keys in order how they should be processed/looked up.
     */
    public static List<String> evaluateKeys(Member member, ConfigDefaultSections areasAnnot, Config propertyAnnotation) {
        if(propertyAnnotation==null){
            return evaluateKeys(member, areasAnnot);
        }
        List<String> keys = new ArrayList<>(Arrays.asList(propertyAnnotation.value()));
        if (keys.isEmpty()) {
            keys.add(member.getName());
        }
        ListIterator<String> iterator = keys.listIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (next.startsWith("[") && next.endsWith("]")) {
                // absolute key, strip away brackets, take key as is
                iterator.set(next.substring(1, next.length() - 1));
            } else {
                if (areasAnnot != null && areasAnnot.value().length>0) {
                    // Remove original entry, since it will be replaced with prefixed entries
                    iterator.remove();
                    // Add prefixed entries, including absolute (root) entry for "" area keys.
                    for (String area : areasAnnot.value()) {
                        iterator.add(area.isEmpty() ? next : area + '.' + next);
                    }
                }
            }
        }
        return keys;
    }

}
