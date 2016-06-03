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
package org.apache.tamaya.ui;

import com.vaadin.navigator.View;

/**
 * Interface to register Tamaya UI parts. For priorization also use the @Priority annotations.
 */
public interface ViewProvider {

    /**
     * View lifecycle options that determine when a view is created and how long an instance is used.
     */
    enum ViewLifecycle {
        /** Creates a new view instance whenever the view is showed. */
        CREATE,
        /** Loads the view on first access. */
        LAZY,
        /** Eagerly preloads the view. */
        EAGER
    }

    /**
     * Get the view lifecycle model.
     * @return the lifecycle model, not null.
     */
    ViewLifecycle getLifecycle();

    /**
     * Get the view's name, used for resolving the view display name.
     * @return the view's name.
     */
    String getName();

    /**
     * Get the url pattern where this view should be accessible.
     * @return the url pattern, not null.
     */
    String getUrlPattern();

    /**
     * Get the name to be displayed for this view. This value will also be used to lookup a name from the {@code /ui/lang/tamaya}
     *                                   bundle. If not found the value returned will be used for display.
     *
     * @return the name to be displayed, or its resource bundle key, not null.
     */
    String getDisplayName();

    /**
     * Method that is called to create a new view instance.
     * @see #getLifecycle()
     * @param params any parameters that may be needed to create the view.
     * @return a new view instance, not null.
     */
    View createView(Object... params);
}