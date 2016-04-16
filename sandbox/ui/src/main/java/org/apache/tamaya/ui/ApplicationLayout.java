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
package org.apache.tamaya.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.components.LazyProvider;
import org.apache.tamaya.ui.components.PageTitleUpdater;
import org.apache.tamaya.ui.internal.ResourceBundleMessageProvider;
import org.apache.tamaya.ui.views.ErrorView;


public class ApplicationLayout extends HorizontalLayout {

    private NavBar navBar;
    private Panel content;
    private Navigator navigator;

    public ApplicationLayout() {
        addStyleName(UIConstants.MAIN_LAYOUT);

        setSizeFull();

        initLayouts();
        setupNavigator();
    }

    private void initLayouts() {
        navBar = new NavBar();
        // Use panel as main content container to allow it's content to scroll
        content = new Panel();
        content.setSizeFull();
        content.addStyleName(UIConstants.PANEL_BORDERLESS);

        addComponents(navBar, content);
        setExpandRatio(content, 1);
    }

    private void setupNavigator() {
        navigator = new Navigator(MyUI.getCurrent(), content);

        registerViews();

        // Add view change listeners so we can do things like select the correct menu item and update the page title
        navigator.addViewChangeListener(navBar);
        navigator.addViewChangeListener(new PageTitleUpdater());

        navigator.navigateTo(navigator.getState());
    }

    private void registerViews() {
        for(ViewProvider provider: ServiceContextManager.getServiceContext().getServices(ViewProvider.class)) {
            addView(provider);
        }
        navigator.setErrorView(ErrorView.class);
    }

    /**
     * Registers av given view to the navigator and adds it to the NavBar
     */
    private void addView(ViewProvider provider) {

        switch (provider.getLifecycle()) {
            case CREATE:
                navigator.addView(provider.getUrlPattern(), provider.createView());
                break;
            case LAZY:
                navigator.addProvider(new LazyProvider(provider.getUrlPattern(), provider));
                break;
            case EAGER:
                try {
                    navigator.addView(provider.getUrlPattern(), provider.createView());
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        navBar.addView(provider.getUrlPattern(), provider.getDisplayName());
    }
}