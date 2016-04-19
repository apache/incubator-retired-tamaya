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
package org.apache.tamaya.ui.views.login;

import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.User;
import org.apache.tamaya.ui.event.EventBus;
import org.apache.tamaya.ui.services.MessageProvider;
import org.apache.tamaya.ui.services.UserService;

/**
 * Login dialog centerd on the screen.
 */
public class LoginBox extends VerticalLayout {

    private TextField username;
    private PasswordField password;

    public LoginBox() {
        setWidth("400px");
        addStyleName(UIConstants.LOGIN_BOX);
        setSpacing(true);
        setMargin(true);

        addCaption();
        addForm();
        addButtons();
    }

    private void addCaption() {
        Label caption = new Label("Login to system");
        addComponent(caption);

        caption.addStyleName(UIConstants.LABEL_H1);
    }

    private void addForm() {
        FormLayout loginForm = new FormLayout();
        MessageProvider mp = ServiceContextManager.getServiceContext().getService(MessageProvider.class);
        username = new TextField(mp.getMessage("default.label.username"));
        password = new PasswordField(mp.getMessage("default.label.password"));
        loginForm.addComponents(username, password);
        addComponent(loginForm);
        loginForm.setSpacing(true);
        for(Component component:loginForm){
            component.setWidth("100%");
        }
        username.focus();
    }

    private void addButtons() {
        HorizontalLayout buttonsLayout = new HorizontalLayout();
        Button forgotButton = new Button("Forgot", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Notification.show("Sorry, this feature is not yet implemented.", Notification.Type.TRAY_NOTIFICATION);
            }
        });
        Button loginButton = new Button("Login", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                login();
            }
        });
        buttonsLayout.addComponents(forgotButton, loginButton);
        addComponent(buttonsLayout);
        buttonsLayout.setSpacing(true);
        forgotButton.addStyleName(UIConstants.BUTTON_LINK);
        loginButton.addStyleName(UIConstants.BUTTON_PRIMARY);
        loginButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setComponentAlignment(buttonsLayout, Alignment.BOTTOM_RIGHT);
    }

    private void login() {
        User user = ServiceContextManager.getServiceContext().getService(UserService.class)
                .login(username.getValue(), password.getValue());
        if(user!=null){
            EventBus.post(new LoginEvent(user));
        }else{
            Notification.show("Login failed.", "Sorry the system could not log you in.", Notification.Type.WARNING_MESSAGE);
            username.focus();
        }
    }
}