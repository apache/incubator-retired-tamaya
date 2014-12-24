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
package org.apache.tamaya.core.internal.format;

import org.apache.tamaya.core.resource.Resource;
import org.apache.tamaya.core.config.ConfigurationFormat;
import org.apache.tamaya.core.spi.ConfigurationFormatSpi;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.tamaya.spi.ServiceContext;

/**
 * Singleton accessor to access registered reader mechanism.
 */
public final class DefaultConfigurationFormatSpi implements ConfigurationFormatSpi {

    public ConfigurationFormat getFormat(String formatName){
        Objects.requireNonNull(formatName);
        try {
            for (ConfigurationFormat configFormat : ServiceContext.getInstance().getServices(ConfigurationFormat.class)) {
                if(formatName.equals(configFormat.getFormatName())){
                    return configFormat;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }

    public Collection<String> getFormatNames(){
        Set<String> result = new HashSet<>();
        try {
            result.addAll(ServiceContext.getInstance().getServices(ConfigurationFormat.class).stream().map(ConfigurationFormat::getFormatName)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return result;
    }

	public ConfigurationFormat getFormat(Resource resource) {
        Objects.requireNonNull(resource);
        try {
            for (ConfigurationFormat configFormat : ServiceContext.getInstance().getServices(ConfigurationFormat.class)) {
                if(configFormat.isAccepted(resource)){
                    return configFormat;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
	}


}
