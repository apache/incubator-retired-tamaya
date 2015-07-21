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
package org.apache.tamaya.resource;

import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AbstractPathPropertySourceProviderTest {

    private AbstractPathPropertySourceProvider myProvider = new AbstractPathPropertySourceProvider("*.properties") {
        @Override
        protected Collection<PropertySource> getPropertySources(URL url) {
            List<PropertySource> result = new ArrayList<>();
            result.add(new EmptyPropertySource());
            return result;
        }
    };

    @Test
    public void testGetPropertySources() throws Exception {
        assertNotNull(myProvider.getPropertySources());
    }

    @Test
    public void testCreatePropertiesPropertySource() throws Exception {
        PropertySource ps = AbstractPathPropertySourceProvider.createPropertiesPropertySource(
                ClassLoader.getSystemClassLoader().getResource("test.properties")
        );
        assertNotNull(ps);
        assertTrue(ps.getProperties().isEmpty());
    }

    private static final class EmptyPropertySource implements PropertySource {
        /**
         * Lookup order:
         * TODO rethink whole default PropertySources and ordering:
         * TODO introduce default values or constants for ordinals
         * <ol>
         * <li>System properties (ordinal 400)</li>
         * <li>Environment properties (ordinal 300)</li>
         * <li>JNDI values (ordinal 200)</li>
         * <li>Properties file values (/META-INF/applicationConfiguration.properties) (ordinal 100)</li>
         * </ol>
         * <p/>
         * <p><b>Important Hints for custom implementations</b>:</p>
         * <p>
         * If a custom implementation should be invoked <b>before</b> the default implementations, use a value &gt; 400
         * </p>
         * <p>
         * If a custom implementation should be invoked <b>after</b> the default implementations, use a value &lt; 100
         * </p>
         * <p/>
         * <p>Reordering of the default order of the config-sources:</p>
         * <p>Example: If the properties file/s should be used <b>before</b> the other implementations,
         * you have to configure an ordinal &gt; 400. That means, you have to add e.g. deltaspike_ordinal=401 to
         * /META-INF/apache-deltaspike.properties . Hint: In case of property files every file is handled as independent
         * config-source, but all of them have ordinal 400 by default (and can be reordered in a fine-grained manner.</p>
         *
         * @return the 'importance' aka ordinal of the configured values. The higher, the more important.
         */
        public int getOrdinal() {
            String configuredOrdinal = get(TAMAYA_ORDINAL);
            if (configuredOrdinal != null) {
                try {
                    return Integer.parseInt(configuredOrdinal);
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING,
                            "Configured Ordinal is not an int number: " + configuredOrdinal, e);
                }
            }
            return getDefaultOrdinal();
        }

        /**
         * Returns the  default ordinal used, when no ordinal is set, or the ordinal was not parseable to an int value.
         *
         * @return the  default ordinal used, by default 0.
         */
        public int getDefaultOrdinal() {
            return 0;
        }

        @Override
        public String getName() {
            return "<empty>";
        }

        @Override
        public String get(String key) {
            return null;
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.emptyMap();
        }

        @Override
        public boolean isScannable() {
            return true;
        }
    }
}