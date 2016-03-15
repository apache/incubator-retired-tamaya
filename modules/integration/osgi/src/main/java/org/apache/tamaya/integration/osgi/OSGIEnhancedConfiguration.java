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
package org.apache.tamaya.integration.osgi;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spisupport.BasePropertySource;
import org.apache.tamaya.spisupport.DefaultConfiguration;
import org.apache.tamaya.spisupport.DefaultConfigurationContext;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Configuration object that also reflects the values provided by the OSGI ConfigAdmin Configuration.
 * Similar to other tamaya areas adding a tamaya.ordinal into the corresponding OSGI configuration for
 * a pif/factoryPid allows to control the ordinal/priority of the OSGI configuration related to other
 * configured Tamaya Property Sources. Overall the configuration evaluation for Tamaya follows the
 * same rules, with the difference that each bunldle owns its own ConfigAdmin based part. From
 * Tamaya, the granularity depends on the implementation of the ConfigurationProviderSpi. By default
 * Tamaya configuration is managed as a global resource/config tree, wheres bundle specific sections are
 * selected only.
 */
public class OSGIEnhancedConfiguration extends DefaultConfiguration{
    /** The default ordinal used for the OSGI config, */
    private static final int OSGI_DEFAULT_ORDINAL = 0;

    /**
     * Constructor.
     *
     * @param osgiConfiguration The OSGI configuration found.
     */
    public OSGIEnhancedConfiguration(org.osgi.service.cm.Configuration osgiConfiguration) {
        super(new OSGIConfigurationContext(osgiConfiguration));
    }

    /**
     * Class that models a Tamaya ConfigurationContext, which implicitly contains the bundle specific
     * Configuration wrapped into a Tamaya PropertySource.
     */
    private static final class OSGIConfigurationContext extends DefaultConfigurationContext{
        private ConfigurationContext tamayaContext = ConfigurationProvider.getConfiguration().getContext();
        private OSGIPropertySource osgiPropertySource;

        public OSGIConfigurationContext(org.osgi.service.cm.Configuration osgiConfiguration){
            if(osgiConfiguration!=null) {
                this.osgiPropertySource = new OSGIPropertySource(osgiConfiguration);
            }
        }

        @Override
        public List<PropertySource> getPropertySources() {
            List<PropertySource> sources = super.getPropertySources();
            if(osgiPropertySource!=null){
                sources.add(osgiPropertySource);
            }
            return sources;
        }
    }

    /**
     * Tamaya PropertySource providing the values from an OSGI Configuration.
     */
    private static final class OSGIPropertySource extends BasePropertySource{

        private final org.osgi.service.cm.Configuration osgiConfiguration;

        public OSGIPropertySource(org.osgi.service.cm.Configuration osgiConfiguration){
            this.osgiConfiguration = Objects.requireNonNull(osgiConfiguration);
        }

        @Override
        public int getDefaultOrdinal() {
            String val = System.getProperty("osgi.defaultOrdinal");
            if(val!=null){
                return Integer.parseInt(val.trim());
            }
            return OSGI_DEFAULT_ORDINAL;
        }

        @Override
        public String getName() {
            return "OSGIConfig:pid="+
                    (osgiConfiguration.getPid()!=null?osgiConfiguration.getPid():osgiConfiguration.getFactoryPid());
        }

        @Override
        public Map<String, String> getProperties() {
            Map<String, String> map = new HashMap<>();
            Dictionary<String,Object> dict = osgiConfiguration.getProperties();
            Enumeration<String> keys = dict.keys();
            while(keys.hasMoreElements()){
                String key = keys.nextElement();
                map.put(key,String.valueOf(dict.get(key)));
            }
            return map;
        }
    }
}
