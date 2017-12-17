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
package org.apache.tamaya.base.configsource;

import org.apache.tamaya.base.configsource.BaseConfigSource;
import org.apache.tamaya.base.configsource.ConfigSourceComparator;
import org.junit.Assert;
import org.junit.Test;

import javax.config.spi.ConfigSource;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseConfigSourceTest {

    @Test
    public void givenOrdinalOverwritesGivenDefaulOrdinal() {
        BaseConfigSource bs = new BaseConfigSource() {
            @Override
            public Map<String, String> getProperties() {
                return Collections.emptyMap();
            }
        };

        bs.setDefaultOrdinal(10);

        assertThat(bs.getDefaultOrdinal()).isEqualTo(10);
        assertThat(bs.getOrdinal()).isEqualTo(10);

        bs.setOrdinal(20);

        assertThat(bs.getOrdinal()).isEqualTo(20);
    }

    @Test
    public void testGetOrdinal() {

        ConfigSource defaultPropertySource = new BaseConfigSource(56) {

            @Override
            public String getName() {
                return "testWithDefault";
            }

            @Override
            public String getValue(String key) {
                return null;
            }

            @Override
            public Map<String,String> getProperties() {
                return Collections.emptyMap();
            }
        };

        Assert.assertEquals(56, ConfigSourceComparator.getOrdinal(defaultPropertySource));
        Assert.assertEquals(1000, new OverriddenOrdinalConfigSource().getOrdinal());

        // propertySource with invalid ordinal
        Assert.assertEquals(1, new OverriddenInvalidOrdinalConfigSource().getOrdinal());
    }

    @Test
    public void testGet() {
        Assert.assertEquals(1000, new OverriddenOrdinalConfigSource().getOrdinal());
    }

    private static class OverriddenOrdinalConfigSource extends BaseConfigSource {

        private OverriddenOrdinalConfigSource() {
            super(250);
        }

        @Override
        public String getName() {
            return "overriddenOrdinal";
        }

        @Override
        public Map<String,String> getProperties() {
            Map<String,String> result = new HashMap<>(1);
            result.put(CONFIG_ORDINAL, "1000");
            return result;
        }
    }

    private static class OverriddenInvalidOrdinalConfigSource extends BaseConfigSource {

        private OverriddenInvalidOrdinalConfigSource() {
            super(1);
        }

        @Override
        public String getName() {
            return "overriddenInvalidOrdinal";
        }

        @Override
        public Map<String, String> getProperties() {
            Map<String,String> result = new HashMap<>(1);
            result.put(CONFIG_ORDINAL, "invalid");
            return result;
        }
    }


}
