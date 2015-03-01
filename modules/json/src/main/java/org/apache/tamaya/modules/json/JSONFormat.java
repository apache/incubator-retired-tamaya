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

import org.apache.tamaya.format.ConfigurationData;
import org.apache.tamaya.format.ConfigurationFormat;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;

/**
 * Implementation of the {@link org.apache.tamaya.format.ConfigurationFormat}
 * able to read configuration properties represented in JSON
 *
 * @see <a href="http://www.json.org">JSON format specification</a>
 */
public class JSONFormat implements ConfigurationFormat {
    /**
     * Lock for internal synchronization.
     */
    private StampedLock lock = new StampedLock();

    @Override
    public boolean accepts(URL url) {
        Objects.requireNonNull(url);

        boolean isAFile = url.getProtocol().equals("file");
        boolean isJSON = url.getPath().endsWith(".json");


        return isAFile && isJSON;
    }

    @Override
    public ConfigurationData readConfiguration(String resource, InputStream inputStream) {

//        try (InputStream is = new InputStreamCloser(inputStream)){
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(is);


//        } catch (IOException e) {
//            throw new ConfigException("Failed to ");
//        }



        throw new RuntimeException("Not implemented yet!");
    }
}
