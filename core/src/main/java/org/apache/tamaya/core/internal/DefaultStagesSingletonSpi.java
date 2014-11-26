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
package org.apache.tamaya.core.internal;

import org.apache.tamaya.Stage;
import org.apache.tamaya.spi.StagesSingletonSpi;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anatole on 12.11.2014.
 */
public class DefaultStagesSingletonSpi implements StagesSingletonSpi{


    public DefaultStagesSingletonSpi(){
        addStage(StageBuilder.create("Development").build());
        addStage(StageBuilder.create("Test").build());
        addStage(StageBuilder.create("Integration").build());
        addStage(StageBuilder.create("Staging").build());
        addStage(StageBuilder.create("Production").build());
    }

    /**
     * All the stages known.
     */
    private static final Map<String, Stage> stages = new ConcurrentHashMap<>();

    @Override
    public Stage getDevelopmentStage() {
        return getStage("Development");
    }

    @Override
    public Stage getTestStage() {
        return getStage("Test");
    }

    @Override
    public Stage getIntegrationStage() {
        return getStage("Integration");
    }

    @Override
    public Stage getStagingStage() {
        return getStage("Staging");
    }

    @Override
    public Stage getProductionStage() {
        return getStage("Production");
    }

    /**
     * Get a stage by name. If not present, create a new stage.
     *
     * @param name the stage's name.
     * @return tge stage instance, never null.
     */
    public Stage getStage(String name) {
        Stage stage = stages.get(name);
        if (stage == null) {
            throw new IllegalArgumentException("No such state: " + name);
        }
        return stage;
    }

    /**
     * Adds a new stage.
     *
     * @param stage the new stage instance.
     * @throws IllegalStateException if a stage with the same name is already existing.
     */
    public void addStage(Stage stage) {
        Stage existing = stages.putIfAbsent(stage.getName(), stage);
        if (existing != null) {
            throw new IllegalStateException("A stage named '" + stage.getName() + "' already exists: " + existing);
        }
    }

    /**
     * Access all the stages currently defined.
     *
     * @return the current stages, never null.
     */
    public Collection<Stage> getStages() {
        return stages.values();
    }

}