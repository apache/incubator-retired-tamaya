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
package org.apache.tamaya.ui.components;

import com.vaadin.navigator.View;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.services.MessageProvider;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lazily initializes a view when it's first accessed, then always returns the
 * same instance on subsequent calls.
 */
public class LazyProvider implements ViewProvider {
    private static final Logger LOG = Logger.getLogger(
            LazyProvider.class.getName());
    private Class<? extends View> viewClass;
    private View view;
    private String urlPattern;
    private String name;

    public LazyProvider(String name, String urlPattern, Class<? extends View> viewClass) {
        this.viewClass = Objects.requireNonNull(viewClass);
        this.urlPattern = Objects.requireNonNull(urlPattern);
        this.name = Objects.requireNonNull(name);
    }

    @Override
    public String getUrlPattern() {
        return urlPattern;
    }


    @Override
    public ViewLifecycle getLifecycle() {
        return ViewLifecycle.LAZY;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                .getMessage(name);
    }

    @Override
    public View createView(Object... params) {
        if(view==null){
            try {
                view = viewClass.newInstance();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to create view: "+urlPattern, e);
            }
        }
        return view;
    }
}