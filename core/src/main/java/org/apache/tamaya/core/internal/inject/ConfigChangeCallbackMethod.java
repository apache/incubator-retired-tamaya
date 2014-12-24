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
package org.apache.tamaya.core.internal.inject;

import org.apache.tamaya.ConfigChangeSet;
import org.apache.tamaya.Configuration;

import java.beans.PropertyChangeEvent;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class holds a method object that is annotated to be a callback method to be called on configuration
 * changes.
 */
public final class ConfigChangeCallbackMethod {

    private static final Logger LOG = Logger.getLogger(ConfigChangeCallbackMethod.class.getName());

    private Method callbackMethod;

    public ConfigChangeCallbackMethod(Method callbackMethod) {
        this.callbackMethod = Optional.of(callbackMethod).filter(
                (m) -> void.class.equals(m.getReturnType()) &&
                        m.getParameterCount() == 1 &&
                        m.getParameterTypes()[0].equals(ConfigChangeSet.class)).get();
    }

    public Consumer<ConfigChangeSet> createConsumer(Object instance, Configuration... configurations){
        // TODO consider also environment !
        return event -> {
            for(Configuration cfg:configurations){
                if(event.getPropertySource().getName().equals(cfg.getName())){
                    return;
                }
            }
            call(instance, event);
        };
    }

    public void call(Object instance, ConfigChangeSet configChangeEvent) {
        try {
            callbackMethod.setAccessible(true);
            callbackMethod.invoke(instance, configChangeEvent);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e, () -> "Error calling ConfigChange callback method " + callbackMethod.getDeclaringClass().getName() + '.' + callbackMethod.getName() + " on " + instance);
        }
    }
}
