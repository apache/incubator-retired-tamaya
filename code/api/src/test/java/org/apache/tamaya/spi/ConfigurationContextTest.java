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
package org.apache.tamaya.spi;

import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the abstract functionality of {@link ConfigurationContext}.
 */
public class ConfigurationContextTest {

    @Test
    public void test_EMPTY(){
        assertThat(ConfigurationContext.EMPTY.getPropertySource("foo")).isNull();
        assertThat(ConfigurationContext.EMPTY.getPropertySources()).isNotNull();
        assertThat(ConfigurationContext.EMPTY.getPropertySources().isEmpty()).isTrue();
        assertThat(ConfigurationContext.EMPTY.getMetaData("foo")).isNotNull();
        assertThat(ConfigurationContext.EMPTY.getPropertyConverters()).isNotNull();
        assertThat(ConfigurationContext.EMPTY.getPropertyConverters().isEmpty()).isTrue();
        assertThat(ConfigurationContext.EMPTY.getPropertyFilters()).isNotNull();
        assertThat(ConfigurationContext.EMPTY.getPropertyFilters().isEmpty()).isTrue();
        assertThat(ConfigurationContext.EMPTY.getServiceContext()).isNotNull();
        assertThat(ConfigurationContext.EMPTY.getPropertyConverters(TypeLiteral.of(Boolean.class))).isNotNull();
        assertThat(ConfigurationContext.EMPTY.getPropertyConverters(TypeLiteral.of(Boolean.class)).isEmpty()).isTrue();
        assertThat(ConfigurationContext.EMPTY.toString()).isNotNull();
    }

}
