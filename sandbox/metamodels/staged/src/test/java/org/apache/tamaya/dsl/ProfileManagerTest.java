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
package org.apache.tamaya.dsl;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by atsticks on 05.05.16.
 */
public class ProfileManagerTest {

    private ProfileManager profileManager = ProfileManager.getInstance();

    @org.junit.Test
    public void isProfileActive() throws Exception {
        assertTrue(profileManager.isProfileActive("DEV"));
        assertTrue(profileManager.isProfileActive("DEFAULT"));
        assertFalse(profileManager.isProfileActive("PROD"));
    }

    @org.junit.Test
    public void isProfileDefined() throws Exception {
        assertTrue(profileManager.isProfileDefined("DEV"));
        assertTrue(profileManager.isProfileDefined("DEFAULT"));
        assertFalse(profileManager.isProfileDefined("foo"));
    }

    @org.junit.Test
    public void isProfileDefault() throws Exception {
        assertFalse(profileManager.isProfileDefault("DEV"));
        assertTrue(profileManager.isProfileDefault("DEFAULT"));
    }

    @org.junit.Test
    public void getActiveProfiles() throws Exception {
        List<String> profiles = profileManager.getActiveProfiles();
        assertTrue(profiles.contains("DEV"));
        assertTrue(profiles.contains("DEFAULT"));
        assertFalse(profiles.contains("TEST"));
        assertFalse(profiles.contains("PROD"));
    }

    @org.junit.Test
    public void getDefaultProfiles() throws Exception {
        List<String> profiles = profileManager.getDefaultProfiles();
        assertTrue(profiles.contains("DEFAULT"));
        assertFalse(profiles.contains("TEST"));
    }

    @org.junit.Test
    public void getAllProfiles() throws Exception {
        Set<String> profiles = profileManager.getAllProfiles();
        assertTrue(profiles.contains("DEFAULT"));
        assertTrue(profiles.contains("DEV"));
        assertTrue(profiles.contains("TEST"));
        assertTrue(profiles.contains("PROD"));
        assertFalse(profiles.contains("foo"));
    }
}