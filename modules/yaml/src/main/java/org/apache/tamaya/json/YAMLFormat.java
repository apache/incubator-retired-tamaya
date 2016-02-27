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
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;


/**
 * Implementation of the {@link org.apache.tamaya.format.ConfigurationFormat}
 * able to read configuration properties represented in JSON
 *
 * @see <a href="http://www.json.org">JSON format specification</a>
 */
public class YAMLFormat implements ConfigurationFormat {
    /**
     * THe logger.
     */
    private static final Logger LOG = Logger.getLogger(YAMLFormat.class.getName());

    /**
     * Constructor, itniaitlizing zhe JSON reader factory.
     */
    public YAMLFormat(){
    }

    @Override
    public String getName() {
        return "yaml";
    }

    @Override
    public boolean accepts(URL url) {
        return Objects.requireNonNull(url).getPath().endsWith(".yaml");
    }

    @Override
    public ConfigurationData readConfiguration(String resource, InputStream inputStream) {
        try( InputStream in = inputStream;) {
            Map<String, String> values = readConfig(resource, inputStream);
            return ConfigurationDataBuilder.of(resource, this).addProperties(values)
                .build();
        } catch (Exception e) {
            throw new ConfigException("Failed to read data from " + resource, e);
        }
    }

    /**
     * Reads the configuration.
     * @param inputStream the input stream, not null.
     * @return the configuration read from the given resource URL.
     * @throws ConfigException if resource URL cannot be read.
     */
    protected Map<String, String> readConfig(String resource, InputStream inputStream) {
        try{
            Yaml yaml = new Yaml();
            HashMap<String, String> values = new HashMap<>();
            Object config = yaml.load(inputStream);
            mapYamlIntoProperties(config, values);
            if(LOG.isLoggable(Level.FINEST)){
                LOG.finest("Read data from " + resource + " : " + values);
            }
            return values;
        }catch (Throwable t) {
            throw new ConfigException(format("Failed to read properties from %s", resource), t);
        }
    }
    /**
     * Reads the configuration.
     * @param urlResource soure of the configuration.
     * @return the configuration read from the given resource URL.
     * @throws ConfigException if resource URL cannot be read.
     */
    protected Map<String, String> readConfig(URL urlResource) {
        try (InputStream is = urlResource.openStream()) {
            return readConfig(urlResource.toExternalForm(), is);
        }
        catch (Throwable t) {
            throw new ConfigException(format("Failed to read properties from %s", urlResource.toExternalForm()), t);
        }
    }

    private void mapYamlIntoProperties(Object config, HashMap<String, String> values) {
        mapYamlIntoProperties("", config, values);
    }

    /**
     * Maps the given config item (could be a String, a collection type or something else returned by the yaml parser
     * to a key/value pair and adds it to {@code values} (hereby honoring the prefix as a key to be used.).
     * Collection types are recursively to remapped hereby extending the given prefix as needed and recursively
     * delegate mapping of values contained.
     * @param prefix the prefix or key evaluated so far, never null (but can be empty for root entries).
     * @param config the config value. Could be a single value or a collection type.
     * @param values the properties where items identified must be written into. These properties are going to be
     *               returned as result of the format reading operation ans integrated into the overall configuration
     *               map.
     */
    protected void mapYamlIntoProperties(String prefix, Object config, HashMap<String, String> values) {
        // add further data types supported by yaml, e.g. date, ...
        if(config instanceof List){
            StringBuilder b = new StringBuilder();
            for(Object val:((List<Object>)config)){
                b.append(mapValueToString(val));
                b.append(",");
            }
            if(b.length()>0){
                b.setLength(b.length()-1);
            }
            values.put(prefix, b.toString());
            values.put("_"+prefix+".collection-type", "List");
        } else if(config instanceof Map){
            for(Map.Entry<String,Object> en:((Map<String,Object>)config).entrySet()){
                String newPrefix = prefix.isEmpty()?en.getKey():prefix +"."+en.getKey();
                mapYamlIntoProperties(newPrefix, en.getValue(), values);
            }
        } else{
            values.put(prefix, mapValueToString(config));
        }
    }

    protected String mapValueToString(Object val) {
        return String.valueOf(val);
    }

}
