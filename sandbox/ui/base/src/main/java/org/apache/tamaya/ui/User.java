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


import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A simple User object.
 */
public class User {
    /** The user ID. */
    private final String userID;
    /** The full name. */
    private String fulLName;
    /** The credentials. */
    private String credentials;
    /** The user's roles. */
    private Set<String> roles = new HashSet<>();
    /** The user's last login date. */
    private Date loginDate = new Date();
    /** The user's last logout date. */
    private Date logoutDate = new Date();

    /**
     * Constructor.
     * @param uid the user ID, not null.
     * @param fullName the full name.
     * @param credentials the credentials.
     * @param roles its roles.
     */
    public User(String uid, String fullName, String credentials, String... roles){
        this.userID = Objects.requireNonNull(uid);
        this.fulLName = fullName!=null?fullName:userID;
        if(fullName==null){
            this.fulLName = userID;
        }
        this.roles.addAll(Arrays.asList(roles));
        this.credentials = credentials;
    }

    /**
     * èPerforms a login, checking the credentials.
     * @param credentials the credentials.
     * @return true, if the user could be logged in.
     */
    public boolean login(String credentials){
        if(this.credentials!=null){
            this.loginDate = new Date();
            return this.credentials.equals(credentials);
        }
        return credentials==null || credentials.isEmpty();
    }

    /**
     * Checks if the user is currently logged in.
     * @return true, if the user is currently logged in.
     */
    public boolean isLoggedin(){
        long now = System.currentTimeMillis();
        if(this.logoutDate!=null && this.logoutDate.getTime() < now){
            return false;
        }
        return this.loginDate!=null && this.loginDate.getTime() < now;
    }

    /**
     * Logs the user out.
     */
    public void logout(){
        this.logoutDate = new Date();
    }

    /**
     * Get the user ID.
     * @return the user ID, never null.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Get the full name.
     * @return the full name, never null.
     */
    public String getFullName() {
        return fulLName;
    }

    /**
     * Checks if the user has the given role.
     * @param role the role to be checked, not null.
     * @return true, if the user has the required role.
     */
    public boolean hasRole(String role){
        return this.roles.contains(role);
    }

    /**
     * Get the user's roles.
     * @return the roles, never null.
     */
    public Set<String> getRoles(){
        return Collections.unmodifiableSet(roles);
    }

    /**
     * Get the last login timestamp.
     * @return the last login date, or null.
     */
    public Date getLoginDate(){
        return loginDate;
    }

    /**
     * Get the last login timestamp.
     * @return the last login date, or null.
     */
    public Date getLogoutDate(){
        return logoutDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "fulLName='" + fulLName + '\'' +
                ", userID='" + userID + '\'' +
                ", roles=" + roles +
                ", loginDate=" + loginDate +
                ", logoutDate=" + logoutDate +
                '}';
    }
}
