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
package org.apache.tamaya.integration.osgi.base;

import org.apache.felix.cm.PersistenceManager;
import org.apache.felix.cm.file.FilePersistenceManager;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the Felix {@link PersistenceManager}, which is used as a
 * configuration backend for reading and writing configuration.
 */
class TamayaPersistenceManager implements PersistenceManager{

    private FilePersistenceManager fallBackPM;
    private BundleContext context;
    private Map<String, Dictionary<String,String>> configs = new ConcurrentHashMap<>();

    TamayaPersistenceManager(BundleContext bundleContext){
        this.context = bundleContext;
        fallBackPM = new FilePersistenceManager(context, bundleContext.getProperty("felix.cm.dir"));
    }

    @Override
    public boolean exists(String pid) {
        if(fallBackPM!=null && fallBackPM.exists(pid)){
            return true;
        }
        return !ConfigurationProvider.getConfiguration().with(
                ConfigurationFunctions.section(
                        "__"+pid+"__"
                )).getProperties().isEmpty();
    }

    @Override
    public Dictionary load(String pid) throws IOException {
        Dictionary dict = new Hashtable();
        if(fallBackPM!=null){
            try{
                Dictionary loaded = fallBackPM.load(pid);
                Enumeration<String> en = loaded.keys();
                while(en.hasMoreElements()){
                    String key = en.nextElement();
                    dict.put(key, loaded.get(key));
                }
            } catch(IOException e){
                if(!exists(pid)){
                    throw new IOException("No such Config (neither in Tamaya, nor on the FW): " + pid, e);
                }
            }
        }
        Map<String,String> tamayaLoad = ConfigurationProvider.getConfiguration().with(
                ConfigurationFunctions.section(
                        "__"+pid+"__"
                )).getProperties();
        for(Map.Entry<String,String> en:tamayaLoad.entrySet()){
            // TODO Make configurable, if Tamaya is overriding, or extending...
            dict.put(en.getKey(), en.getValue());
        }
        configs.put(pid, dict);
        return dict;
    }

    @Override
    public Enumeration getDictionaries() throws IOException {
        List<Dictionary> dicts = new ArrayList<>();
        if(fallBackPM != null){
            Enumeration en = fallBackPM.getDictionaries();
            while(en.hasMoreElements()){
                dicts.add((Dictionary)en.nextElement());
            }
        }
        dicts.addAll(configs.values());
        return Collections.enumeration(dicts);
    }

    @Override
    public void store(String pid, Dictionary dictionary) throws IOException {
        // TODO Define a configurable storage strategy
        // 1: to file storage
        // 2: to Tamaya
        // 3: both
        // 4: read only
        if(fallBackPM != null){
            fallBackPM.store(pid, dictionary);
        }
    }

    @Override
    public void delete(String pid) throws IOException {
        // TODO Define a configurable storage strategy
        // 1: to file storage
        // 2: to Tamaya
        // 3: both
        // 4: read only
        if(fallBackPM != null){
            fallBackPM.delete(pid);
        }
    }
}
