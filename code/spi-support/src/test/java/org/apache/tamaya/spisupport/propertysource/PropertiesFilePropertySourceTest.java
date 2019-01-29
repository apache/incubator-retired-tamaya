/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesFilePropertySourceTest {

    private final SimplePropertySource testfilePropertySource = new SimplePropertySource(Thread.currentThread()
            .getContextClassLoader().getResource("testfile.properties"));
    private final SimplePropertySource overrideOrdinalPropertySource = new SimplePropertySource(
            Thread.currentThread().getContextClassLoader().getResource("overrideOrdinal.properties"));


    @Test
    public void testGetOrdinal() {
        assertThat(testfilePropertySource.getOrdinal()).isEqualTo(0);
        assertThat(Integer.parseInt(overrideOrdinalPropertySource.get(PropertySource.TAMAYA_ORDINAL)
                .getValue()))
                .isEqualTo(overrideOrdinalPropertySource.getOrdinal());
    }


    @Test
    public void testGet() {
        assertThat(testfilePropertySource.get("key3").getValue()).isEqualTo("val3");
        assertThat(overrideOrdinalPropertySource.get("mykey5").getValue()).isEqualTo("myval5");
        assertThat(testfilePropertySource.get("nonpresentkey")).isNull();
    }


    @Test
    public void testGetProperties() throws Exception {
        assertThat(testfilePropertySource.getProperties()).hasSize(5).containsKeys("key1", "key2", "key3", "key4", "key5");
    }
}
