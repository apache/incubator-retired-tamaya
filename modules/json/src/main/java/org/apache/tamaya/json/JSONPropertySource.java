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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;

import static java.lang.String.format;

/**
 * Property source based on a JSON file.
 */
public class JSONPropertySource implements PropertySource {
    /** Constant for enabling comments in Johnzon. */
    public static final String JOHNZON_SUPPORTS_COMMENTS_PROP = "org.apache.johnzon.supports-comments";

    /** The underlying resource. */
    private final URL urlResource;
    /** The values read. */
    private final Map<String, String> values;
    /** The evaluated ordinal. */
    private int ordinal;
    /** The JSON reader factory used. */
    private JsonReaderFactory readerFactory = initReaderFactory();

    /** Initializes the factory to be used for creating readers. */
    private JsonReaderFactory initReaderFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(JOHNZON_SUPPORTS_COMMENTS_PROP, true);
       return Json.createReaderFactory(config);
    }

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
        Map<String, Object> config = new HashMap<>();
        config.put(JOHNZON_SUPPORTS_COMMENTS_PROP, true);
        this.readerFactory = Json.createReaderFactory(config);
    }


    @Override
    public int getOrdinal() {
        PropertyValue configuredOrdinal = get(TAMAYA_ORDINAL);
        if(configuredOrdinal!=null){
            try{
                return Integer.parseInt(configuredOrdinal.getValue());
            } catch(Exception e){
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                        "Configured Ordinal is not an int number: " + configuredOrdinal, e);
            }
        }
        return ordinal;
    }

    @Override
    public String getName() {
        return urlResource.toExternalForm();
    }

    @Override
    public PropertyValue get(String key) {
        return PropertyValue.of(key, getProperties().get(key), getName());
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(values);
    }

    /**
     * Reads the configuration.
     * @param urlResource soure of the configuration.
     * @return the configuration read from the given resource URL.
     * @throws ConfigException if resource URL cannot be read.
     */
    protected Map<String, String> readConfig(URL urlResource) {
        try (InputStream is = urlResource.openStream()) {
            JsonStructure root = this.readerFactory.createReader(is, Charset.forName("UTF-8")).read();

            // Test added. H. Saly, 15. Aug. 2015
            if (!(root instanceof JsonObject)) {
                throw new ConfigException("Currently only JSON objects are supported");
            }

            Map<String, String> values = new HashMap<>();
            JSONVisitor visitor = new JSONVisitor((JsonObject)root, values);
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
