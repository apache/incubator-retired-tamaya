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
package org.apache.tamaya.spi;

import org.apache.tamaya.Stage;

import java.util.Collection;

/**
 * Spi to be registered using the Bootstrap mechanism for backing up the {@link org.apache.tamaya.Stages} singleton.
 * This SPI is loaded on boot time.
 */
public interface StagesSingletonSpi {

    /**
     * Get the default stage for develpment.
     * @return the default stage, never null.
     */
    Stage getDevelopmentStage();

    /**
     * Get the default stage for (component) testing.
     * @return the default stage, never null.
     */
    Stage getTestStage();

    /**
     * Get the default stage for integration (testing).
     * @return the default stage, never null.
     */
    Stage getIntegrationStage();

    /**
     * Get the default stage for staging.
     * @return the default stage, never null.
     */
    Stage getStagingStage();

    /**
     * Get the default stage for production.
     * @return the default stage, never null.
     */
    Stage getProductionStage();

    /**
     * Get a stage by name. If not present, create a new stage.
     *
     * @param name the stage's name.
     * @return tge stage instance, never null.
     */
    Stage getStage(String name);

    /**
     * Adds a new stage.
     *
     * @param stage the new stage instance.
     * @throws IllegalStateException if a stage with the same name is already existing.
     */
    void addStage(Stage stage);

    /**
     * Access all the stages currently defined.
     *
     * @return the current stages, never null.
     */
    Collection<Stage> getStages();

}
