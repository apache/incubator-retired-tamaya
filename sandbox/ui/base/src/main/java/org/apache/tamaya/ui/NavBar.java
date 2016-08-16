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

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.event.EventBus;
import org.apache.tamaya.ui.event.LogoutEvent;
import org.apache.tamaya.ui.event.NavigationEvent;
import org.apache.tamaya.ui.services.MessageProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Left side navigation bar.
 */
public class NavBar extends VerticalLayout implements ViewChangeListener {

    private Map<String, Button> buttonMap = new HashMap<>();

    public NavBar(ApplicationLayout appLayout) {
        // setHeight("100%");
        setWidth(200, Unit.PIXELS);
        addStyleName(UIConstants.MENU_ROOT);
        addStyleName(UIConstants.NAVBAR);
        setDefaultComponentAlignment(Alignment.TOP_LEFT);
        MessageProvider messages = ServiceContextManager.getServiceContext().getService(MessageProvider.class);
        Label logo = new Label("<strong>"+ messages.getMessage("project.name")+"</strong>", ContentMode.HTML);
        logo.addStyleName(UIConstants.MENU_TITLE);
        addComponent(logo);
        addLogoutAndSettingsButton(appLayout);
    }


    private void addLogoutAndSettingsButton(final ApplicationLayout appLayout) {
        MessageProvider messages = ServiceContextManager.getServiceContext().getService(MessageProvider.class);
        Button logout = new Button(messages.getMessage("default.label.logout"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                User user = CurrentUser.get();
                if(user!=null){
                    user.logout();
                    EventBus.post(new LogoutEvent(user));
                }
                CurrentUser.set(null);
            }
        });
        logout.addStyleName(UIConstants.BUTTON_LOGOUT);
        logout.addStyleName(UIConstants.BUTTON_BORDERLESS);
        logout.setIcon(FontAwesome.SIGN_OUT);
        Button settings = new Button("...", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                UISettingsDialog dlog = new UISettingsDialog(appLayout.getNavigationBar());
                dlog.show();
            }
        });
        settings.addStyleName(UIConstants.BUTTON_SETTINGS);
        settings.addStyleName(UIConstants.BUTTON_BORDERLESS);
        VerticalLayout buttons = new VerticalLayout(logout, settings);
        addComponent(buttons);
    }

    public void addViewButton(final String uri, String displayName) {
        Button viewButton = new Button(displayName, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                EventBus.post(new NavigationEvent(uri));
            }
        });
        viewButton.addStyleName(UIConstants.BUTTON_LOGOUT);
        // viewButton.addStyleName(UIConstants.MENU_ITEM);
        viewButton.addStyleName(UIConstants.BUTTON_BORDERLESS);
        addComponent(viewButton, components.size() - 1);
        viewButton.setHeight(20, Unit.PIXELS);

        buttonMap.put(uri, viewButton);
    }

    public void removeViewButton(String uri) {
        Button button = buttonMap.remove(uri);
        if(button!=null) {
            removeComponent(button);
        }
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        return true; // false blocks navigation, always return true here
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
        for(Button button: buttonMap.values()){
            button.removeStyleName(UIConstants.SELECTED);
        }
        Button button = buttonMap.get(event.getViewName());
        if (button != null) {
            button.addStyleName(UIConstants.SELECTED);
        }
    }


}