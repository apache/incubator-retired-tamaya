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
package org.apache.tamaya.modules.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.core.propertysource.DefaultOrdinal;
import org.apache.tamaya.spi.PropertySource;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.StampedLock;

import static java.lang.String.format;

/**
 * Property source based on a JSON file.
 */
public class JSONPropertySource
    implements PropertySource {

    private int priority = DefaultOrdinal.FILE_PROPERTIES;
    private InputResource source;
    private HashMap<String, String> values;

    /**
     * Lock for internal synchronization.
     */
    private StampedLock propertySourceLock = new StampedLock();


    public JSONPropertySource(File file) {
        this(file, 0);
    }

    public JSONPropertySource(File file, int priority) {
        this.priority = priority;
        source = new FileBasedResource(file);
    }

    @Override
    public int getOrdinal() {
        Lock writeLock = propertySourceLock.asWriteLock();

        try {
            writeLock.lock();

            if (values == null) {
                readSource();
            }
        } finally {
            writeLock.unlock();
        }

        return priority;
    }

    @Override
    public String getName() {
        return "json-properties";
    }

    @Override
    public String get(String key) {
        Objects.requireNonNull(key, "Key must not be null");

        return getProperties().get(key);
    }

    @Override
    public Map<String, String> getProperties() {
        Lock writeLock = propertySourceLock.asWriteLock();

        try {
            writeLock.lock();

            if (values == null) {
                readSource();
            }

            return Collections.unmodifiableMap(values);
        } finally {
            writeLock.unlock();
        }
    }

    protected void readSource() {
        try (InputStream is = source.getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            // @todo Add test for this. Oliver B. Fischer, 5. Jan. 2015
            if (!(root instanceof ObjectNode)) {
                throw new ConfigException("Currently only JSON objects are supported");
            }

            HashMap<String, String> values = new HashMap<>();
            JSONVisitor visitor = new JSONVisitor((ObjectNode) root, values);
            visitor.run();

            this.values = values;

            if (this.values.containsKey(TAMAYA_ORDINAL)) {
                int newPriority = Integer.parseInt(this.values.get(TAMAYA_ORDINAL));
                priority = newPriority;
                this.values.remove(TAMAYA_ORDINAL);
            }
        }
        catch (Throwable t) {
            throw new ConfigException(format("Failed to read properties from %s", source.getDescription()), t);
        }

    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
