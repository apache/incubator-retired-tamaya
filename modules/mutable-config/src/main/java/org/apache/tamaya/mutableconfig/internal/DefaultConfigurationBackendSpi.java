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

import org.apache.tamaya.mutableconfig.spi.MutableConfigurationBackendSpi;
import org.apache.tamaya.mutableconfig.spi.MutableConfigurationBackendProviderSpi;

import java.io.File;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mutable Config Request factory that tries to convert given URIs to file references, if successful, it returns
 * ConfigChangeRequests fir .properties and .xml files.
 */
public class DefaultConfigurationBackendSpi implements MutableConfigurationBackendProviderSpi {

    private static final Logger LOG = Logger.getLogger(DefaultConfigurationBackendSpi.class.getName());

    @Override
    public MutableConfigurationBackendSpi getBackend(URI uri) {
        try{
            File f = new File(uri);
            if(f.getName().endsWith(".properties")){
                return new PropertiesFileConfigBackendSpi(f);
            }else if(f.getName().endsWith(".xml")){
                return new XmlPropertiesFileConfigBackendSpi(f);
            }
        } catch(Exception e){
            LOG.log(Level.FINEST, "URI not convertible to file, ignoring " + uri, e);
        }
        return null;
    }

}
