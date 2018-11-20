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
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertyValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>{@link org.apache.tamaya.spi.PropertySource} to access environment variables via Tamaya
 * which are setCurrent via {@code export VARIABLE=createValue} on UNIX systems or
 * {@code setCurrent VARIABLE=createValue} on Windows systems.</p>
 *
 * <p>Using the {@linkplain EnvironmentPropertySource} without any
 * additional configuration gives access to all existing environment
 * variables available to the Java process Tamaya is running in.</p>
 *
 * <h1>Simple usage example</h1>
 *
 * <pre>
 * $ export OPS_MODE=production
 * $ export COLOR=false
 * $ java -jar application.jar
 * </pre>
 *
 * <p>To access {@code OPS_MODE} and {@code COLOR} with the following code
 * fragment could be used:</p>
 *
 * <pre>
 * PropertySource ps = new EnvironmentPropertySource();
 * PropertyValue opsMode = ps.current("OPS_MODE");
 * PropertyValue color = ps.current("COLOR");
 * </pre>
 *
 * <h1>Application specific environment variables with prefix</h1>
 *
 * <p>Given the case where two instances of the same application are running on
 * a single machine but need different values for the environment variable
 * {@code CUSTOMER}. The {@linkplain EnvironmentPropertySource} allows you
 * to prefix the environment variable with an application specific prefix
 * and to access it by the non-prefixed variable name.</p>
 *
 * <pre>
 * $ export CUSTOMER=none
 * $ export a81.CUSTOMER=moon
 * $ export b78.CUSTOMER=luna
 * </pre>
 *
 * <p>Given an environment with these three variables the application running
 * for the customer called Moon could be started with the following command:</p>
 *
 * <pre>
 * $ java -Dtamaya.envprops.prefix=a81 -jar application.jar
 * </pre>
 *
 * <p>The application specific createValue can now be accessed from the code of the
 * application like this:</p>
 *
 * <pre>
 * PropertySource ps = new EnvironmentPropertySource();
 * PropertyValue pv = ps.current("CUSTOMER");
 * System.out.println(pv.getValue());
 * </pre>
 *
 * <p>The output of application would be {@code moon}.</p>
 *
 * <h1>Disabling the access to environment variables</h1>
 *
 * <p>The access to environment variables can simply be
 * disabled by setting the system property {@code tamaya.envprops.disable}
 * or {@code tamaya.defaults.disable} to {@code true}.</p>
 */
public class EnvironmentPropertySource extends BasePropertySource {
    private static final String TAMAYA_ENVPROPS_PREFIX = "tamaya.envprops.prefix";
    private static final String TAMAYA_ENVPROPS_DISABLE = "tamaya.envprops.disable";
    private static final String TAMAYA_DEFAULT_DISABLE = "tamaya.defaults.disable";

    /**
     * Default ordinal for {@link org.apache.tamaya.spisupport.propertysource.EnvironmentPropertySource}
     */
    public static final int DEFAULT_ORDINAL = 300;

    /**
     * Prefix that allows environment properties to virtually be mapped on specified sub section.
     */
    private String prefix;

    /**
     * If true, this property source does not return any properties. This is useful since this
     * property source is applied by default, but can be switched off by setting the
     * {@code tamaya.envprops.disable} system/environment property to {@code true}.
     */
    private boolean disabled = false;

    private SystemPropertiesProvider propertiesProvider = new SystemPropertiesProvider();

    /**
     * Creates a new instance. Also initializes the {@code prefix} and {@code disabled} properties
     * from the system-/ environment properties:
     * <pre>
     *     tamaya.envprops.prefix
     *     tamaya.envprops.disable
     * </pre>
     */
    public EnvironmentPropertySource(){
        initFromSystemProperties();
    }

    /**
     * Initializes the {@code prefix} and {@code disabled} properties from the system-/
     * environment properties:
     * <pre>
     *     tamaya.envprops.prefix
     *     tamaya.envprops.disable
     * </pre>
     */
    private void initFromSystemProperties() {
        // Read prefix
        String value = System.getProperty(TAMAYA_ENVPROPS_PREFIX);
        if(value==null){
            value = System.getenv(TAMAYA_ENVPROPS_PREFIX);
        }
        if(value !=null){
            this.prefix = value;
        }
        // Read enabled 1) for property source, 2) as default
        value = System.getProperty(TAMAYA_ENVPROPS_DISABLE);
        if(value==null){
            value = System.getenv(TAMAYA_ENVPROPS_DISABLE);
        }
        if(value==null){
            value = System.getProperty(TAMAYA_DEFAULT_DISABLE);
        }
        if(value==null){
            value = System.getenv(TAMAYA_DEFAULT_DISABLE);
        }
        if(value!=null && !value.isEmpty()) {
            this.disabled = Boolean.parseBoolean(value);
        }
    }

    /**
     * Creates a new instance using a fixed ordinal createValue.
     * @param ordinal the ordinal number.
     */
    public EnvironmentPropertySource(int ordinal){
        this(null, ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     * @param ordinal the ordinal to be used.
     */
    public EnvironmentPropertySource(String prefix, int ordinal){
        super("environment-properties");
        this.prefix = prefix;
        setOrdinal(ordinal);
    }

    /**
     * Creates a new instance.
     * @param prefix the prefix to be used, or null.
     */
    public EnvironmentPropertySource(String prefix){
        this.prefix = prefix;
    }

    @Override
    public int getDefaultOrdinal() {
        return DEFAULT_ORDINAL;
    }

    @Override
    public String getName() {
        if (isDisabled()) {
            return "environment-properties(disabled)";
        }
        return "environment-properties";
    }

    @Override
    public PropertyValue get(String key) {
        if (isDisabled()) {
            return null;
        }
        // Exact match (i.e. com.ACME.getNumChilds)
        String effectiveKey = hasPrefix() ? getPrefix() + "." + key
                : key;
        String value = getPropertiesProvider().getenv(effectiveKey);
        // Replace all . by _ (i.e. com_ACME_size)
        if(value==null){
            value = getPropertiesProvider().getenv(effectiveKey.replaceAll("\\.", "_"));
        }
        // Replace all . by _ and convert to upper case (i.e. COM_ACME_SIZE)
        if(value==null){
            value = getPropertiesProvider().getenv(effectiveKey.replaceAll("\\.", "_")
                    .toUpperCase());
        }
        if(value==null){
            return null;
        }
        return PropertyValue.of(key, value, getName());
    }

    private boolean hasPrefix() {
        return null != prefix && prefix.isEmpty();
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        if(disabled){
            return Collections.emptyMap();
        }
        String prefix = this.prefix;
        if(prefix==null) {
            Map<String, PropertyValue> entries = new HashMap<>(System.getenv().size());
            for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                entries.put(entry.getKey(), PropertyValue.of(entry.getKey(), entry.getValue(), getName()));
            }
            return entries;
        }else{
            Map<String, PropertyValue> entries = new HashMap<>(System.getenv().size());
            for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
                entries.put(prefix + entry.getKey(), PropertyValue.of(prefix + entry.getKey(), entry.getValue(), getName()));
            }
            return entries;
        }
    }


    @Override
    protected String toStringValues() {
        return  super.toStringValues() +
                "  prefix=" + prefix + '\n' +
                "  disabled=" + disabled + '\n';
    }

    void setPropertiesProvider(SystemPropertiesProvider spp) {
        propertiesProvider = spp;
        initFromSystemProperties();
    }

    SystemPropertiesProvider getPropertiesProvider() {
        return propertiesProvider;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isDisabled() {
        return disabled;
    }

    @Override
    public ChangeSupport getChangeSupport(){
        return ChangeSupport.IMMUTABLE;
    }

    /**
     * <p>Provides access to the system properties used to configure
     * {@linkplain EnvironmentPropertySource}.</p>
     *
     * <p>This implementation delegates all property lookups
     * to {@linkplain System#getProperty(String)}.</p>
     */
    static class SystemPropertiesProvider {
        String getEnvPropsPrefix() {
            return System.getenv(TAMAYA_ENVPROPS_PREFIX);
        }

        String getEnvPropsDisable() {
            return System.getenv(TAMAYA_ENVPROPS_DISABLE);
        }

        String getDefaultsDisable() {
            return System.getenv(TAMAYA_DEFAULT_DISABLE);
        }

        String getenv(String name) {
            return System.getenv(name);
        }

        Map<String, String> getenv() {
            return System.getenv();
        }
    }
}
