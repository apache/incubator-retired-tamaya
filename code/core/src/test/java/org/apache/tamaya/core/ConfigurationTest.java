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
package org.apache.tamaya.core;

import org.apache.tamaya.Configuration;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This tests checks if the combination of 2 prioritized PropertySource return valid results of the final configuration.
 */
public class ConfigurationTest {

    @Test
    public void testAccess(){
        assertThat(current()).isNotNull();
    }

    private Configuration current() {
        return Configuration.current();
    }

    @Test
    public void testContent(){
        assertThat(current().get("name")).isNotNull();
        assertThat(current().get("name2")).isNotNull(); // from default
        assertThat(current().get("name3")).isNotNull(); // overridden default, mapped by filter to name property
        String value4 = current().get("name4");
        assertThat(value4).isNotNull(); // final only


        assertThat(current().get("name")).isEqualTo("Robin");
        assertThat(current().get("name2")).isEqualTo("Sabine"); // from default
        assertThat(current().get("name3")).isEqualTo("Mapped to name: Robin");  // overridden default, mapped by filter to name property
        assertThat(value4).startsWith("Sereina(filtered1)(filtered2)(filtered3)(filtered4)(filtered5)(filtered6)(filtered7)(filtered8)(filtered9)(filtered10)"); // final only
        assertThat(current().get("name5")).isNull(); // final only, but removed from filter
    }
}
