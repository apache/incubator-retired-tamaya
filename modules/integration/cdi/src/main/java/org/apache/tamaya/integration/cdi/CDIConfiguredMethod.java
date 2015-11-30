/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tamaya.integration.cdi;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.inject.spi.ConfiguredMethod;

import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by atsticks on 29.11.15.
 */
public class CDIConfiguredMethod implements ConfiguredMethod{

    private Method method;
    private List<String> keys = new ArrayList<>();

    CDIConfiguredMethod(InjectionPoint injectionPoint, List<String> keys){
        this.method = (Method)injectionPoint.getMember();
        this.keys.addAll(keys);
        this.keys = Collections.unmodifiableList(this.keys);
    }

    @Override
    public Collection<String> getConfiguredKeys() {
        return keys;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    @Override
    public Method getAnnotatedMethod() {
        return method;
    }

    @Override
    public String getName() {
        return method.getName();
    }

    @Override
    public String getSignature() {
        return null;
    }

    @Override
    public void configure(Object instance, Configuration config) {
        throw new UnsupportedOperationException("Use CDI annotations for configuration injection.");
    }

    @Override
    public String toString() {
        return "CDIConfiguredMethod["+getSignature()+']';
    }
}
