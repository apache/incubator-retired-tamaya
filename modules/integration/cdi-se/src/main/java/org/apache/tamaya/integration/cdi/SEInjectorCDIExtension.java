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
package org.apache.tamaya.integration.cdi;


import org.apache.tamaya.inject.ConfigurationInjection;
import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.inject.api.ConfigDefaultSections;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * CDI portable extension that integrates {@link org.apache.tamaya.inject.ConfigurationInjector}
 * with CDI by adding configuration features to CDI (config enable CDI beans).
 */
@Vetoed
public final class SEInjectorCDIExtension implements Extension {

    /**
     * Method that injects the values into any configured fields, by wrapping the given
     * InjectionTarget.
     * @param pit the injection target
     * @param <T> the target type
     */
    public <T> void initializeConfiguredFields(final @Observes ProcessInjectionTarget<T> pit) {
        final AnnotatedType<T> at = pit.getAnnotatedType();
        if (!isConfigured(at.getJavaClass())) {
            return;
        }
        final InjectionTarget<T> it = pit.getInjectionTarget();
        InjectionTarget<T> wrapped = new InjectionTarget<T>() {
            @Override
            public void inject(T instance, CreationalContext<T> ctx) {
                it.inject(instance, ctx);
                ConfigurationInjection.getConfigurationInjector().configure(instance);
            }

            @Override
            public void postConstruct(T instance) {
                it.postConstruct(instance);
            }

            @Override
            public void preDestroy(T instance) {
                it.dispose(instance);
            }

            @Override
            public void dispose(T instance) {
                it.dispose(instance);
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public T produce(CreationalContext<T> ctx) {
                return it.produce(ctx);
            }
        };
        pit.setInjectionTarget(wrapped);
    }

    private boolean isConfigured(Class type) {
        if (type.getAnnotation(ConfigDefaultSections.class) != null) {
            return true;
        }
        // if no class level annotation is there we might have field level annotations only
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Config.class)) {
                return true;
            }
        }
        // if no class level annotation is there we might have method level annotations only
        for (Method method : type.getDeclaredMethods()) {
            if(method.isAnnotationPresent(Config.class)) {
                return true;
            }
        }
        return false;
    }


}
