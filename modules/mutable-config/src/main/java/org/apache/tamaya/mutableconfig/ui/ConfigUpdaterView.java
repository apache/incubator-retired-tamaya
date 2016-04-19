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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import org.apache.tamaya.mutableconfig.MutableConfigurationProvider;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;
import java.util.Arrays;

/**
 * Tamaya UI view to change configuration.
 */
public class ConfigUpdaterView extends VerticalSpacedLayout implements View {

    /**
     * Provider to register the view.
     */
    @Priority(20)
    public static final class Provider implements ViewProvider{

        @Override
        public ViewLifecycle getLifecycle() {
            return ViewLifecycle.LAZY;
        }

        @Override
        public String getUrlPattern() {
            return "/edit";
        }

        @Override
        public String getDisplayName() {
            return "view.update.name";
        }

        @Override
        public View createView(){
            return new ConfigUpdaterView();
        }
    }

    private ComboBox changePropagationPolicy = new ComboBox("view.update.select.propagationPolicy",
            Arrays.asList(new String[]{"ALL", "MOST_SIGNIFICANT_ONLY", "SELECTIVE", "NONE"}));

    private TextField changePropagationPolicyOther = new TextField("view.update.text.propagationPolicyOther",
            MutableConfigurationProvider.getApplyAllChangePolicy().getClass().getName());

    private TextArea generalInfo = new TextArea(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.update.textArea.general"));



    public ConfigUpdaterView() {
        Label caption = new Label(ServiceContextManager.getServiceContext()
                .getService(MessageProvider.class).getMessage("view.update.name"));
        Label description = new Label(ServiceContextManager.getServiceContext()
                .getService(MessageProvider.class).getMessage("view.update.description"),
                ContentMode.HTML);

        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);
        addComponents(caption, description,changePropagationPolicy,changePropagationPolicyOther,generalInfo);
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