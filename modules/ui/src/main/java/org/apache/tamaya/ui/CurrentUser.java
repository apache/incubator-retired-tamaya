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

import com.vaadin.server.VaadinSession;

/**
 * Convenience wrapper for storing and retrieving a user from the VaadinSession
 */
public final class CurrentUser {
    /** The key used. */
    private static final String KEY = "currentUser";

    /**
     * Singleton constructor.
     */
    private CurrentUser(){}

    /**
     * Set the current users.
     * @param user the current user, not null.
     */
    public static void set(User user) {
        VaadinSession.getCurrent().setAttribute(KEY, user);
    }

    /**
     * Get the current user.
     * @return the current user, or null.
     */
    public static User get() {
        return (User) VaadinSession.getCurrent().getAttribute(KEY);
    }

    /**
     * Checks if the current user is present and logged in.
     * @return {@code true} if user is present and logged in.
     */
    public static boolean isLoggedIn() {
        return get() != null && get().isLoggedin();
    }
}
