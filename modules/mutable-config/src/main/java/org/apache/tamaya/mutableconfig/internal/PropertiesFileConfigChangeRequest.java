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
package org.apache.tamaya.mutableconfig.internal;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.mutableconfig.spi.AbstractConfigChangeRequest;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Change Request implementation based on .properties file.
 */
class PropertiesFileConfigChangeRequest extends AbstractConfigChangeRequest{

    private static final Logger LOG = Logger.getLogger(PropertiesFileConfigChangeRequest.class.getName());

    private File file;

    private Properties properties = new Properties();

    PropertiesFileConfigChangeRequest(File file){
        super(file.toURI());
        this.file = file;
        if(file.exists()) {
            try (InputStream is = getBackendURI().toURL().openStream()) {
                properties.load(is);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Failed to load properties from " + file, e);
            }
        }
    }

    @Override
    public boolean exists(String keyExpression) {
        if(properties.containsKey(keyExpression)){
            return true;
        }
        for(Object key:properties.keySet()){
            if(key.toString().matches(keyExpression)){
                return true;
            }
        }
        return false;
    }


    @Override
    protected void commitInternal() {
        if(!file.exists()){
            try {
                if(!file.createNewFile()){
                    throw new ConfigException("Failed to create config file " + file);
                }
            } catch (IOException e) {
                throw new ConfigException("Failed to create config file " + file, e);
            }
        }
        for(Map.Entry<String,String> en:super.properties.entrySet()){
            this.properties.put(en.getKey(), en.getValue());
        }
        for(String rmKey:super.removed){
            this.properties.remove(rmKey);
        }
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))){
            properties.store(bos, "Properties written from Tamaya, request: " + getRequestID());
            bos.flush();
        }
        catch(Exception e){
            throw new ConfigException("Failed to write config to " + file, e);
        }
    }

    @Override
    public String toString() {
        return "PropertiesFileConfigChangeRequest{" +
                "file=" + file +
                '}';
    }
}
