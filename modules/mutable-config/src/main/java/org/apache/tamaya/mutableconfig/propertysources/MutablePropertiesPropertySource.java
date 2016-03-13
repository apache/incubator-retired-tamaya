/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.mutableconfig.propertysources;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spi.PropertyValueBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple implementation of a mutable {@link org.apache.tamaya.spi.PropertySource} for .properties files.
 */
public class MutablePropertiesPropertySource extends AbstractMutablePropertySource {

    /**
     * The logger.
     */
    private static final Logger LOG = Logger.getLogger(MutablePropertiesPropertySource.class.getName());
    /**
     * Default update interval is 1 minute.
     */
    private static final long DEFAULT_UPDATE_INTERVAL = 60000L;

    /**
     * The property source name.
     */
    private String name;

    /**
     * The configuration resource's URL.
     */
    private File file;

    /**
     * Timestamp of last read.
     */
    private long lastRead;

    /**
     * Interval, when the resource should try to update its contents.
     */
    private long updateInterval = DEFAULT_UPDATE_INTERVAL;
    /**
     * The current properties.
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * Creates a new Properties based PropertySource based on the given URL.
     *
     * @param propertiesLocation the URL encoded location, not null.
     */
    public MutablePropertiesPropertySource(File propertiesLocation, int defaultOrdinal) {
        super(defaultOrdinal);
        this.name = propertiesLocation.toString();
        try {
            this.file = propertiesLocation;
            load();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Cannot convert file to URL: " + propertiesLocation, e);
        }
    }



    @Override
    public PropertyValue get(String key) {
        Map<String,String> properties = getProperties();
        String val = properties.get(key);
        if(val==null){
            return null;
        }
        PropertyValueBuilder b = new PropertyValueBuilder(key, val, getName());
        String metaKeyStart = "_" + key + ".";
        for(Map.Entry<String,String> en:properties.entrySet()) {
            if(en.getKey().startsWith(metaKeyStart)){
                b.addContextData(en.getKey().substring(metaKeyStart.length()), en.getValue());
            }
        }
        return b.build();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getProperties() {
        checkLoad();
        return Collections.unmodifiableMap(this.properties);
    }


    private void checkLoad() {
        if(file!=null && (lastRead+updateInterval)<System.currentTimeMillis()){
            load();
        }
    }

    /**
     * loads the Properties from the given URL
     *
     * @return loaded {@link Properties}
     * @throws IllegalStateException in case of an error while reading properties-file
     */
    private void load() {
        try (InputStream stream = new FileInputStream(file)) {
            Map<String, String> properties = new HashMap<>();
            Properties props = new Properties();
            if (stream != null) {
                props.load(stream);
            }
            for (String key : props.stringPropertyNames()) {
                properties.put(key, props.getProperty(key));
            }
            this.lastRead = System.currentTimeMillis();
            LOG.log(Level.FINEST, "Loaded properties from " + file);
            this.properties = properties;
        } catch (IOException e) {
            LOG.log(Level.FINEST, "Cannot load properties from " + file, e);
        }
    }

    @Override
    protected void commitInternal(TransactionContext context) {
        if(context.isEmpty()){
            LOG.info("Nothing to commit for transaction: " + context.getTransactionID());
            return;
        }
        if(!file.exists()){
            try {
                if(!file.createNewFile()){
                    throw new ConfigException("Failed to create config file " + file);
                }
            } catch (IOException e) {
                throw new ConfigException("Failed to create config file " + file, e);
            }
        }
        for(Map.Entry<String,String> en:context.getAddedProperties().entrySet()){
            int index = en.getKey().indexOf('?');
            if(index>0){
                this.properties.put(en.getKey().substring(0, index), en.getValue());
            }else{
                this.properties.put(en.getKey(), en.getValue());
            }
        }
        for(String rmKey:context.getRemovedProperties()){
            this.properties.remove(rmKey);
        }
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))){
            Properties props = new Properties();
            for (Map.Entry<String,String> en : this.properties.entrySet()) {
                props.setProperty(en.getKey(), en.getValue());
            }
            props.store(bos, "Properties written from Tamaya on " + new Date());
            bos.flush();
        }
        catch(Exception e){
            throw new ConfigException("Failed to write config to " + file, e);
        }
    }


}
