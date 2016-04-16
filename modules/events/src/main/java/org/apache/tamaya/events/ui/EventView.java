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
package org.apache.tamaya.events.ui;

import com.vaadin.data.Property;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.tamaya.events.ConfigEvent;
import org.apache.tamaya.events.ConfigEventListener;
import org.apache.tamaya.events.ConfigEventManager;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;


public class EventView extends VerticalSpacedLayout implements View {

    @Priority(20)
    public static final class Provider implements ViewProvider{

        @Override
        public ViewLifecycle getLifecycle() {
            return ViewLifecycle.EAGER;
        }

        @Override
        public String getUrlPattern() {
            return "/events";
        }

        @Override
        public String getDisplayName() {
            return "view.events.name";
        }

        @Override
        public View createView(){
            return new EventView();
        }
    }

    private CheckBox changeMonitorEnabled = new CheckBox(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.events.button.enableMonitoring"));
    private Button clearViewButton = new Button(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.events.button.clearView"));
    private Table eventsTable = new Table(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.events.table.name"));


    public EventView() {
        Label caption = new Label(ServiceContextManager.getServiceContext()
                .getService(MessageProvider.class).getMessage("view.events.name"));
        Label description = new Label(ServiceContextManager.getServiceContext()
                .getService(MessageProvider.class).getMessage("view.events.description"),
                ContentMode.HTML);

        ConfigEventManager.addListener(new ConfigEventListener() {
            @Override
            public void onConfigEvent(ConfigEvent<?> event) {
                addEvent(event);
            }
        });
        changeMonitorEnabled.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                ConfigEventManager.enableChangeMonitoring(changeMonitorEnabled.getValue());
            }
        });
        clearViewButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                eventsTable.removeAllItems();
            }
        });

        changeMonitorEnabled.setData(ConfigEventManager.isChangeMonitoring());
        eventsTable.addContainerProperty("Timestamp", Long.class, null);
        eventsTable.addContainerProperty("Type", Class.class, null);
        eventsTable.addContainerProperty("Payload", String.class, null);
        eventsTable.addContainerProperty("Version",  String.class, null);
        eventsTable.setPageLength(20);
        eventsTable.setWidth("100%");
        eventsTable.setResponsive(true);

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponents(changeMonitorEnabled, clearViewButton);
        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);
        addComponents(caption, description, hl, eventsTable);
    }

    private void addEvent(ConfigEvent<?> evt){
        eventsTable.addItem(new Object[]{evt.getTimestamp(), evt.getResourceType().getSimpleName(),
                String.valueOf(evt.getResource()),evt.getVersion()});
        this.markAsDirty();
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