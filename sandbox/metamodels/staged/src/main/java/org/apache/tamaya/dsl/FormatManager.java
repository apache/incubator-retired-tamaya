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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.resolver.Resolver;

import java.util.*;

/**
 * Component that manages the current supported formats:
 * <pre>
 * TAMAYA:
 *   FORMAT-DEF:
 *     - formats: yaml, properties, xml-properties
 * </pre>
 * Hereby:
 * <ul>
 *     <li><b>profiles</b> defines the available profiles, including implicit default profiles.</li>
 * </ul>
 */
public class FormatManager {

    private static final FormatManager INSTANCE = new FormatManager();

    /** The currently active profiles, in order of precedence, the most significant are the last ones. */
    private List<String> activeProfiles = new ArrayList<>();
    /** A set of all defined profiles. */
    private Set<String> profiles = new HashSet<>();
    /** The current used default profiles, loaded initially, before all other profiles are loaded. */
    private List<String> defaultProfiles = new ArrayList<>();

    /**
     * Get the current instance.
     * @return the current profile manager, never null.
     */
    public static FormatManager getInstance(){
        return INSTANCE;
    }

    private FormatManager(){
        Configuration metaConfig = EnvConfig.getMetaConfiguration();
        Configuration profileConfig = metaConfig.with(
                ConfigurationFunctions.section("TAMAYA.ENV-DEF"));
        String[] selectables = profileConfig.getOrDefault("profiles","DEFAULT,DEV,TEST,PROD").split(",");
        for(String sel:selectables){
            sel = sel.trim();
            if(sel.isEmpty()){
                continue;
            }
            this.profiles.add(sel);
        }
        String[] defaults = profileConfig.getOrDefault("defaults","DEFAULT").split(",");
        for(String def:defaults){
            def = def.trim();
            if(def.isEmpty()){
                continue;
            }
            if(!isProfileDefined(def)){
                throw new ConfigException("Invalid profile encountered: " +def + ", valid are: " + profiles);
            }
            this.defaultProfiles.add(def);
        }
        String[] expressions = profileConfig.getOrDefault("evaluation","${sys:ENV}, ${env:ENV}").split(",");
        String currentEnvironment = null;
        for(String exp:expressions){
            exp = exp.trim();
            if(exp.isEmpty()){
                continue;
            }
            currentEnvironment = Resolver.evaluateExpression(exp, false);
            if(currentEnvironment!=null){
                currentEnvironment = currentEnvironment.trim();
                if(!currentEnvironment.isEmpty()){
                    break;
                }
            }
        }
        if(currentEnvironment==null|| currentEnvironment.isEmpty()){
            currentEnvironment = profileConfig.getOrDefault("default-active", "DEV");
        }
        this.activeProfiles.addAll(defaultProfiles);
        String[] profilesActive = currentEnvironment.split(",");
        for(String prof:profilesActive){
            prof = prof.trim();
            if(prof.isEmpty()){
                continue;
            }
            if(!isProfileDefined(prof)){
                throw new ConfigException("Invalid profile encountered: " +prof + ", valid are: " + profiles);
            }
            this.activeProfiles.add(prof);
        }
    }

    /**
     * Allows to check if a profile is currently active.
     * @param profileName the profile name, not null.
     * @return true, if the profile is active.
     */
    public boolean isProfileActive(String profileName){
        return this.activeProfiles.contains(profileName);
    }

    /**
     * Allows to check if a profile is currently defined.
     * @param profileName the profile name, not null.
     * @return true, if the profile is defined.
     */
    public boolean isProfileDefined(String profileName){
        return this.profiles.contains(profileName);
    }

    /**
     * Allows to check if a profile is a default profile.
     * @param profileName the profile name, not null.
     * @return true, if the profile is a default profile.
     */
    public boolean isProfileDefault(String profileName){
        return this.defaultProfiles.contains(profileName);
    }

    /**
     * Get the list of currently active profiles.
     * @return the list of currently active profiles, in order of precedence, the most significant
     * are the last ones, never null.
     */
    public List<String> getActiveProfiles(){
        return Collections.unmodifiableList(activeProfiles);
    }

    /**
     * Get the list of currently active profiles.
     * @return the list of currently active profiles, in order of precedence, the most significant
     * are the last ones, never null.
     */
    public List<String> getDefaultProfiles(){
        return Collections.unmodifiableList(defaultProfiles);
    }

    /**
     * Get the list of currently active profiles.
     * @return the list of currently active profiles, in order of precedence, the most significant
     * are the last ones, never null.
     */
    public Set<String> getAllProfiles(){
        return Collections.unmodifiableSet(profiles);
    }

}
