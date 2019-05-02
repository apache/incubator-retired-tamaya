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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Tests for {@link DefaultMetaDataProvider}.
 */
public class DefaultMetaDataProviderTest {

    @Test
    public void cretion() {
        assertThatCode(() -> new DefaultMetaDataProvider()).doesNotThrowAnyException();
    }

    @Test
    public void init() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertThat(provider).isEqualTo(provider.init(ConfigurationContext.EMPTY));
    }

    @Test
    public void getMetaData() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertThat(provider).isEqualTo(provider.init(ConfigurationContext.EMPTY));
        assertThat(provider.getMetaData("foo")).isNotNull();

    }

    @Test
    public void setMeta() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertThat(provider).isEqualTo(provider.init(ConfigurationContext.EMPTY));
        provider.setMeta("foo", "a", "b");
        assertThat(provider.getMetaData("foo")).isNotNull().hasSize(1);
    }

    @Test
    public void setMeta_Map() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertThat(provider).isEqualTo(provider.init(ConfigurationContext.EMPTY));
        Map<String,String> map = new HashMap<>();
        map.put("a", "b");
        provider.setMeta("foo", map);
        assertThat(provider.getMetaData("foo")).isNotNull().hasSize(1);

    }

    @Test
    public void reset() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertThat(provider).isEqualTo(provider.init(ConfigurationContext.EMPTY));
        provider.reset();
        assertThat(provider.getMetaData("foo")).isNotNull().isEmpty();
    }

    @Test
    public void reset1() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertThat(provider).isEqualTo(provider.init(ConfigurationContext.EMPTY));
        provider.reset();
        assertThat(provider.getMetaData("foo")).isNotNull().isEmpty();
    }

    @Test
    public void testToString() {
        DefaultMetaDataProvider provider = new DefaultMetaDataProvider();
        assertThat(provider.init(ConfigurationContext.EMPTY).toString())
            .isEqualTo("DefaultMetaDataProvider[additionalProperties = {}, context = ConfigurationContext.EMPTY]");
    }
}
