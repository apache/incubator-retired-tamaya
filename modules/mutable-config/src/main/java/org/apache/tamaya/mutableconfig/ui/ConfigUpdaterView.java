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
package org.apache.tamaya.mutableconfig.ui;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.apache.tamaya.mutableconfig.MutableConfigurationProvider;
import org.apache.tamaya.mutableconfig.spi.MutablePropertySource;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;

/**
 * Tamaya UI view to change configuration.
 */
public class ConfigUpdaterView extends VerticalSpacedLayout implements View {

    /**
     * Provider to register the view.
     */
    @Priority(50)
    public static final class Provider implements ViewProvider{

        @Override
        public ViewLifecycle getLifecycle() {
            return ViewLifecycle.LAZY;
        }

        @Override
        public String getName() {
            return "view.edit.name";
        }

        @Override
        public String getUrlPattern() {
            return "/edit";
        }

        @Override
        public String getDisplayName() {
            return ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.edit.name");
        }

        @Override
        public View createView(Object... params){
            return new ConfigUpdaterView();
        }
    }

    private ProtocolWidget logWidget = new ProtocolWidget();
    private PopupView logPopup = new PopupView("Show log", logWidget);

    private MutableConfiguration mutableConfig = MutableConfigurationProvider.getMutableConfiguration();

    private TransactionControlWidget taControl = new TransactionControlWidget(mutableConfig,
            logWidget);
    private PopupView taDetails = new PopupView("Transaction Details", taControl);

    private ConfigEditorWidget editorWidget = new ConfigEditorWidget(mutableConfig, logWidget, taControl);


    public ConfigUpdaterView() {
        Label caption = new Label(ServiceContextManager.getServiceContext()
                .getService(MessageProvider.class).getMessage("view.edit.name"));
        Label description = new Label(ServiceContextManager.getServiceContext()
                .getService(MessageProvider.class).getMessage("view.edit.description"),
                ContentMode.HTML);

        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);
        logWidget.print("INFO: Writable Property Sources: ");
        for(MutablePropertySource ps:mutableConfig.getMutablePropertySources()){
            logWidget.print(ps.getName(), ", ");
        }
        logWidget.println();
        logWidget.setHeight(100, Unit.PERCENTAGE);
        HorizontalLayout hl = new HorizontalLayout(taDetails, logPopup);
        hl.setSpacing(true);
        addComponents(caption, description, editorWidget, hl);
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