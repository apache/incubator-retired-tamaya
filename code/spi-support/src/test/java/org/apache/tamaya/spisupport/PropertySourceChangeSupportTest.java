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

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spisupport.propertysource.BuildablePropertySource;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Tests for {@link java.beans.PropertyChangeSupport}.
 */
public class PropertySourceChangeSupportTest {

    @Test
    public void getChangeSupport() {
        PropertySource ps = BuildablePropertySource.builder().withName("test").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.IMMUTABLE, ps);
        assertThat(ChangeSupport.IMMUTABLE).isEqualTo(support.getChangeSupport());
        support = new PropertySourceChangeSupport(ChangeSupport.UNSUPPORTED, ps);
        assertThat(ChangeSupport.UNSUPPORTED).isEqualTo(support.getChangeSupport());
    }

    @Test
    public void getVersion_Immutable() {
        PropertySource ps = BuildablePropertySource.builder().withName("test").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.IMMUTABLE, ps);
        support.load(ps.getProperties());
        String v0 = support.getVersion();
        assertThat(v0).isNotNull();
        String v1 = support.getVersion();
        support.load(Collections.emptyMap());
        String v2 = support.getVersion();
        assertThat(v0).isEqualTo(v1);
        assertThat(v1).isEqualTo(v2);
    }

    @Test
    public void getVersion() {
        PropertySource ps = BuildablePropertySource.builder().withSimpleProperty("foo", "bar").withName("test").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.SUPPORTED, ps);
        support.load(ps.getProperties());
        String v0 = support.getVersion();
        assertThat(v0).isNotNull();
        String v1 = support.getVersion();
        support.load(Collections.emptyMap());
        String v2 = support.getVersion();
        assertThat(v0).isEqualTo(v1);
        assertThat(v1).isNotEqualTo(v2);
    }

    @Test
    public void addChangeListener() {
        PropertySource ps = BuildablePropertySource.builder().withName("test").
                withSimpleProperty("foo", "bar").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.SUPPORTED, ps);
        support.load(ps.getProperties());
        BiConsumer<Set<String>, PropertySource> l = mock(BiConsumer.class);
        support.addChangeListener(l);
        support.load(Collections.emptyMap());
        verify(l).accept(any(), any());
    }

    @Test
    public void removeChangeListener() {
        PropertySource ps = BuildablePropertySource.builder().withName("test").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.SUPPORTED, ps);
        BiConsumer<Set<String>, PropertySource> l = mock(BiConsumer.class);
        support.addChangeListener(l);
        support.removeChangeListener(l);
        support.load(Collections.emptyMap());
        verifyNoMoreInteractions(l);
    }

    @Test
    public void removeAllChangeListeners() {
        PropertySource ps = BuildablePropertySource.builder().withName("test").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.IMMUTABLE, ps);
        BiConsumer<Set<String>, PropertySource> l = mock(BiConsumer.class);
        support.addChangeListener(l);
        support.removeAllChangeListeners();
        support.load(Collections.emptyMap());
        verifyNoMoreInteractions(l);
    }

    @Test
    public void update() {
        PropertySource ps = BuildablePropertySource.builder().withName("test").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.IMMUTABLE, ps);
        assertThatCode(() -> support.load(Collections.emptyMap())).doesNotThrowAnyException();
    }

    @Test
    public void getValue() {
        PropertySource ps = BuildablePropertySource.builder().withSimpleProperty("foo", "bar").withName("test")
                .withSimpleProperty("foo", "bar").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.IMMUTABLE, ps);
        support.load(ps.getProperties());
        assertThat(support.getValue("foo")).isNotNull();
        assertThat(support.getValue("bar")).isNull();
    }

    @Test
    public void getProperties() {
        PropertySource ps = BuildablePropertySource.builder().withName("test")
                .withSimpleProperty("foo", "bar").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.IMMUTABLE, ps);
        Map properties = support.getProperties();
        assertThat(properties).isNotNull();
        assertThat(properties).isEmpty();
        support.load(ps.getProperties());
        properties = support.getProperties();
        assertThat(properties).isNotNull();
        assertThat(properties).hasSize(1);
    }

    @Test
    public void scheduleChangeMonitor() throws InterruptedException {
        PropertySource ps = BuildablePropertySource.builder().withName("test")
                .withSimpleProperty("foo", "bar").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.SUPPORTED, ps);
        support.load(ps.getProperties());
        String v1 = support.getVersion();
        support.scheduleChangeMonitor(() -> Collections.emptyMap(), 500, TimeUnit.MILLISECONDS);
        Thread.sleep(600L);
        String v2 = support.getVersion();
        assertThat(v1).isNotEqualTo(v2);
    }

    @Test
    public void cancelSchedule() throws InterruptedException {
        PropertySource ps = BuildablePropertySource.builder().withName("test")
                .withSimpleProperty("foo", "bar").build();
        PropertySourceChangeSupport support = new PropertySourceChangeSupport(ChangeSupport.IMMUTABLE, ps);
        support.load(ps.getProperties());
        String v1 = support.getVersion();
        support.scheduleChangeMonitor(() -> Collections.emptyMap(), 1, TimeUnit.SECONDS);
        Thread.sleep(500L);
        support.cancelSchedule();
        String v2 = support.getVersion();
        assertThat(v1).isEqualTo(v2);
    }
}
