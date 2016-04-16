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

import javax.servlet.annotation.WebServlet;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import org.apache.tamaya.ui.components.LazyProvider;
import org.apache.tamaya.ui.components.PageTitleUpdater;
import org.apache.tamaya.ui.event.LogoutEvent;
import org.apache.tamaya.ui.event.NavigationEvent;
import org.apache.tamaya.ui.views.ErrorView;
import org.apache.tamaya.ui.views.login.LoginEvent;
import org.apache.tamaya.ui.views.login.LoginView;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of a html page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("valo")
@Title("Tamaya")
public class MyUI extends UI {

    private Content content = new Content();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setupEventBus();

        if (CurrentUser.isLoggedIn()) {
            setContent(new ApplicationLayout());
        } else {
            setContent(new LoginView());
        }
    }

    @Subscribe
    public void userLoggedIn(
            LoginEvent event) {
        CurrentUser.set(event.getUser());
        setContent(new ApplicationLayout());
    }

    @Subscribe
    public void navigateTo(NavigationEvent view) {
        getNavigator().navigateTo(view.getViewName());
    }

    public static MyUI getCurrent() {
        return (MyUI) UI.getCurrent();
    }

    @Subscribe
    public void logout(LogoutEvent logoutEvent) {
        // Don't invalidate the underlying HTTP session if you are using it for something else
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();

    }

    private void setupEventBus() {
        org.apache.tamaya.ui.event.EventBus.register(this);
    }


    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
