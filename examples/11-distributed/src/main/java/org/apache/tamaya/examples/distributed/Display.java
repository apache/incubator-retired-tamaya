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

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.Json;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.core.propertysource.EnvironmentPropertySource;
import org.apache.tamaya.core.propertysource.SystemPropertySource;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.hazelcast.HazelcastPropertySource;
import org.apache.tamaya.inject.ConfigurationInjection;
import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.spi.*;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Created by atsticks on 12.11.16.
 */
public class Display extends Application{

    private static final Logger LOG = Logger.getLogger(Display.class.getSimpleName());

    public static final String DISPLAY_SHOW_TOPIC = "Display::show";
    public static final String DISPLAY_REGISTER_TOPIC = "Display::register";
    public static final String CONTENT_FIELD = "content";

    @Config(defaultValue="UNKNOWN DISPLAY")
    private String displayName;

    private Scene scene;

    private Group root = new Group();

    private Stage stage;

    private TextField titleField = new TextField("title");

    private TextField configFilterField = new TextField("");

    private TextArea contentField = new TextArea("scene");

    private TextArea monitorField = new TextArea("monitor");

    private DisplayContent displayContent = new DisplayContent();

    private DisplayRegistration registration;

    private StringBuffer monitorBuffer = new StringBuffer();

    private Vertx vertx;

    private static HazelcastPropertySource hazelCastPropertySource;

    public Display(){
        LOG.info("\n-----------------------------------\n" +
                "Starting Display...\n" +
                "-----------------------------------");
        LOG.info("--- Starting Vertx cluster...");
        // Reusing the hazelcast instance already in place for vertx...
        ClusterManager mgr = new HazelcastClusterManager(
                hazelCastPropertySource.getHazelcastInstance());
        VertxOptions vertxOptions = new VertxOptions().setClusterManager(mgr);
        Vertx.clusteredVertx(vertxOptions, h -> {
            vertx = h.result();
        });
        LOG.info("--- Waiting for Vertx cluster...");
        while(vertx==null){
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        titleField.getStyleClass().add("title");
        contentField.getStyleClass().add("content");
        monitorField.getStyleClass().add("monitor");
        titleField.setId("title");
        titleField.setEditable(false);
        contentField.setId("scene");
        contentField.setEditable(false);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        LOG.info("--- Configuring application...");
        ConfigurationInjection.getConfigurationInjector()
                .configure(this);
        LOG.info("--- Registering display...");
        registerDisplay();
        LOG.info("--- Starting stage...");
        initStage(stage);
        registerListeners();
        LOG.info("--- Showing stage...");
        stage.show();
        LOG.info("\n---------------\n" +
                 "Display started\n" +
                 "---------------");
    }

    private void registerDisplay() {
        registration = new DisplayRegistration(displayName);
        logToMonitor("Display started at " + LocalDateTime.now() +
                "\n  id   = " + registration.getId() +
                "\n  name = " + registration.getDisplayName());
        // Register in the shared map every 10 seconds, with a TTL of 20 seconds...
        vertx.eventBus().publish(DISPLAY_REGISTER_TOPIC, Json.encode(registration));
        vertx.periodicStream(10000).handler(time -> {
            registration = registration.update();
            vertx.eventBus().publish(DISPLAY_REGISTER_TOPIC, Json.encode(registration));
            vertx.sharedData().getClusterWideMap("displays", h -> {
                h.result().put(registration.getId(), registration, 20000L, null);
            });
        });
    }

    private void registerListeners() {
        // registering update hook
        vertx.eventBus().consumer(DISPLAY_SHOW_TOPIC, h -> {
            DisplayContent content = Json.decodeValue((String)h.body(), DisplayContent.class);
            logToMonitor("NEW CONTENT: " + content.toString());
            if(registration.getId().equals(content.displayId)) {
                logToMonitor("Applying content: " + content + "...");
                titleField.setText(content.title);
                contentField.setText(content.content.get(CONTENT_FIELD));
                if(content.displayName!=null) {
                    this.registration.setDisplayName(
                            content.displayName
                    );
                    Platform.runLater(() -> {
                        this.stage.setTitle(content.displayName);
                    });
                }
                logToMonitor("SUCCESS.");
            }
        });
        vertx.eventBus().consumer(DISPLAY_REGISTER_TOPIC, h -> {
            DisplayRegistration registration = Json.decodeValue((String)h.body(), DisplayRegistration.class);
            logToMonitor("NEW DISPLAY: " + registration.toString());
        });
    }

    private void initStage(Stage stage) {
        stage.setTitle(registration.getDisplayName());
        scene = new Scene(root, Color.WHITE);
        scene.getStylesheets().add("/stylesheet.css");

        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("main-layout");
        layout.setPrefSize(600, 400);
//        layout.setTop(createWinTitle());

        Node displayPanel = createDisplayNode();
        Node monitorPanel = createMonitorNode();

        TabPane tabPane = new TabPane();
        tabPane.getStylesheets().add("main-tabs");
        Tab tab0 = new Tab("Display", displayPanel);
        tab0.setClosable(false);
        Tab tab1 = new Tab("Monitor", monitorPanel);
        tab1.setClosable(false);
        tabPane.getTabs().add(0, tab0);
        tabPane.getTabs().add(1, tab1);
        layout.setCenter(tabPane);
        layout.setBottom(createStatusPane());
        scene.setRoot(layout);
        stage.setScene(scene);
    }

    private Node createStatusPane() {
        return new Label();
    }

    private Node createMonitorNode() {
        VBox vbox = new VBox();
        ScrollPane monitorPane = new ScrollPane(monitorField);
        monitorPane.setFitToHeight(true);
        monitorPane.setFitToWidth(true);
        monitorField.setPrefSize(2000,2000);
        vbox.getChildren().addAll(monitorPane);
        return vbox;
    }

    private Node createDisplayNode() {
        VBox vbox = new VBox();
        ScrollPane contentPane = new ScrollPane(contentField);
        contentPane.setFitToHeight(true);
        contentPane.setFitToWidth(true);
        titleField.setText("- Nothing to show -");
        contentField.setText("- Nothing to show -");
        vbox.getChildren().addAll(titleField, contentPane, createButtonPane());
        return vbox;
    }

    private Pane createButtonPane() {
        HBox buttonLayout = new HBox();
        buttonLayout.getStyleClass().add("button-pane");
        Button showConfig = new Button("Show Config");
        showConfig.setId("showConfig-button");
        showConfig.onActionProperty().set(h -> {
            if("Hide Config".equals(showConfig.getText())){
                monitorField.setText(monitorBuffer.toString());
                showConfig.setText("Show Config");
            }else {
                showConfig();
                showConfig.setText("Hide Config");
            }
        });
        configFilterField.onActionProperty().set(h -> {
            showConfig();
        });
        configFilterField.setId("configFilter-field");
        buttonLayout.getChildren().addAll(showConfig, configFilterField);
        return buttonLayout;
    }

    private void showConfig() {
        String filter = configFilterField.getText();
        String configAsText = null;
        if(filter!=null && !filter.trim().isEmpty()){
            configAsText = ConfigurationProvider.getConfiguration()
                    .with(ConfigurationFunctions.section(filter))
                    .query(ConfigurationFunctions.textInfo());
        }else{
            configAsText = ConfigurationProvider.getConfiguration()
                    .query(ConfigurationFunctions.textInfo());
        }
        monitorField.setText(configAsText);
    }

    public static void main(String[] args) {
        // Programmatically setup our configuration
        hazelCastPropertySource = new HazelcastPropertySource();
        ConfigurationContext ctx = ConfigurationProvider.getConfigurationContextBuilder()
                .addPropertySources(
                        new EnvironmentPropertySource(),
                        new SystemPropertySource(),
                        hazelCastPropertySource
                        )
                .addDefaultPropertyConverters()
                .build();
        ConfigurationProvider.setConfiguration(
                ConfigurationProvider.createConfiguration(ctx));
        // Launch the app
        Application.launch(Display.class);
    }


    public void logToMonitor(String message){
        if(!message.endsWith("\n")){
            monitorBuffer.append(message + '\n');
        }else{
            monitorBuffer.append(message);
        }
        synchronized (monitorField) {
            monitorField.setText(monitorBuffer.toString());
        }
    }
}
