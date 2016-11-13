/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.tamaya.examples.distributed;

import com.sun.deploy.uitoolkit.impl.fx.ui.FXAppContext;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by atsticks on 13.11.16.
 */
class ContentManagerPanel extends VBox{

    private ChoiceBox selector = new ChoiceBox();
    private TextField titleField = new TextField();
    private TextArea contentField = new TextArea();
    private TextField displayNameField = new TextField();
    private Button sendButton = new Button("Update Content");
    private Configuration config;
    private Vertx vertx;

    public ContentManagerPanel(Vertx vertx){
        this.vertx = vertx;
        displayNameField.setMinHeight(30.0);
        displayNameField.setMinWidth(200.0);
        displayNameField.setId("displayNameField");
        titleField.setMinHeight(30.0);
        titleField.setMinWidth(200.0);
        titleField.setId("title");
        titleField.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleField.setStyle("-fx-text-fill: #EFEFEF; -fx-background-color: black;");
        contentField.setId("scene");
        contentField.setFont(Font.font("Arial", FontWeight.LIGHT, 18));
        getChildren().addAll(selector, new Label("Title"), titleField, new Label("content"), contentField,
                new Label("Display Name"), displayNameField, new Separator(Orientation.VERTICAL), sendButton);
        sendButton.setOnAction(h -> {
            String selection = (String)selector.getSelectionModel().getSelectedItem();
            if(selection!=null){
                String uuid = selection.split("::")[1];
                DisplayContent content = new DisplayContent();
                content.content.put(Display.CONTENT_FIELD, contentField.getText());
                content.title = titleField.getText();
                content.displayId = uuid;
                content.displayName = displayNameField.getText();
                vertx.eventBus().publish(Display.DISPLAY_SHOW_TOPIC, Json.encode(content));
            }
        });
        selector.setOnAction(h -> {
            String selection = (String)selector.getSelectionModel().getSelectedItem();
            if(selection!=null) {
                displayNameField.setText(selection.split("::")[0]);
            }
        });
        updateList();
        vertx.periodicStream(5000).handler(h -> {
            updateList();
        });
    }

    public void updateList(){
        config = ConfigurationProvider.getConfiguration()
                .with(ConfigurationFunctions.section("displays.", true));
        // resulting config:
        // -----------------
        // UUID.displayName
        // UUID.content.title
        // UUID.content.content
        // UUID.timestamp
        final Set<String> keys = new TreeSet<>();
        for(Map.Entry<String,String> en:config.getProperties().entrySet()){
            if(en.getKey().endsWith(".displayName")){
                String uuid = en.getKey().substring(0,36);
                keys.add(en.getValue()+"::"+uuid);
            }
        }
        Platform.runLater(() -> {
            final Set<String> exKeys = new HashSet<String>(selector.getItems());
            for(Object item:exKeys){
                if(!keys.contains(item)){
                    selector.getItems().remove(item);
                }
            }
            for(String item:keys){
                if(!selector.getItems().contains(item)){
                    selector.getItems().add(item);
                }
            }
        });
    }
}
