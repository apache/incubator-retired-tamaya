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
package org.apache.tamaya;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static access to the {@link Configuration} for the very application.
 */
public abstract class ConfigurationProvider {

    private static final Logger LOG = Logger.getLogger("ConfigurationProvider");

    private static final Configuration INSTANCE = loadConfiguration();


    private static Configuration loadConfiguration() {
        for(Configuration config: ServiceLoader.load(Configuration.class)){
            // Sort and use priority, or delegate?
            return config;
        }
        String configClass = System.getProperty("configuration");
        if(configClass!=null){
            try{
                return (Configuration)Class.forName(configClass).newInstance();
            }catch(Exception e){
                throw new ConfigException("Error loading configuration...", e);
            }finally{
                showBanner();
            }
        }
        throw new ConfigException("No configuration available.");
    }

    private static void showBanner(){
        BufferedReader reader = null;
        try{
            URL url = ConfigurationProvider.class.getResource("/tamaya-banner.txt");
            if(url!=null){
                reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
                StringBuilder b = new StringBuilder();
                String line = reader.readLine();
                while(line != null){
                    b.append(line).append('\n');
                    line = reader.readLine();
                }
                System.out.println(b.toString());
            }
        }
        catch(Exception e){
            System.out.println("************ TAMAYA CONFIG ************");
        }
        finally{
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected ConfigurationProvider() {
        // just to prevent initialisation
    }

    /**
     * Access the current configuration.
     * @return the configuration.
     */
    public static Configuration getConfiguration(){
        return INSTANCE;
    }


}
