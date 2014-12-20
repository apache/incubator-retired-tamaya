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
package org.apache.tamaya.core.env;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.Environment;

/**
 * Properties implementation class that can be applied as current System properties by calling
 * {@link org.apache.tamaya.core.env.ConfiguredSystemProperties#install()}. The system properties will
 * then behave contextually depending on the current runtime configuration active.
 */
public class ConfiguredSystemProperties extends Properties {

	private static final long serialVersionUID = 2152870929299226804L;

	private static final Logger LOG = Logger.getLogger(ConfiguredSystemProperties.class.getName());
    private Properties initialProperties;
    private static volatile Map<String, Properties> contextualProperties = new ConcurrentHashMap<>();
    private static volatile Supplier<String> contextProvider = () ->
            Environment.current().get("context.id").orElse("<system>");


    private final Object LOCK = new Object();


    private ConfiguredSystemProperties(Properties initialProperties) {
        super(initialProperties);
        this.initialProperties = initialProperties;
    }

    public static void install() {
        Properties props = System.getProperties();
        if (props instanceof ConfiguredSystemProperties) {
            return;
        }
        ConfiguredSystemProperties systemProps = new ConfiguredSystemProperties(props);
        LOG.finest("Installing enhanced system properties...");
        System.setProperties(systemProps);
        LOG.info("Installed enhanced system properties successfully.");
    }

    public static void uninstall() {
        Properties props = System.getProperties();
        if (props instanceof ConfiguredSystemProperties) {
            Properties initialProperties = ((ConfiguredSystemProperties) props).initialProperties;
            LOG.finest("Uninstalling enhanced system properties...");
            System.setProperties(initialProperties);
            LOG.info("Uninstalled enhanced system properties successfully.");
        }
    }

    @Override
    public String getProperty(String key) {
        return getContextualProperties().getProperty(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return getContextualProperties().getProperty(key, defaultValue);
    }

    @Override
    public Enumeration<?> propertyNames() {
        return getContextualProperties().propertyNames();
    }

    @Override
    public Set<String> stringPropertyNames() {
        return getContextualProperties().stringPropertyNames();
    }

    @Override
    public synchronized int size() {
        return getContextualProperties().size();
    }

    @Override
    public synchronized Enumeration<Object> keys() {
        return getContextualProperties().keys();
    }

    @Override
    public synchronized Enumeration<Object> elements() {
        return getContextualProperties().elements();
    }

    @Override
    public synchronized boolean contains(Object value) {
        return getContextualProperties().contains(value);
    }

    @Override
    public boolean containsValue(Object value) {
        return getContextualProperties().containsValue(value);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        return getContextualProperties().containsKey(key);
    }

    @Override
    public synchronized Object get(Object key) {
        return getContextualProperties().get(key);
    }

    @Override
    public synchronized Object clone() {
        return getContextualProperties().clone();
    }

    @Override
    public Set<Object> keySet() {
        return getContextualProperties().keySet();
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return getContextualProperties().entrySet();
    }

    @Override
    public Collection<Object> values() {
        return getContextualProperties().values();
    }


    @Override
    public Object getOrDefault(Object key, Object defaultValue) {
        return getContextualProperties().getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super Object, ? super Object> action) {
        getContextualProperties().forEach(action);
    }


    @Override
    public Object computeIfAbsent(Object key, Function<? super Object, ?> mappingFunction) {
        return getContextualProperties().computeIfAbsent(key, mappingFunction);
    }

    @Override
    public synchronized Object computeIfPresent(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return getContextualProperties().computeIfPresent(key, remappingFunction);
    }

    @Override
    public synchronized Object compute(Object key, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return getContextualProperties().compute(key, remappingFunction);
    }

    @Override
    public String toString() {
        return getContextualProperties().toString();
    }

    @Override
    public synchronized Object setProperty(String key, String value) {
        return getContextualProperties().setProperty(key, value);
    }

    @Override
    public synchronized void load(Reader reader) throws IOException {
        getContextualProperties().load(reader);
    }

    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        getContextualProperties().load(inStream);
    }

    @SuppressWarnings("deprecation")
	@Override
    public void save(OutputStream out, String comments) {
        super.save(out, comments);
    }

    @Override
    public void store(Writer writer, String comments) throws IOException {
        getContextualProperties().store(writer, comments);
    }

    @Override
    public void store(OutputStream out, String comments) throws IOException {
        getContextualProperties().store(out, comments);
    }

    @Override
    public void loadFromXML(InputStream in) throws IOException {
        getContextualProperties().loadFromXML(in);
    }

    @Override
    public void storeToXML(OutputStream os, String comment) throws IOException {
        getContextualProperties().storeToXML(os, comment);
    }

    @Override
    public void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        getContextualProperties().storeToXML(os, comment, encoding);
    }

    @Override
    public void list(PrintStream out) {
        getContextualProperties().list(out);
    }

    @Override
    public void list(PrintWriter out) {
        getContextualProperties().list(out);
    }

    @Override
    public boolean isEmpty() {
        return getContextualProperties().isEmpty();
    }

    @Override
    public Object put(Object key, Object value) {
        return getContextualProperties().put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return getContextualProperties().remove(key);
    }

    @Override
    public void putAll(Map<?, ?> t) {
        getContextualProperties().putAll(t);
    }

    @Override
    public void clear() {
        getContextualProperties().clear();
    }

    @Override
    public boolean equals(Object o) {
        return getContextualProperties().equals(o);
    }

    @Override
    public int hashCode() {
        return getContextualProperties().hashCode();
    }

    @Override
    public void replaceAll(BiFunction<? super Object, ? super Object, ?> function) {
        getContextualProperties().replaceAll(function);
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {
        return getContextualProperties().putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return getContextualProperties().remove(key, value);
    }

    @Override
    public boolean replace(Object key, Object oldValue, Object newValue) {
        return getContextualProperties().replace(key, oldValue, newValue);
    }

    @Override
    public Object replace(Object key, Object value) {
        return getContextualProperties().replace(key, value);
    }

    @Override
    public Object merge(Object key, Object value, BiFunction<? super Object, ? super Object, ?> remappingFunction) {
        return getContextualProperties().merge(key, value, remappingFunction);
    }

    public Properties getInitialProperties() {
        return initialProperties;
    }

    /**
     * Uninstalls the contextual system properties for the current context, as determined by the current
     * context provider active.
     */
    public static void resetProperties() {
        String contextId = contextProvider == null ? "" : contextProvider.get();
        contextualProperties.remove(contextId);
    }

    protected Properties getContextualProperties() {
        String contextId = contextProvider == null ? "" : contextProvider.get();
        Properties props = ConfiguredSystemProperties.contextualProperties.get(contextId);
        if (props == null) {
            synchronized (LOCK) {
                props = ConfiguredSystemProperties.contextualProperties.get(contextId);
                if (props == null) {
                    props = createNewProperties();
                    contextualProperties.put(contextId, props);
                }
            }
        }
        return props;
    }

    protected Properties createNewProperties() {
        Properties props = new Properties(initialProperties);
        Configuration config = Configuration.current();
        Map<String, String> configMap = config.toMap();
        for (Map.Entry<String, String> en : configMap.entrySet()) {
            props.put(en.getKey(), en.getValue());
        }
        return props;
    }
}
