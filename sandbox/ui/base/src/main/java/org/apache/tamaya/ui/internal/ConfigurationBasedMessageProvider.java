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
package org.apache.tamaya.ui.internal;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spisupport.DefaultConfiguration;
import org.apache.tamaya.ui.services.MessageProvider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Component resolving messages for being shown in the UI, based on the ResourceBundle mechanisms.
 * The baseName used can optionally be configured by setting {@code tamaya.ui.baseName} either as system property,
 * environment property or Tamaya configuration. Be aware that the JDK resource bundle mechanism only reads
 * the first property file on the classpath (or a corresponding class file implementing ResourceBundle).
 */
public final class ConfigurationBasedMessageProvider implements MessageProvider{

    /**
     * The property name for configuring the resource bundle's base name either as
     * system property, environment property or configuration entry.
     */
    private static final String TAMAYA_UI_BASE_NAME = "tamaya.ui.baseName";

    private final String baseName = evaluateBaseName();

    private Map<String, Map<String,String>> propertiesCache = new ConcurrentHashMap<>();


    /**
     * Private singleton constructor.
     */
    public ConfigurationBasedMessageProvider(){

    }

    /**
     * Get a message using the defaul locale.
     * @param key the message key, not null.
     * @return the resolved message, or the bundle ID, never null.
     */
    public String getMessage(String key){
        return getMessage(key, Locale.getDefault());
    }

    /**
     * Get a message.
     * @param key the message key, not null.
     * @param locale the target locale, or null, for the default locale.
     * @return the resolved message, or the key, never null.
     */
    public String getMessage(String key, Locale locale){
        List<String> bundleIds = evaluateBundleIds(locale);
        for(String bundleID:bundleIds){
            Map<String,String> entries = this.propertiesCache.get(bundleID);
            if(entries==null){
                entries = loadEntries(bundleID);
            }
            String value = entries.get(key);
            if(value!=null){
                return value;
            }
        }
        return key;
    }

    private Map<String, String> loadEntries(String bundleID) {
        ConfigurationContextBuilder ctxBuilder = ConfigurationProvider.getConfigurationContextBuilder();
        for(String format:new String[]{"xml", "properties"}) {
            try {
                Enumeration<URL> urls = getClass().getClassLoader().getResources(bundleID+"."+format);
                while(urls.hasMoreElements()){
                    URL url = urls.nextElement();
                    ctxBuilder.addPropertySources(new URLPropertySource(url));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Map<String, String>  entries = new DefaultConfiguration(ctxBuilder.build()).getProperties();
        this.propertiesCache.put(bundleID, entries);
        return entries;
    }

    private List<String> evaluateBundleIds(Locale locale) {
        List<String> bundleIds = new ArrayList<>();
        String country = locale.getCountry();
        if(country==null){
            country="";
        }
        String lang = locale.getLanguage();
        if(lang==null){
            lang="";
        }
        String variant = locale.getVariant();
        if(variant==null){
            variant="";
        }
        String key = baseName + "_"+country+"_"+lang+"_"+variant;
        key = reduceKey(key);
        if(!bundleIds.contains(key)){
            bundleIds.add(key);
        }
        key = baseName + "_"+country+"_"+lang;
        key = reduceKey(key);
        if(!bundleIds.contains(key)){
            bundleIds.add(key);
        }
        key = baseName + "_"+country;
        key = reduceKey(key);
        if(!bundleIds.contains(key)){
            bundleIds.add(key);
        }
        key = baseName;
        if(!bundleIds.contains(key)){
            bundleIds.add(key);
        }
        return bundleIds;
    }

    /**
     * Remove all doubled '_' hereby normalizing the bundle key.
     * @param key the key, not null.
     * @return the normaliuzed key, not null.
     */
    private String reduceKey(String key) {
        String reduced = key.replace("___","_").replace("__","_");
        if(reduced.endsWith("_")){
            reduced = reduced.substring(0,reduced.length()-1);
        }
        return reduced;
    }

    /**
     * Evaluates the base name to be used for creating the resource bundle used.
     * @return base name
     */
    private String evaluateBaseName() {
        String baseName = System.getProperty(TAMAYA_UI_BASE_NAME);
        if(baseName==null || baseName.isEmpty()){
            baseName = System.getenv("tamaya.ui.baseName");
        }
        if(baseName==null || baseName.isEmpty()){
            baseName = ConfigurationProvider.getConfiguration().get("tamaya.ui.baseName");
        }
        if(baseName==null || baseName.isEmpty()){
            baseName = "ui/lang/tamaya";
        }
        return baseName.replace('.', '/');
    }

}
