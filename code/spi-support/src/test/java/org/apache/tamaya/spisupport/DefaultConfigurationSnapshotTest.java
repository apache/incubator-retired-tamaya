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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationSnapshot;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DefaultConfigurationSnapshotTest {

//    confkey1=javaconf-value1
//    confkey2=javaconf-value2
//    confkey3=javaconf-value3

    @Test
    public void testCreationForKeys() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
    }

    @Test
    public void getFrozenAtReturnsTheCorrectTimestamp() throws InterruptedException {
        Configuration source = mock(Configuration.class);
        Mockito.when(source.getContext()).thenReturn(ConfigurationContext.EMPTY);
        Mockito.when(source.getSnapshot(Mockito.anyCollection())).thenReturn(ConfigurationSnapshot.EMPTY);
        Mockito.when(source.getSnapshot()).thenReturn(ConfigurationSnapshot.EMPTY);
        Mockito.when(source.getProperties()).thenReturn(Collections.emptyMap());

        long poiStart = System.nanoTime();
        Thread.sleep(10L);
        DefaultConfigurationSnapshot fc = new DefaultConfigurationSnapshot(source);
        Thread.sleep(10L);

        long poiEnd = System.nanoTime();

        assertThat(fc.getTimestamp()>poiStart).isTrue();
        assertThat(fc.getTimestamp()<poiEnd).isTrue();
    }


    @Test
    public void idMustBeNotNull() {
        Configuration source = mock(Configuration.class);

        Mockito.when(source.getContext()).thenReturn(ConfigurationContext.EMPTY);
        Mockito.when(source.getProperties()).thenReturn(Collections.emptyMap());

        DefaultConfigurationSnapshot fc = new DefaultConfigurationSnapshot(source);

        assertThat(fc).isNotNull();
    }

    /*
     * All tests for equals() and hashCode() go here...
     */
    @Test
    public void twoFrozenAreDifferentIfTheyHaveADifferentIdAndFrozenAtTimestamp() {
        Map<String, String> properties = new HashMap<>();
        properties.put("key", "createValue");

        Configuration configuration = mock(Configuration.class);
        Mockito.when(configuration.getContext()).thenReturn(ConfigurationContext.EMPTY);
        doReturn(properties).when(configuration).getProperties();

        DefaultConfigurationSnapshot fcA = new DefaultConfigurationSnapshot(configuration);
        DefaultConfigurationSnapshot fcB = new DefaultConfigurationSnapshot(configuration);

        assertThat(fcA).isNotEqualTo(fcB);
    }

    /*
     * END OF ALL TESTS for equals() and hashCode()
     */

    @Test
    public void testGetContext() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertThat(config.getContext().getPropertySources().size()).isEqualTo(snapshot.getContext().getPropertySources().size());
        assertThat(config.getContext().getPropertyConverters().size()).isEqualTo(snapshot.getContext().getPropertyConverters().size());
        assertThat(config.getContext().getPropertyFilters().size()).isEqualTo(snapshot.getContext().getPropertyFilters().size());
    }

    @Test
    public void testGet() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertThat("javaconf-value1").isEqualTo(snapshot.get("confkey1"));
        assertThat("javaconf-value2").isEqualTo(snapshot.get("confkey2"));
        assertThat("javaconf-value3").isEqualTo(snapshot.get("confkey3"));
        assertThat(snapshot.getOrDefault("confkey4", null)).isNull();
        assertThat("javaconf-value1").isEqualTo(snapshot.get("confkey1", String.class));
        assertThat("javaconf-value2").isEqualTo(snapshot.get("confkey2", String.class));
        assertThat("javaconf-value3").isEqualTo(snapshot.get("confkey3", String.class));
        assertThat(snapshot.getOrDefault("confkey4", String.class, null)).isNull();
        assertThat("javaconf-value1").isEqualTo(snapshot.get("confkey1", TypeLiteral.of(String.class)));
        assertThat("javaconf-value2").isEqualTo(snapshot.get("confkey2", TypeLiteral.of(String.class)));
        assertThat("javaconf-value3").isEqualTo(snapshot.get("confkey3", TypeLiteral.of(String.class)));
        assertThat((String) snapshot.getOrDefault("confkey4", TypeLiteral.of(String.class), null)).isNull();
    }

    @Test
    public void testGet_with_Subset() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1"));
        assertThat("javaconf-value1").isEqualTo(snapshot.get("confkey1"));
        assertThat(snapshot.getOrDefault("confkey2", null)).isNull();
        assertThat("javaconf-value1").isEqualTo(snapshot.get("confkey1", String.class));
        assertThat(snapshot.getOrDefault("confkey2", String.class, null)).isNull();
        assertThat("javaconf-value1").isEqualTo(snapshot.get("confkey1", TypeLiteral.of(String.class)));
        assertThat((String) snapshot.getOrDefault("confkey2", TypeLiteral.of(String.class), null)).isNull();
    }

    @Test
    public void testGet_with_MultiKey() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertThat("javaconf-value1").isEqualTo(snapshot.get(Arrays.asList("confkey1", "foo")));
        assertThat("javaconf-value2").isEqualTo(snapshot.get(Arrays.asList("foo", "confkey2")));
        assertThat("javaconf-value1").isEqualTo(snapshot.get(Arrays.asList("confkey1", "foo"), String.class));
        assertThat("javaconf-value2").isEqualTo(snapshot.get(Arrays.asList("foo", "confkey2"), String.class));
    }

    @Test
    public void testGetOrDefault() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertThat("javaconf-value1").isEqualTo(snapshot.getOrDefault("confkey1", "foo"));
        assertThat("javaconf-value2").isEqualTo(snapshot.getOrDefault("confkey2", "foo"));
        assertThat("javaconf-value2").isEqualTo(snapshot.getOrDefault("confkey2", String.class,"foo"));
        assertThat("javaconf-value3").isEqualTo(snapshot.getOrDefault("confkey3", "foo"));
        assertThat("foo").isEqualTo(snapshot.getOrDefault("confkey4", "foo"));
        assertThat("foo").isEqualTo(snapshot.getOrDefault("confkey4", String.class,"foo"));
    }

    @Test
    public void testGetProperties() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3", "missing"));
        Map<String, String> properties = snapshot.getProperties();
        assertThat(properties).isNotNull();
        assertThat(properties).hasSize(3);
        assertThat("javaconf-value1").isEqualTo(properties.get("confkey1"));
        assertThat("javaconf-value2").isEqualTo(properties.get("confkey2"));
        assertThat("javaconf-value3").isEqualTo(properties.get("confkey3"));
        assertThat(properties.get("confkey4")).isNull();
    }

    @Test
    public void testGetId() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot1 = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3", "foo"));
        ConfigurationSnapshot snapshot2 = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3", "foo"));
        assertThat(snapshot1).isNotEqualTo(snapshot2);
        assertThat(((DefaultConfigurationSnapshot) snapshot1).getId())
            .isNotEqualTo(((DefaultConfigurationSnapshot) snapshot2).getId());
    }

    @Test
    public void testGetOrDefault_withSubset() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1"));
        assertThat("javaconf-value1").isEqualTo(snapshot.getOrDefault("confkey1", "foo"));
        assertThat("foo").isEqualTo(snapshot.getOrDefault("confkey2", "foo"));
        assertThat("javaconf-value1").isEqualTo(snapshot.getOrDefault("confkey1", String.class,"foo"));
        assertThat("foo").isEqualTo(snapshot.getOrDefault("confkey2", String.class, "foo"));
    }

    @Test
    public void testGetOrDefault_with_MultiKey() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertThat("javaconf-value1").isEqualTo(snapshot.getOrDefault(Arrays.asList("confkey1", "foo"), "foo"));
        assertThat("foo").isEqualTo(snapshot.getOrDefault(Arrays.asList("confkeyNone", "foo"), "foo"));
        assertThat("javaconf-value1").isEqualTo(snapshot.getOrDefault(Arrays.asList("confkey1", "foo"), String.class, "foo"));
        assertThat("foo").isEqualTo(snapshot.getOrDefault(Arrays.asList("confkeyNone", "foo"), String.class, "foo"));
    }

    @Test
    public void testGetKeys() {
        Configuration config = Configuration.current();
        DefaultConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertThat(snapshot.getKeys().contains("confkey1")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey2")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey3")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey4")).isFalse();
        assertThat(snapshot.getKeys().contains("foo")).isFalse();
    }

    @Test
    public void testGetKeys_Subkeys() {
        Configuration config = Configuration.current();
        ConfigurationSnapshot snapshot = new DefaultConfigurationSnapshot(config,
                Arrays.asList("confkey1", "confkey2", "confkey3"));
        assertThat(snapshot.getKeys().contains("confkey1")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey2")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey3")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey4")).isFalse();
        assertThat(snapshot.getKeys().contains("foo")).isFalse();
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1", "confkey2"));
        assertThat(snapshot.getKeys().contains("confkey1")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey2")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey3")).isFalse();
        assertThat(snapshot.getKeys().contains("confkey4")).isFalse();
        assertThat(snapshot.getKeys().contains("foo")).isFalse();
        snapshot = snapshot.getSnapshot(Arrays.asList("confkey1", "foo"));
        assertThat(snapshot.getKeys().contains("confkey1")).isTrue();
        assertThat(snapshot.getKeys().contains("confkey2")).isFalse();
        assertThat(snapshot.getKeys().contains("confkey3")).isFalse();
        assertThat(snapshot.getKeys().contains("confkey4")).isFalse();
        assertThat(snapshot.getKeys().contains("foo")).isTrue();
    }

}
