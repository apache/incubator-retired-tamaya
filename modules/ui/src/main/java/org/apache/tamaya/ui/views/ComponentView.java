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
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import org.apache.tamaya.spi.ServiceContextManager;
import org.apache.tamaya.ui.UIConstants;
import org.apache.tamaya.ui.ViewProvider;
import org.apache.tamaya.ui.components.VerticalSpacedLayout;
import org.apache.tamaya.ui.services.MessageProvider;

import javax.annotation.Priority;
import java.util.List;
import java.util.Map;

/**
 * View showing the current loaded system components.
 */
public class ComponentView extends VerticalSpacedLayout implements View {


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
        public String getUrlPattern() {
            return "/components";
        }

        @Override
        public String getDisplayName() {
            return ServiceContextManager.getServiceContext().getService(MessageProvider.class)
                    .getMessage("view.components.name");
        }

        @Override
        public View createView(){
            return new ComponentView();
        }
    }


    private Tree configTree = new Tree(ServiceContextManager.getServiceContext().getService(MessageProvider.class)
            .getMessage("default.label.components"));


    public ComponentView() {
        Label caption = new Label("Components");
        Label description = new Label(
                "This view shows the components currently active. This information may be useful when checking if an" +
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
        Configuration config = ConfigurationProvider.getConfiguration();

        String currentParent = "General";
        configTree.addItem(currentParent);
        configTree.addItem("Configuration.class");
        configTree.setItemCaption("Configuration.class", "Configuration class = " + config.getClass().getName());
        configTree.setParent("Configuration.class", currentParent);
        configTree.setChildrenAllowed("Configuration.class", false);

        configTree.addItem("ConfigurationContext.class");
        configTree.setItemCaption("ConfigurationContext.class", "ConfigurationContext class = " +
                config.getContext().getClass().getName());
        configTree.setParent("ConfigurationContext.class", currentParent);
        configTree.setChildrenAllowed("ConfigurationContext.class", false);

        configTree.addItem("PropertyValueCombinationPolicy.class");
        configTree.setItemCaption("PropertyValueCombinationPolicy.class",
                PropertyValueCombinationPolicy.class.getSimpleName() + " class = " +
                        config.getContext().getPropertyValueCombinationPolicy().getClass().getName());
        configTree.setParent("PropertyValueCombinationPolicy.class", currentParent);
        configTree.setChildrenAllowed("PropertyValueCombinationPolicy.class", false);

        configTree.addItem("ConfigurationContext.types");
        configTree.setItemCaption("ConfigurationContext.types", "Configurable types");
        configTree.setParent("ConfigurationContext.types", currentParent);
        for(Map.Entry<TypeLiteral<?>,List<PropertyConverter<?>>> en:config.getContext().getPropertyConverters().entrySet()){
            configTree.addItem(en.getKey());
            configTree.setItemCaption(en.getKey(), "Type = " + en.getKey().toString());
            configTree.setParent(en.getKey(), "ConfigurationContext.types");
            for(PropertyConverter conv: en.getValue()){
                configTree.addItem(conv);
                configTree.setItemCaption(conv, conv.getClass().getName());
                configTree.setChildrenAllowed(conv, false);
                configTree.setParent(conv, en.getKey());
            }
        }
        configTree.addItem("ConfigurationContext.filters");
        configTree.setItemCaption("ConfigurationContext.filters", "Property Filters");
        for(PropertyFilter filter: config.getContext().getPropertyFilters()){
            configTree.addItem(filter);
            configTree.setItemCaption(filter, filter.getClass().getName());
            configTree.setChildrenAllowed(filter, false);
            configTree.setParent(filter, "ConfigurationContext.filters");
        }
        configTree.addItem("ConfigurationContext.sources");
        configTree.setItemCaption("ConfigurationContext.sources", "Property Sources");
        for(PropertySource source: config.getContext().getPropertySources()){
            configTree.addItem(source);
            configTree.setItemCaption(source, "name = "+source.getName());
            configTree.setParent(source, "ConfigurationContext.sources");

            configTree.addItem(source.toString() + ".ordinal");
            configTree.setItemCaption(source.toString() + ".ordinal", "ordinal = "+source.getOrdinal());
            configTree.setParent(source.toString() + ".ordinal", source);
            configTree.setChildrenAllowed(source.toString() + ".ordinal", false);
            configTree.addItem(source.toString() + ".class");
            configTree.setItemCaption(source.toString() + ".class", "class = "+source.getClass().getName());
            configTree.setChildrenAllowed(source.toString() + ".class", false);
            configTree.setParent(source.toString() + ".class", source);
            Map<String,String> props = source.getProperties();
            configTree.addItem(props);
            configTree.setItemCaption(props, "properties:");
            configTree.setParent(props, source);
            for(Map.Entry propEn:props.entrySet()){
                String entryKey = props.hashCode() + propEn.getKey().toString();
                configTree.addItem(entryKey);
                configTree.setChildrenAllowed(entryKey, false);
                configTree.setItemCaption(entryKey, propEn.getKey() + "=" + propEn.getValue());
                configTree.setParent(entryKey, props);
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