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
package org.apache.tamaya;

import java.util.Collection;
import java.util.Map;

/**
 * Stage which should be supported by all layers.
 */
public interface Stage {
    /**
     * Get the stage's name. A stage's name is unique for a whole system.
     * @return the stage's name, never null.
     */
    public String getName();

    /**
     * Get all the stage's (unmodifiable) properties.
     * @return the stage's (unmodifiable) properties, never null.
     */
    public Map<String,String> getProperties();

    /**
     * Get the default stage for develpment.
     * @return the default stage, never null.
     */
    public static Stage development(){
        return Stages.getDevelopmentStage();
    }

    /**
     * Get the default stage for (component) testing.
     * @return the default stage, never null.
     */
    public static Stage test(){
        return Stages.getTestStage();
    }

    /**
     * Get the default stage for integration (testing).
     * @return the default stage, never null.
     */
    public static Stage integration(){
        return Stages.getIntegrationStage();
    }

    /**
     * Get the default stage for staging.
     * @return the default stage, never null.
     */
    public static Stage staging(){
        return Stages.getStagingStage();
    }

    /**
     * Get the default stage for production.
     * @return the default stage, never null.
     */
    public static Stage production(){
        return Stages.getProductionStage();
    }


    /**
     * Get a stage by name. If not present, create a new stage.
     *
     * @param name the stage's name.
     * @return tge stage instance, never null.
     */
    public static Stage of(String name){
        return Stages.getStage(name);
    }

    /**
     * Adds a new stage.
     *
     * @param stage the new stage instance.
     * @throws IllegalStateException if a stage with the same name is already existing.
     */
    public static void add(Stage stage){
        Stages.addStage(stage);
    }

    /**
     * Access all the stages currently defined.
     *
     * @return the current stages, never null.
     */
    public static Collection<Stage> getStages(){
        return Stages.getStages();
    }
}


