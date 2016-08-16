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
package org.apache.tamaya.ui.event;


import java.util.Objects;

/**
 * Event sent when the user wants to navigate.
 */
public class NavigationEvent {
    /** The target view. */
    private String viewName;

    /**
     * Constructor.
     * @param viewName the target view, not null.
     */
    public NavigationEvent(String viewName) {
        this.viewName = Objects.requireNonNull(viewName);
    }

    /**
     * Access the target view name.
     * @return the target view name, never null.
     */
    public String getViewName() {
        return viewName;
    }

    @Override
    public String toString() {
        return "NavigationEvent{" +
                "viewName='" + viewName + '\'' +
                '}';
    }
}
