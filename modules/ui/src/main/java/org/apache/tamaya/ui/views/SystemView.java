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
import com.vaadin.ui.Tree;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;

/**
 * View showing the current loaded system components.
 */
@Priority(10000)
public class SystemView extends VerticalSpacedLayout implements View {


    /**
     * Provider to register this view.
     */
    @Priority(20)
    public static final class Provider implements ViewProvider{

        @Override
        public ViewLifecycle getLifecycle() {
            return ViewLifecycle.CREATE;
        }

        @Override
        public String getName() {
            return "view.system.name";
        }

        @Override
        public String getUrlPattern() {
            return "/system";
        }

        @Override
        public String getDisplayName() {
            return ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.system.name");
        }

        @Override
        public View createView(Object... params){
            return new SystemView();
        }
    }


    private Tree configTree = new Tree(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("default.label.system"));


    public SystemView() {
        Label caption = new Label("Tamaya Runtime");
        Label description = new Label(
                "This view shows the system components currently active. This information may be useful when checking if an" +
                        "configuration extension is loaded and for inspection of the configuration and property sources" +
                        "invovlved.",
                ContentMode.HTML);

        fillComponentTree();

        addComponents(caption, description, configTree);

        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);

    }

    private void fillComponentTree() {
        configTree.removeAllItems();
        for(SystemInfoProvider infoProvider:ServiceContextManager.getServiceContext()
                .getServices(SystemInfoProvider.class)){
            infoProvider.provideSystemInfo(configTree);
        }
    }

    private String getCaption(String key, String value) {
        int index = key.lastIndexOf('.');
        if(index<0){
            return key + " = " + value;
        }else{
            return key.substring(index+1) + " = " + value;
        }
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }
}