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

import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import org.apache.tamaya.mutableconfig.ChangePropagationPolicy;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.apache.tamaya.mutableconfig.MutableConfigurationProvider;
import org.apache.tamaya.mutableconfig.propertysources.ConfigChangeContext;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Tamaya UI view to change configuration.
 */
public class TransactionControlWidget extends HorizontalLayout {

    private Field taID = new TextField("Transaction ID");
    private Field taContent = new TextArea("Transaction Context");
    private VerticalLayout taLayout = new VerticalLayout(taID, taContent);

    private CheckBox autoCommit = new CheckBox(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.edit.box.autoCommit"));

    private ComboBox changePropagationPolicy = new ComboBox(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.edit.select.propagationPolicy"),
            Arrays.asList(new String[]{"ALL", "MOST_SIGNIFICANT_ONLY", "NONE", "CUSTOM"}));

    private TextField changePropagationPolicyOther = new TextField(
            ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.edit.text.propagationPolicyOther"),
            MutableConfigurationProvider.getApplyAllChangePolicy().getClass().getName());

    private MutableConfiguration mutableConfig;
    private Button startTAButton = new Button(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("view.edit.button.startTransaction"));
    private Button rollbackTAButton = new Button(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("view.edit.button.rollbackTransaction"));
    private Button commitTAButton = new Button(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("view.edit.button.commitTransaction"));
    private ProtocolWidget logWriter;
    private VerticalSpacedLayout leftLayout = new VerticalSpacedLayout();

    public TransactionControlWidget(MutableConfiguration mutableConfig, ProtocolWidget logWriter) {
        taContent.setReadOnly(true);
        taContent.setWidth(600, Unit.PIXELS);
        taContent.setHeight(250, Unit.PIXELS);
        taLayout.setWidth(600, Unit.PIXELS);
        taID.setReadOnly(true);
        taID.setWidth(100, Unit.PERCENTAGE);
        this.mutableConfig = Objects.requireNonNull(mutableConfig);
        this.logWriter = Objects.requireNonNull(logWriter);
        changePropagationPolicy.setWidth(300, Unit.PIXELS);
        changePropagationPolicyOther.
                setWidth(600, Unit.PIXELS);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addComponents(startTAButton, commitTAButton, rollbackTAButton);
        leftLayout.addComponents(changePropagationPolicy, changePropagationPolicyOther, buttonLayout);
        addComponents(leftLayout, taLayout);
        initActions();
        update();
    }

    private void initActions() {
        autoCommit.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                mutableConfig.setAutoCommit(autoCommit.getValue());
                if(mutableConfig.getAutoCommit()) {
                    Notification.show("Autocommit is now ON.",
                            Notification.Type.TRAY_NOTIFICATION);
                }else{
                    Notification.show("Autocommit is now OFF.",
                            Notification.Type.TRAY_NOTIFICATION);
                }
                logWriter.println(" - Set Auto-Commit to " + autoCommit.getValue());
            }
        });
        changePropagationPolicy.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                changePropagationPolicyOther.setEnabled(false);
                changePropagationPolicyOther.addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                        String className = changePropagationPolicyOther.getValue();
                        try {
                            mutableConfig.setChangePropagationPolicy(
                                    (ChangePropagationPolicy) Class.forName(className).newInstance());
                            logWriter.println(" - Set ChangePropagationPolicy " + className);
                            Notification.show("ChangePropagationPolicy is now CUSTOM: " + className);
                        } catch (Exception e) {
                            Notification.show("Failed to apply change policy: " + className + ": " + e,
                                    Notification.Type.ERROR_MESSAGE);
                        }
                    }
                });
                switch ((String) changePropagationPolicy.getValue()) {
                    case "MOST_SIGNIFICANT_ONLY":
                        mutableConfig.setChangePropagationPolicy(
                                MutableConfigurationProvider.getApplyMostSignificantOnlyChangePolicy());
                        Notification.show("ChangePropagationPolicy is now MOST_SIGNIFICANT_ONLY.",
                                Notification.Type.TRAY_NOTIFICATION);
                        logWriter.println(" - Set ChangePropagationPolicy to MOST_SIGNIFICANT_ONLY.");
                        break;
                    case "NONE":
                        Notification.show("Applying none equals being your config READ-ONLY.",
                                Notification.Type.ASSISTIVE_NOTIFICATION);
                        mutableConfig.setChangePropagationPolicy(
                                MutableConfigurationProvider.getApplyNonePolicy());
                        Notification.show("ChangePropagationPolicy is now NONE.", Notification.Type.TRAY_NOTIFICATION);
                        logWriter.println(" - Set ChangePropagationPolicy to NONE.");
                        break;
                    case "CUSTOM":
                        changePropagationPolicyOther.setEnabled(true);
                        break;
                    case "ALL":
                    default:
                        mutableConfig.setChangePropagationPolicy(
                                MutableConfigurationProvider.getApplyAllChangePolicy());
                        Notification.show("ChangePropagationPolicy is now ALL.", Notification.Type.TRAY_NOTIFICATION);
                        logWriter.println(" - Set ChangePropagationPolicy to ALL.");
                }
            }
        });
        startTAButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String taId = mutableConfig.startTransaction();
                update();
                Notification.show("Transaction started: " + taId, Notification.Type.TRAY_NOTIFICATION);
                logWriter.println("Started Transaction: " + taId);
            }
        });
        rollbackTAButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String taId = mutableConfig.getTransactionId();
                mutableConfig.rollbackTransaction();
                update();
                Notification.show("Transaction rolled back: " + taId, Notification.Type.TRAY_NOTIFICATION);
                logWriter.println("Rolled back Transaction: " + taId);
            }
        });
        commitTAButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String taId = mutableConfig.getTransactionId();
                mutableConfig.commitTransaction();
                update();
                Notification.show("Transaction comitted: "  + taId, Notification.Type.TRAY_NOTIFICATION);
                logWriter.println("Committed Transaction: " + taId);
            }
        });
    }

    public void update(){
        taID.setReadOnly(false);
        taContent.setReadOnly(false);
        if(mutableConfig.getTransactionId()==null){
            taID.setValue("N/A");
        }else {
            taID.setValue(mutableConfig.getTransactionId());
        }
        StringBuilder b = new StringBuilder();
        ConfigChangeContext changes = mutableConfig.getConfigChangeContext();
        if(mutableConfig.getTransactionId()==null){
            startTAButton.setEnabled(true);
            rollbackTAButton.setEnabled(false);
            commitTAButton.setEnabled(false);
            changePropagationPolicy.setEnabled(true);
            changePropagationPolicyOther.setEnabled(true);
            b.append("No Transaction Context available.");
        }else{
            b.append("TA ID      : ").append(changes.getTransactionID()).append('\n');
            b.append("Started at : ").append(changes.getStartedAt()).append("\n\n");
            b.append("PUT:\n");
            b.append("====\n");
            for(Map.Entry<String,String> en:changes.getAddedProperties().entrySet()){
                b.append(en.getKey()).append(" = ").append(en.getValue()).append("\n\n");
            }
            b.append("DEL:\n");
            b.append("====\n");
            for(String key:changes.getRemovedProperties()){
                b.append(key).append("\n\n");
            }
            startTAButton.setEnabled(false);
            rollbackTAButton.setEnabled(true);
            commitTAButton.setEnabled(true);
            changePropagationPolicy.setEnabled(false);
            changePropagationPolicyOther.setEnabled(false);
        }
        taContent.setValue(b.toString());
        taID.setReadOnly(true);
        taContent.setReadOnly(true);
    }

    private String getCaption(String key, String value) {
        int index = key.lastIndexOf('.');
        if (index < 0) {
            return key + " = " + value;
        } else {
            return key.substring(index + 1) + " = " + value;
        }
    }

}