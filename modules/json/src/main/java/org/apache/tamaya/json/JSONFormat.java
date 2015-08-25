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
import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationDataBuilder;
import org.apache.tamaya.format.ConfigurationFormat;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

/**
 * Implementation of the {@link org.apache.tamaya.format.ConfigurationFormat}
 * able to read configuration properties represented in JSON
 *
 * @see <a href="http://www.json.org">JSON format specification</a>
 */
public class JSONFormat implements ConfigurationFormat {
    /** Property that make Johnzon accept commentc. */
    public static final String JOHNZON_SUPPORTS_COMMENTS_PROP = "org.apache.johnzon.supports-comments";
    /** The reader factory used. */
    private JsonReaderFactory readerFactory;

    /**
     * Constructor, itniaitlizing zhe JSON reader factory.
     */
    public JSONFormat(){
        Map<String, Object> config = new HashMap<String, Object>();
        config.put(JOHNZON_SUPPORTS_COMMENTS_PROP, true);
        this.readerFactory = Json.createReaderFactory(config);
    }

    @Override
    public String getName() {
        return "json";
    }

    @Override
    public boolean accepts(URL url) {
        return Objects.requireNonNull(url).getPath().endsWith(".json");
    }

    @Override
    public ConfigurationData readConfiguration(String resource, InputStream inputStream) {

        try {
            final JsonReader reader = this.readerFactory.createReader(inputStream, Charset.forName("UTF-8"));
            JsonObject root = reader.readObject();
            HashMap<String, String> values = new HashMap<>();
            JSONVisitor visitor = new JSONVisitor(root, values);
            visitor.run();
            return ConfigurationDataBuilder.of(resource, this).addProperties(values)
                                           .build();
        } catch (JsonException e) {
            throw new ConfigException("Failed to read data from " + resource, e);
        }
    }
}
