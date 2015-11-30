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
package org.apache.tamaya.model.spi;

import org.apache.tamaya.model.ConfigModel;

import java.util.Collection;

/**
 * Model of a configuration state. A model can be a full model, or a partial model, validating only
 * a configuration subset. This allows better user feedback because big configurations can be grouped
 * and validated by multiple (partial) models.
 */
public interface ModelProviderSpi {

    /**
     * Get the validation defined.
     *
     * @return the sections defined, never null.
     */
    Collection<ConfigModel> getConfigModels();

}
