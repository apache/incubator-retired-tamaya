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
package org.apache.tamaya.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertySource;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;

/**
 * Property source based on a JSON file.
 */
public class JSONPropertySource implements PropertySource {
    /** The underlying resource. */
    private URL urlResource;
    /** The values read. */
    private Map<String, String> values;
    /** The evaluated ordinal. */
    private int ordinal;

    /**
     * Constructor, hereby using 0 as the default ordinal.
     * @param resource the resource modelled as URL, not null.
     */
    public JSONPropertySource(URL resource) {
        this(resource, 0);
    }

    /**
     * Constructor.
     * @param resource the resource modelled as URL, not null.
     * @param defaultOrdinal the defaultOrdinal to be used.
     */
    public JSONPropertySource(URL resource, int defaultOrdinal) {
        urlResource = Objects.requireNonNull(resource);
        this.ordinal = defaultOrdinal; // may be overriden by read...
        this.values = readConfig(urlResource);
        if (this.values.containsKey(TAMAYA_ORDINAL)) {
            this.ordinal = Integer.parseInt(this.values.get(TAMAYA_ORDINAL));
        }
    }


    @Override
    public int getOrdinal() {
        String configuredOrdinal = get(TAMAYA_ORDINAL);
        if(configuredOrdinal!=null){
            try{
                return Integer.parseInt(configuredOrdinal);
            } catch(Exception e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING, e,
                        () -> "Configured Ordinal is not an int number: " + configuredOrdinal);
            }
        }
        return ordinal;
    }

    @Override
    public String getName() {
        return urlResource.toExternalForm();
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(values);
    }

    /**
     * Reads the configuration.
     */
    protected Map<String, String> readConfig(URL urlResource) {
        try (InputStream is = urlResource.openStream()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(is);

            // @todo Add test for this. Oliver B. Fischer, 5. Jan. 2015
            if (!(root instanceof ObjectNode)) {
                throw new ConfigException("Currently only JSON objects are supported");
            }

            Map<String, String> values = new HashMap<>();
            JSONVisitor visitor = new JSONVisitor((ObjectNode) root, values);
            visitor.run();
            return values;
        }
        catch (Throwable t) {
            throw new ConfigException(format("Failed to read properties from %s", urlResource.toExternalForm()), t);
        }
    }

    @Override
    public boolean isScannable() {
        return true;
    }
}
