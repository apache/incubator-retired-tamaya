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
package org.apache.tamaya.core.propertysource;

import org.apache.tamaya.core.internal.PropertySourceComparator;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class BasePropertySourceTest {

    @Test
    public void isAlwaysScanable() {
        BasePropertySource bs = new BasePropertySource() {
            @Override
            public Map<String, PropertyValue> getProperties() {
                return Collections.emptyMap();
            }
        };

        assertThat(bs.isScannable()).isTrue();
    }

    @Test
    public void givenOrdinalOverwritesGivenDefaulOrdinal() {
        BasePropertySource bs = new BasePropertySource() {
            @Override
            public Map<String, PropertyValue> getProperties() {
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

        PropertySource defaultPropertySource = new BasePropertySource(56) {

            @Override
            public String getName() {
                return "testWithDefault";
            }

            @Override
            public PropertyValue get(String key) {
                return null;
            }

            @Override
            public Map<String, PropertyValue> getProperties() {
                return Collections.emptyMap();
            }
        };

        Assert.assertEquals(56, PropertySourceComparator.getOrdinal(defaultPropertySource));
        Assert.assertEquals(1000, new OverriddenOrdinalPropertySource().getOrdinal());

        // propertySource with invalid ordinal
        Assert.assertEquals(1, new OverriddenInvalidOrdinalPropertySource().getOrdinal());
    }

    @Test
    public void testGet() {
        Assert.assertEquals(1000, new OverriddenOrdinalPropertySource().getOrdinal());
    }

    private static class OverriddenOrdinalPropertySource extends BasePropertySource {

        private OverriddenOrdinalPropertySource() {
            super(250);
        }

        @Override
        public String getName() {
            return "overriddenOrdinal";
        }

        @Override
        public Map<String, PropertyValue> getProperties() {
            Map<String,PropertyValue> result = new HashMap<>(1);
            result.put(PropertySource.TAMAYA_ORDINAL, PropertyValue.of(PropertySource.TAMAYA_ORDINAL, "1000", getName()));
            return result;
        }
    }

    private static class OverriddenInvalidOrdinalPropertySource extends BasePropertySource {

        private OverriddenInvalidOrdinalPropertySource() {
            super(1);
        }

        @Override
        public String getName() {
            return "overriddenInvalidOrdinal";
        }

        @Override
        public Map<String, PropertyValue> getProperties() {
            Map<String,PropertyValue> result = new HashMap<>(1);
            result.put(PropertySource.TAMAYA_ORDINAL, PropertyValue.of(PropertySource.TAMAYA_ORDINAL, "invalid", getName()));
            return result;
        }
    }


}
