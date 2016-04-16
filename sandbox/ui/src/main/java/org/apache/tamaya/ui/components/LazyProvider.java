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
import org.apache.tamaya.ui.ViewProvider;

import java.util.Objects;

/**
 * Lazily initializes a view when it's first accessed, then always returns the
 * same instance on subsequent calls.
 */
public class LazyProvider implements com.vaadin.navigator.ViewProvider {
    private ViewProvider provider;
    private View view;

    public LazyProvider(String viewName, ViewProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public String getViewName(String s) {
        return provider.getDisplayName();
    }

    @Override
    public View getView(String viewName) {
        if (view == null) {
            view = provider.createView();
        }
        return view;
    }
}