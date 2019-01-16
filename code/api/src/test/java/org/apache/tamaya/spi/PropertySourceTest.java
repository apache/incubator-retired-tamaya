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

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class PropertySourceTest {


    /**
     * Test of EMPTY instance of PropertySource
     */
    @Test
    public void testEmptySource() {
        PropertySource instance = PropertySource.EMPTY;
        assertThat(instance.getOrdinal()).isEqualTo(Integer.MIN_VALUE);
        assertThat(instance.getName()).isEqualTo("<empty>");
        assertThat(instance.get("key")).isNull();
        assertThat(instance.getProperties().isEmpty()).isTrue();
        assertThat(instance.toString()).isEqualTo("PropertySource.EMPTY");

    }

    @Test
    public void getOrdinal(){
        assertThat(0).isEqualTo(new PropertySourceImpl().getOrdinal());
        PropertySourceImpl ps = new PropertySourceImpl();
        ps.value = PropertyValue.createValue(PropertySource.TAMAYA_ORDINAL, "123");
        assertThat(123).isEqualTo(ps.getOrdinal());
        ps.value = PropertyValue.createValue(PropertySource.TAMAYA_ORDINAL, "abc");
        assertThat(0).isEqualTo(ps.getOrdinal());
    }

    @Test
    public void getVersion(){
        assertThat("N/A").isEqualTo(new PropertySourceImpl().getVersion());
    }

    @Test
    public void addChangeListener(){
        BiConsumer<Set<String>,PropertySource> l = mock(BiConsumer.class);
        new PropertySourceImpl().addChangeListener(l);
    }

    @Test
    public void removeChangeListener(){
        BiConsumer<Set<String>,PropertySource> l = mock(BiConsumer.class);
        new PropertySourceImpl().removeChangeListener(l);
    }

    @Test
    public void removeAllChangeListeners(){
        new PropertySourceImpl().removeAllChangeListeners();
    }

    @Test
    public void isScannable() {
        assertThat(new PropertySourceImpl().isScannable()).isTrue();
    }

    @Test
    public void getChangeSupport() {
        assertThat(ChangeSupport.UNSUPPORTED).isEqualTo(new PropertySourceImpl().getChangeSupport());
    }

    public class PropertySourceImpl implements PropertySource {

        PropertyValue value;

        public String getName() {
            return "";
        }

        public PropertyValue get(String key) {
            return value;
        }

        public Map<String, PropertyValue> getProperties() {
            return Collections.emptyMap();
        }
    }
    
}
