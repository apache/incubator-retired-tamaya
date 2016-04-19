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
package org.apache.tamaya.ui.views;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.CurrentUser;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;

/**
 * Home view containing a title and a description, used as default entry point of the UI after login.
 */
public class HomeView extends VerticalSpacedLayout implements View {

    /**
     * Provider to bew registered providing this view to the UI module.
     */
    @Priority(0)
    public static final class Provider implements ViewProvider{

        @Override
        public ViewLifecycle getLifecycle() {
            return ViewLifecycle.CREATE;
        }

        @Override
        public String getUrlPattern() {
            return "";
        }

        @Override
        public String getDisplayName() {
            return ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.home.name");
        }

        @Override
        public View createView(){
            return new HomeView();
        }
    }

    /**
     * Constructor.
     */
    public HomeView() {
        Label caption = new Label("Welcome, " + CurrentUser.get().getUserID());
        Label description = new Label(
                "<b>Apache Tamaya</b> is an API and extendable framework for accessing and managing configuration.<br/> \n" +
                        "Please check the project's home page <a href='http://tamaya.incubator.apache.org'>http://tamaya.incubator.apache.org</a>.",
                ContentMode.HTML);

        addComponents(caption, description);

        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);

    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // nothing to do
    }
}