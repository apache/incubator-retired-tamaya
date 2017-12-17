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
package org.apache.tamaya.examples.customconfigsource;

import javax.config.spi.ConfigSource;
import javax.config.spi.ConfigSourceProvider;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleConfigSourceProvider implements ConfigSourceProvider {
    private static final String[] RESOURCES = {
        "cfgOther/a.properties", "cfgOther/b.properties", "cfgOther/c.properties"
    };

    @Override
    public Collection<ConfigSource> getConfigSources(ClassLoader cl) {
        List<ConfigSource> propertySources = new ArrayList<>();

        for (String res : RESOURCES) {
            URL url = ClassLoader.getSystemClassLoader().getResource(res);
            try {
                propertySources.add(new SimpleConfigSource(url));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return propertySources;
    }
}
