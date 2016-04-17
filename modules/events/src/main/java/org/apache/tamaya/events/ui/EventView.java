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

import com.vaadin.data.Item;
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
import java.util.Date;


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
    private TextField pollingInterval = new TextField(ServiceContextManager.getServiceContext()
            .getService(MessageProvider.class).getMessage("view.events.field.pollingInterval"));
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
                if(changeMonitorEnabled.getValue()) {
                    Notification.show("Event Monitoring (Polling) active.");
                }else{
                    Notification.show("Event Monitoring (Polling) inactive.");
                }
            }
        });
        clearViewButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                eventsTable.removeAllItems();
                Notification.show("Events cleared.");
            }
        });

        HorizontalLayout eventSettings = new HorizontalLayout();
        eventSettings.addComponents(changeMonitorEnabled, new Label(" Polling Interval"), pollingInterval, clearViewButton);
        changeMonitorEnabled.setValue(ConfigEventManager.isChangeMonitoring());
        pollingInterval.setValue(String.valueOf(ConfigEventManager.getChangeMonitoringPeriod()));
        pollingInterval.setRequired(true);
        pollingInterval.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                try{
                    long millis = Long.parseLong((String)valueChangeEvent.getProperty().getValue());
                    ConfigEventManager.setChangeMonitoringPeriod(millis);
                    Notification.show("Updated Event Monitoring Poll Interval to " + millis + " milliseconds.");
                }catch(Exception e){
                    Notification.show("Cannot update Event Monitoring Poll Interval to "
                            + valueChangeEvent.getProperty().getValue(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        eventsTable.addContainerProperty("Timestamp", Date.class, null);
        eventsTable.addContainerProperty("Type", String.class, "?");
        eventsTable.addContainerProperty("Payload", String.class, "<empty>");
        eventsTable.addContainerProperty("Version",  String.class, "?");
        eventsTable.setPageLength(20);
        eventsTable.setWidth("100%");
        eventsTable.setResponsive(true);


        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);
        addComponents(caption, description, eventSettings, eventsTable);
    }

    private void addEvent(ConfigEvent<?> evt){
        Object newItemId = eventsTable.addItem();
        Item row = eventsTable.getItem(newItemId);
        row.getItemProperty("Timestamp").setValue(new Date(evt.getTimestamp()));
        row.getItemProperty("Type").setValue(evt.getResourceType().getSimpleName());
        String value = String.valueOf(evt.getResource());
        String valueShort = value.length()<150?value:value.substring(0,147)+"...";
        row.getItemProperty("Payload").setValue(valueShort);
        row.getItemProperty("Version").setValue(evt.getVersion());
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