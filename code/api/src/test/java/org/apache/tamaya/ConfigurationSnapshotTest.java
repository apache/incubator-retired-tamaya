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
package org.apache.tamaya;

import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationSnapshotTest {

    public void testEMPTY(){
        assertThat(ConfigurationSnapshot.EMPTY).isNotNull();
    }

    @Test
    public void testEMPTY_timestamp(){
        assertThat(ConfigurationSnapshot.EMPTY.getTimestamp()).isEqualTo(0);
    }

    @Test
    public void testEMPTY_get(){
        assertThat(ConfigurationSnapshot.EMPTY.get("foo")).isNull();
    }

    @Test
    public void testEMPTY_getOptional(){
        assertThat(ConfigurationSnapshot.EMPTY.getOptional("foo")).isNotNull();
        assertThat(ConfigurationSnapshot.EMPTY.getOptional("foo").isPresent()).isFalse();
    }

    @Test
    public void testEMPTY_getOrDefault_noValue(){
        assertThat(ConfigurationSnapshot.EMPTY.getOrDefault("foo", null)).isNull();
    }

    @Test
    public void testEMPTY_getOrDefault_withValue(){
        assertThat("foo").isEqualTo(ConfigurationSnapshot.EMPTY.getOrDefault("foo", "foo"));
    }

    @Test
    public void testEMPTY_getOptional_Iterable(){
        assertThat(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"))).isNotNull();
        assertThat(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo")).isPresent()).isFalse();
    }

    @Test
    public void testEMPTY_getOptional_Class_Iterable(){
        assertThat(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), String.class)).isNotNull();
        assertThat(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), String.class).isPresent()).isFalse();
    }

    @Test
    public void testEMPTY_getOptional_Typeliteral_Iterable(){
        assertThat(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), TypeLiteral.of(String.class))).isNotNull();
        assertThat(ConfigurationSnapshot.EMPTY.getOptional(Collections.singleton("foo"), TypeLiteral.of(String.class)).isPresent()).isFalse();
    }

    @Test
    public void testEMPTY_get_Iterable(){
        assertThat(ConfigurationSnapshot.EMPTY.get(Collections.singleton("foo"))).isNull();
    }

    @Test
    public void testEMPTY_get_Iterable_Class(){
        assertThat(ConfigurationSnapshot.EMPTY.get(Collections.singleton("foo"), String.class)).isNull();
    }

    @Test
    public void testEMPTY_getOrDefault_Class_withValue(){
        assertThat("foo").isEqualTo(ConfigurationSnapshot.EMPTY.getOrDefault("foo", String.class, "foo"));
    }

    @Test
    public void testEMPTY_getOrDefault_TypeLiteral_withValue(){
        assertThat("foo").isEqualTo(ConfigurationSnapshot.EMPTY.getOrDefault("foo", TypeLiteral.of(String.class), "foo"));
    }

    @Test
    public void testEMPTY_get_Iterable_TypeLiteral(){
        assertThat((String) ConfigurationSnapshot.EMPTY.get(Collections.singleton("foo"), TypeLiteral.of(String.class))).isNull();
    }

    @Test
    public void testEMPTY_get_Classl(){
        assertThat((String) ConfigurationSnapshot.EMPTY.get("foo", TypeLiteral.of(String.class))).isNull();
    }

    @Test
    public void testEMPTY_get_TypeLiteral(){
        assertThat((String) ConfigurationSnapshot.EMPTY.get("foo", TypeLiteral.of(String.class))).isNull();
    }

    @Test
    public void testEMPTY_getKeys(){
        assertThat(ConfigurationSnapshot.EMPTY.getKeys()).isNotNull();
        assertThat(ConfigurationSnapshot.EMPTY.getKeys().isEmpty()).isTrue();
    }

    @Test
    public void testEMPTY_getContext(){
        assertThat(ConfigurationContext.EMPTY).isEqualTo(ConfigurationSnapshot.EMPTY.getContext());
    }

    @Test
    public void testEMPTY_getPropertiest(){
        assertThat(ConfigurationSnapshot.EMPTY.getProperties()).isNotNull();
        assertThat(ConfigurationSnapshot.EMPTY.getProperties().isEmpty()).isTrue();
    }

    @Test
    public void testEMPTY_toBuildert(){
        assertThat(ConfigurationSnapshot.EMPTY.toBuilder()).isNotNull();
    }

    @Test
    public void testEMPTY_toStringt(){
        assertThat(ConfigurationSnapshot.EMPTY.toString()).isNotNull();
    }

    @Test
    public void testEMPTY_getSnapshot(){
        assertThat(ConfigurationSnapshot.EMPTY).isEqualTo(ConfigurationSnapshot.EMPTY.getSnapshot());
    }

    @Test
    public void testEMPTY_getSnapshot_Keys(){
        assertThat(ConfigurationSnapshot.EMPTY).isEqualTo(ConfigurationSnapshot.EMPTY.getSnapshot("foo"));
    }

    @Test
    public void testEMPTY_getSnapshot_Iterable(){
        assertThat(ConfigurationSnapshot.EMPTY).isEqualTo(ConfigurationSnapshot.EMPTY.getSnapshot(Collections.singletonList("foo")));
    }

}
