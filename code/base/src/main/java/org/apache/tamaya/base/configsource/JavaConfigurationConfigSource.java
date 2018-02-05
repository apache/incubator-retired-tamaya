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
package org.apache.tamaya.base.configsource;

import org.apache.tamaya.base.ServiceContextManager;

import javax.config.spi.ConfigSource;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.lang.String.format;
import static java.lang.Thread.currentThread;

/**
 * Provider which reads all {@value DEFAULT_SIMPLE_PROPERTIES_FILE_NAME} and
 * {@value DEFAULT_XML_PROPERTIES_FILE_NAME} files found in the
 * classpath. By setting
 * {@code tamaya.defaultprops.disable} or {@code tamaya.defaults.disable}
 * as system or environment property this feature can be disabled.
 */
public class JavaConfigurationConfigSource extends BaseConfigSource {
    /**
     * Default location in the classpath, where Tamaya looks for simple line based configuration by default.
     */
    public static final String DEFAULT_SIMPLE_PROPERTIES_FILE_NAME="META-INF/javaconfig.properties";

    /**
     * Default location in the classpath, where Tamaya looks for XML based configuration by default.
     */
    public static final String DEFAULT_XML_PROPERTIES_FILE_NAME = "META-INF/javaconfig.xml";

    private static final int DEFAULT_ORDINAL = 900;

    private boolean enabled = evaluateEnabled();

    public JavaConfigurationConfigSource(){
        super("resource:META-INF/javaconfig.*", DEFAULT_ORDINAL);
    }

    private boolean evaluateEnabled() {
        String value = System.getProperty("tamaya.defaultprops.disable");
        if(value==null){
            value = System.getenv("tamaya.defaultprops.disable");
        }
        if(value==null){
            value = System.getProperty("tamaya.defaults.disable");
        }
        if(value==null){
            value = System.getenv("tamaya.defaults.disable");
        }
        if(value==null){
            return true;
        }
        return value.isEmpty() || !Boolean.parseBoolean(value);
    }

    private List<ConfigSource> getPropertySources() {
        List<ConfigSource> propertySources = new ArrayList<>();
        propertySources.addAll(loadPropertySourcesByName(DEFAULT_SIMPLE_PROPERTIES_FILE_NAME));
        propertySources.addAll(loadPropertySourcesByName(DEFAULT_XML_PROPERTIES_FILE_NAME));
        Collections.sort(propertySources, ConfigSourceComparator.getInstance());
        return propertySources;
    }

    private Collection<? extends ConfigSource> loadPropertySourcesByName(String filename) {
        List<ConfigSource> propertySources = new ArrayList<>();
        Enumeration<URL> propertyLocations;
        try {
            propertyLocations = ServiceContextManager.getServiceContext()
                    .getResources(filename, currentThread().getContextClassLoader());
        } catch (IOException e) {
            String msg = format("Error while searching for %s", filename);

            throw new IllegalStateException(msg, e);
        }

        while (propertyLocations.hasMoreElements()) {
            URL currentUrl = propertyLocations.nextElement();
            SimpleConfigSource sps = new SimpleConfigSource(currentUrl);

            propertySources.add(sps);
        }

        return propertySources;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }


    @Override
    public Map<String, String> getProperties() {
        if (!isEnabled()) {
            return Collections.emptyMap();
        }
        Map<String,String> result = new HashMap<>();
        for(ConfigSource ps:getPropertySources()){
            result.putAll(ps.getProperties());
        }
        return result;
    }

    @Override
    public String toString() {
        return "JavaConfigPropertySource{" +
                "enabled=" + enabled +
                '}';
    }
}
