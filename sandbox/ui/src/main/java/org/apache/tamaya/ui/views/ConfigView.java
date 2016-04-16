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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;
import java.util.Map;


public class ConfigView extends VerticalSpacedLayout implements View {

    @Priority(10)
    public static final class Provider implements ViewProvider{

        @Override
        public ViewLifecycle getLifecycle() {
            return ViewLifecycle.CREATE;
        }

        @Override
        public String getUrlPattern() {
            return "/config";
        }

        @Override
        public String getDisplayName() {
            return ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.config.name");
        }

        @Override
        public View createView(){
            return new ConfigView();
        }
    }

    private TextField keyFilter = new TextField("Key filter");
    private TextField valueFilter = new TextField("Value filter");
    private Tree tree = new Tree("Current Configuration");

    public ConfigView() {
        Label caption = new Label("Raw Configuration");
        Label description = new Label(
                "This view shows the overall <b>raw</b> configuration tree. Dependening on your access rights you" +
                        "may see partial or masked data. Similarly configuration can be <i>read-only</i> or <i>mutable</i>.",
                ContentMode.HTML);
        HorizontalLayout filters = new HorizontalLayout();

        Button filterButton = new Button("Filter", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                fillTree();
            }
        });
        filters.setDefaultComponentAlignment(Alignment.BOTTOM_LEFT);
        filters.addComponents(keyFilter, valueFilter, filterButton);

        fillTree();

        addComponents(caption, description, filters, tree);

        caption.addStyleName(UIConstants.LABEL_HUGE);
        description.addStyleName(UIConstants.LABEL_LARGE);

    }

    private void fillTree() {
        String keyFilterExp = this.keyFilter.getValue();
        if(keyFilterExp.isEmpty()){
            keyFilterExp = null;
        }
        String valueFilterExp = this.valueFilter.getValue();
        if(valueFilterExp.isEmpty()){
            valueFilterExp = null;
        }
        tree.removeAllItems();
        for(Map.Entry<String,String> entry: ConfigurationProvider.getConfiguration().getProperties().entrySet()){
            String key = entry.getKey();
            if(keyFilterExp!=null && !key.matches(keyFilterExp)){
                continue;
            }
            if(valueFilterExp!=null && !entry.getValue().matches(valueFilterExp)){
                continue;
            }
            tree.addItem(key);
            tree.setItemCaption(key, getCaption(key, entry.getValue()));
            tree.setChildrenAllowed(key, false);
            String parent = null;
            int start = 0;
            int index = key.indexOf('.', start);
            while(index>0){
                String subItem = key.substring(0,index);
                String caption = key.substring(start, index);
                tree.addItem(subItem);
                tree.setItemCaption(subItem, caption);
                if(parent!=null){
                    tree.setParent(subItem, parent);
                }
                parent = subItem;
                start = index+1;
                index = key.indexOf('.', start);
            }
            String lastItem = key.substring(start);
            if(!lastItem.equals(key)){
                if(parent!=null){
                    tree.setParent(key, parent);
                }else{
                    // should not happen
                }
            }else{ // singl root entry
                if(parent!=null) {
                    tree.setParent(key, parent);
                }
            }
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