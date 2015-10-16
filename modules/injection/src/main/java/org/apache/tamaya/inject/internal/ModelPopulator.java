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
package org.apache.tamaya.inject.internal;

import org.apache.tamaya.inject.api.InjectionUtils;
import org.apache.tamaya.model.ConfigValidator;
import org.apache.tamaya.model.spi.ParameterValidation;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Internal facade that registers all kind of injected fields as {@link org.apache.tamaya.model.Validation} entries,
 * so all configured injection points are visible as documented configuration hooks.
 */
final class ModelPopulator {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(ModelPopulator.class.getName());

    /**
     * The singleton class of the model module to be tested for availability on the classpath.
     */
    private static final String MODEL_SINGLETON_CLASS = "org.apache.tamaya.model.ConfigValidator";
    /**
     * Flag initialized to true, if the model module is visible on the same classloader, also this module was loaded.
     */
    private static boolean modelModuleLoaded = checkModelModuleLoaded();

    /**
     * Initializes the flag, controlling the availability of this component.
     *
     * @return true, if the tamaya-model module is visible.
     */
    private static boolean checkModelModuleLoaded() {
        try {
            Class.forName(MODEL_SINGLETON_CLASS, false, ModelPopulator.class.getClassLoader());
            LOG.info("Tamaya Model extension is available. Validation models will be updated" +
                    " based on injected config.");
            return true;
        } catch (Exception e) {
            LOG.info("Tamaya Model extension not available. Validation models will not be updated" +
                    " based on injected config.");
            return false;
        }
    }

    /**
     * Utility classes should not be instantiated.
     */
    private ModelPopulator(){}

    /**
     * Registers the given {@link ConfiguredType} into the tamaya-model validation/documentation module.
     *
     * @param confType the type, not null.
     */
    public static void register(ConfiguredType confType) {
        if (!modelModuleLoaded) {
            return;
        }
        for (ConfiguredField field : confType.getConfiguredFields()) {
            Collection<String> keys = field.getConfiguredKeys();
            for (String key : keys) {
                ParameterValidation val = ConfigValidator.getValidation(key, ParameterValidation.class);
                if (val == null) {
                    InjectableValidationProvider.addValidation(new ParameterValidation.Builder(key)
                            .setType(field.getType().getName())
                            .setDescription("Injected field: " +
                                    field.annotatedField.getDeclaringClass().getName() + '.' + field.toString() +
                                    ", \nconfigured with keys: " + keys)
                            .build());
                }
            }
        }
        for (ConfiguredSetterMethod method : confType.getConfiguredSetterMethods()) {
            Collection<String> keys = method.getConfiguredKeys();
            for (String key : keys) {
                ParameterValidation val = ConfigValidator.getValidation(key, ParameterValidation.class);
                if (val == null) {
                    InjectableValidationProvider.addValidation(new ParameterValidation.Builder(key)
                            .setType(method.getParameterType().getName())
                            .setDescription("Injected field: " +
                                    method.getAnnotatedMethod().getDeclaringClass().getName() + '.' + method.toString() +
                                    ", \nconfigured with keys: " + keys)
                            .build());
                }
            }
        }
//        for (ConfigChangeCallbackMethod callback : confType.getObserverMethods() {
//            Collection<String> keys = callback.getConfiguredKeys();
//            for (String key : keys) {
//                CallbackValidation val = ConfigValidator.getValidation(key, CallbackValidation.class);
//                if (val == null) {
//                    InjectableValidationProvider.addValidation(new CallbackValidation(key, callback));
//                }
//            }
//        }

    }

    public static void registerTemplate(Class<?> type) {
        if (!modelModuleLoaded) {
            return;
        }
        for (Method method : type.getMethods()) {
            if (method.getDeclaringClass() == Object.class) {
                // skip methods decalred on object.
                continue;
            }
            Collection<String> keys = InjectionUtils.getKeys(method);
            for (String key : keys) {
                ParameterValidation val = ConfigValidator.getValidation(key, ParameterValidation.class);
                if (val == null) {
                    InjectableValidationProvider.addValidation(new ParameterValidation.Builder(key)
                            .setType(method.getReturnType().getName())
                            .setDescription("Template method: " +
                                    type.getName() + '.' + method.toString() + ", \n" +
                                    "configured with keys: " + keys)
                            .build());
                }
            }
        }
    }
}
