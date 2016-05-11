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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.format.ConfigurationFormat;
import org.apache.tamaya.format.ConfigurationFormats;
import org.apache.tamaya.functions.ConfigurationFunctions;

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
public final class DSLFormatManager {

    private static final DSLFormatManager INSTANCE = new DSLFormatManager();

    /** The currently active formats, in order of precedence, the most significant are the last ones. */
    private List<ConfigurationFormat> formats = new ArrayList<>();
    /** The currently active suffixes, in order of precedence, the most significant are the last ones. */
    private List<String> suffixes = new ArrayList<>();


    /**
     * Get the current instance.
     * @return the current profile manager, never null.
     */
    public static DSLFormatManager getInstance(){
        return INSTANCE;
    }

    private DSLFormatManager(){
        Configuration metaConfig = MetaConfiguration.getConfiguration();
        Configuration formatsConfig = metaConfig.with(
                ConfigurationFunctions.section("TAMAYA.FORMATS"));
        String[] formats = formatsConfig.getOrDefault("formats","yamk,properties,ini").split(",");
        this.formats.addAll(ConfigurationFormats.getFormats(formats));
        String[] suffixes = formatsConfig.getOrDefault("suffixes","yml,properties,ini").split(",");
        for(String sfx:suffixes){
            sfx = sfx.trim();
            if(sfx.isEmpty()){
                continue;
            }
            this.suffixes.add(sfx);
        }
    }


    /**
     * Allows to check if a suffix is currently activated.
     * @param suffix the suffix name, not null.
     * @return true, if the profile is defined.
     */
    public boolean isSuffixDefined(String suffix){
        return this.suffixes.contains(suffix);
    }

    /**
     * Allows to check if a format is selected.
     * @param formatName the format name, not null.
     * @return true, if the format is a selected.
     */
    public boolean isFormatSelected(String formatName){
        for(ConfigurationFormat format:formats){
            if(format.getName().equals(formatName)){
                return true;
            }
        }
        return false;
    }

    /**
     * Get the list of currently active suffixes.
     * @return the list of currently active suffixes, never null.
     */
    public List<String> getSuffixes(){
        return Collections.unmodifiableList(suffixes);
    }

    /**
     * Get the list of currently active formats.
     * @return the list of currently active formats, never null.
     */
    public List<ConfigurationFormat> getFormats(){
        return Collections.unmodifiableList(formats);
    }


}
