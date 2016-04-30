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
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.tamaya.mutableconfig.ChangePropagationPolicy;
import org.apache.tamaya.mutableconfig.MutableConfiguration;
import org.apache.tamaya.mutableconfig.MutableConfigurationProvider;
import org.apache.tamaya.mutableconfig.spi.MutablePropertySource;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;

/**
 * Tamaya UI view to change configuration.
 */
public class TransactionControlWidget extends VerticalSpacedLayout {

    private CheckBox autoCommit = new CheckBox(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.edit.box.autoCommit"));

    private ComboBox changePropagationPolicy = new ComboBox(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.edit.select.propagationPolicy"),
            Arrays.asList(new String[]{"ALL", "MOST_SIGNIFICANT_ONLY", "SELECTIVE", "NONE", "CUSTOM"}));

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

    public TransactionControlWidget(MutableConfiguration mutableConfig, ProtocolWidget logWriter) {
        this.mutableConfig = Objects.requireNonNull(mutableConfig);
        this.logWriter = Objects.requireNonNull(logWriter);
        changePropagationPolicy.setWidth(300, Unit.PIXELS);
        changePropagationPolicyOther.
                setWidth(600, Unit.PIXELS);
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addComponents(startTAButton, commitTAButton, rollbackTAButton);
        addComponents(changePropagationPolicy, changePropagationPolicyOther, buttonLayout);
        initActions();
    }

    private void initActions() {
        autoCommit.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                mutableConfig.setAutoCommit(autoCommit.getValue());
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
                            Notification.show("Successfully applied change policy: " + className);
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
                        logWriter.println(" - Set ChangePropagationPolicy to MOST_SIGNIFICANT_ONLY.");
                        break;
                    case "SELECTIVE":
//                        mutableConfig.setChangePropagationPolicy(
//                                MutableConfigurationProvider.getApplySelectiveChangePolicy("source1", "source2");
                        Notification.show("Selective Backends are not yet supported by the UI.",
                                Notification.Type.WARNING_MESSAGE);
                        break;
                    case "NONE":
                        Notification.show("Applying none equals being your config READ-ONLY.",
                                Notification.Type.ASSISTIVE_NOTIFICATION);
                        mutableConfig.setChangePropagationPolicy(
                                MutableConfigurationProvider.getApplyNonePolicy());
                        logWriter.println(" - Set ChangePropagationPolicy to NONE.");
                        break;
                    case "CUSTOM":
                        changePropagationPolicyOther.setEnabled(true);
                        break;
                    case "ALL":
                    default:
                        mutableConfig.setChangePropagationPolicy(
                                MutableConfigurationProvider.getApplyAllChangePolicy());
                        logWriter.println(" - Set ChangePropagationPolicy to ALL.");
                }
            }
        });
        startTAButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mutableConfig.startTransaction();
                logWriter.println("Started Transaction: " + mutableConfig.getTransactionId());
            }
        });
        rollbackTAButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mutableConfig.rollbackTransaction();
                logWriter.println("Rolled back Transaction: " + mutableConfig.getTransactionId());
            }
        });
        commitTAButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                mutableConfig.commitTransaction();
                logWriter.println("Committed Transaction: " + mutableConfig.getTransactionId());
            }
        });
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