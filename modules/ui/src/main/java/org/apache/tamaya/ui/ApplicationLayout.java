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

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import org.apache.tamaya.ui.components.PageTitleUpdater;
import org.apache.tamaya.ui.views.ErrorView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UI main layout.
 */
public class ApplicationLayout extends HorizontalLayout {

    private NavBar navBar;
    private Panel content;
    private NavigationBar navigator;

    public ApplicationLayout(UI ui) {
        addStyleName(UIConstants.MAIN_LAYOUT);
        setSizeFull();
        initLayouts();
        setupNavigator(ui);
    }

    public NavigationBar getNavigationBar(){
        return navigator;
    }

    private void initLayouts() {
        navBar = new NavBar(this);
        // Use panel as main content container to allow it's content to scroll
        content = new Panel();
        content.setSizeFull();
        content.addStyleName(UIConstants.PANEL_BORDERLESS);

        addComponents(navBar, content);
        setExpandRatio(content, 1);
    }


    private void setupNavigator(UI ui) {
        navigator = new NavigationBar(ui, content, navBar);

        // Add view change listeners so we can do things like select the correct menu item and update the page title
        navigator.addViewChangeListener(navBar);
        navigator.addViewChangeListener(new PageTitleUpdater());

        navigator.navigateTo("/home");
        navigator.setErrorView(ErrorView.class);
    }


}