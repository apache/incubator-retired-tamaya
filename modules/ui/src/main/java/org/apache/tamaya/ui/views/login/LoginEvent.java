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
package org.apache.tamaya.ui.views.login;

import org.apache.tamaya.ui.User;

import java.util.Objects;

/**
 * Event sent when a user has been authenticated.
 */
public class LoginEvent {
    /** The user, not null. */
    private User user;

    /**
     * Creates a new event.
     * @param user the user logged in, not null.
     */
    public LoginEvent(User user) {
        this.user = Objects.requireNonNull(user);
    }

    /**
     * Get the user logged in.
     * @return the user logged in, never null.
     */
    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "user=" + user +
                '}';
    }

}