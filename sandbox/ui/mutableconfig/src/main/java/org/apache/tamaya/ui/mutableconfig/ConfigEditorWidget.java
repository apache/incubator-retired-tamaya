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
package org.apache.tamaya.ui.mutableconfig;

import com.vaadin.ui.*;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.services.MessageProvider;

import java.util.Objects;

/**
 * Tamaya UI view to change configuration.
 */
public class ConfigEditorWidget extends FormLayout {

    private MutableConfiguration mutableConfig;

    private ProtocolWidget logWriter;
    private TransactionControlWidget taWidget;

    private TextField configKey = new TextField(
            ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.edit.text.configKey"));
    private TextField configValue = new TextField(
            ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.edit.text.configValue"));
    private Button updateButton = new Button(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("view.edit.button.updateKey"));
    private Button removeButton = new Button(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("view.edit.button.removeKey"));
    private Button readButton = new Button(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("view.edit.button.readKey"));

    public ConfigEditorWidget(MutableConfiguration mutableConfig, ProtocolWidget logWriter, TransactionControlWidget taWidget) {
        this.mutableConfig = Objects.requireNonNull(mutableConfig);
        this.logWriter = Objects.requireNonNull(logWriter);
        this.taWidget = Objects.requireNonNull(taWidget);
        configKey.setWidth(50, Unit.PERCENTAGE);
        configValue.setWidth(50, Unit.PERCENTAGE);
        addComponents(configKey, configValue);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addComponents(readButton, new Label("   "), updateButton, removeButton);
        buttonLayout.setSpacing(true);
        addComponents(buttonLayout);
        initActions();
    }

    private void initActions() {
        updateButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(mutableConfig.isWritable(configKey.getValue())){
                    mutableConfig.put(configKey.getValue(), configValue.getValue());
                    Notification.show("Added " + configKey.getValue() + " = " + configValue.getValue(),
                            Notification.Type.TRAY_NOTIFICATION);
                    logWriter.println(" - PUT " + configKey.getValue() + " = " + configValue.getValue());
                    configKey.setValue("");
                    configValue.setValue("");
                }else{
                    Notification.show("Could not add " + configKey.getValue() + " = " + configValue.getValue(),
                            Notification.Type.ERROR_MESSAGE);
                    logWriter.println(" - PUT " + configKey.getValue() + " rejected - not writable.");
                }
                taWidget.update();
            }
        });
        removeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(mutableConfig.isRemovable(configKey.getValue())){
                    mutableConfig.remove(configKey.getValue());
                    logWriter.println(" - DEL " + configKey.getValue());
                    Notification.show("Removed " + configKey.getValue(),
                            Notification.Type.TRAY_NOTIFICATION);
                    configKey.setValue("");
                    configValue.setValue("");
                }else{
                    Notification.show("Could not remove " + configKey.getValue(),
                            Notification.Type.ERROR_MESSAGE);
                    logWriter.println(" - DEL " + configKey.getValue() + " rejected - not removable.");
                }
                taWidget.update();
            }
        });
        readButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(mutableConfig.isExisting(configKey.getValue())){
                    String key = configKey.getValue();
                    configValue.setValue(mutableConfig.get(key));
                    Notification.show("Successfully read " + configKey.getValue(),
                            Notification.Type.TRAY_NOTIFICATION);
                    logWriter.println(" - GET " + key + " = " + configValue.getValue());
                    logWriter.println("   - removable: " + mutableConfig.isRemovable(key));
                    logWriter.println("   - writable : " + mutableConfig.isWritable(key));
                }else{
                    Notification.show("Could not read " + configKey.getValue(),
                            Notification.Type.ERROR_MESSAGE);
                    logWriter.println(" - GET " + configKey.getValue() + " rejected - not existing.");
                }
                taWidget.update();
            }
        });
    }

    private String getCaption(String key, String value) {
        int index = key.lastIndexOf('.');
        if(index<0){
            return key + " = " + value;
        }else{
            return key.substring(index+1) + " = " + value;
        }
    }

}