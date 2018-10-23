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

import java.util.HashMap;
import java.util.Map;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author William.Lieurance 2018.02.17
 */
public class WrappedPropertySourceTest {

    /**
     * Test of of method, of class WrappedPropertySource.
     */
    @Test
    public void testOf_PropertySource() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource());
        assertThat(instance.getName()).isEqualTo("MockedWrappablePropertySource");
        WrappedPropertySource instance2 = WrappedPropertySource.of(instance);
        assertThat(instance2.getName()).isEqualTo("MockedWrappablePropertySource");
    }

    /**
     * Test of getOrdinal method, of class WrappedPropertySource.
     */
    @Test
    public void testOrdinalsAndOrdinalConstructors() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource(), null);
        assertThat(instance.getOrdinal()).isEqualTo(10);
        instance.setOrdinal(20);
        assertThat(instance.getOrdinal()).isEqualTo(20);
        
        WrappedPropertySource instance2 = WrappedPropertySource.of(instance, null);
        assertThat(instance2.getOrdinal()).isEqualTo(10);
    }

    /**
     * Test of setDelegate method, of class WrappedPropertySource.
     */
    @Test
    public void testGetSetDelegate() {
        MockedWrappablePropertySource first = new MockedWrappablePropertySource();
        first.setName("first");
        MockedWrappablePropertySource second = new MockedWrappablePropertySource();
        first.setName("second");
        
        WrappedPropertySource instance = WrappedPropertySource.of(first);
        assertThat(instance.getDelegate()).isEqualTo(first);
        instance.setDelegate(second);
        assertThat(instance.getDelegate()).isEqualTo(second);
        
    }


    /**
     * Test of current method, of class WrappedPropertySource.
     */
    @Test
    public void testGet() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource());
        PropertyValue result = instance.get("thisKey");
        assertThat(result.getValue()).isEqualTo("valueFromMockedWrappablePropertySource");
    }

    /**
     * Test of getProperties method, of class WrappedPropertySource.
     */
    @Test
    public void testGetProperties() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource());
        Map<String, PropertyValue> result = instance.getProperties();
        assertThat(result.containsKey("someKey")).isTrue();
        assertThat(result).hasSize(1);
    }

    /**
     * Test of isScannable method, of class WrappedPropertySource.
     */
    @Test
    public void testIsScannable() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource());
        assertThat(instance.isScannable()).isTrue();
    }

    @Test
    public void testEqualsAndHashAndToStringValues() {
        MockedWrappablePropertySource source1 = new MockedWrappablePropertySource();
        source1.setName("testEqualsName");
        MockedWrappablePropertySource source2 = new MockedWrappablePropertySource();
        source2.setName("testEqualsName");
        MockedWrappablePropertySource source3 = new MockedWrappablePropertySource();
        source3.setName("testNotEqualsName");
        
        WrappedPropertySource wps1 = WrappedPropertySource.of(source1);
        WrappedPropertySource wps2 = WrappedPropertySource.of(source2);
        WrappedPropertySource wps3 = WrappedPropertySource.of(source3);

        assertThat(wps1).isEqualTo(wps1);
        assertThat(wps1).isNotEqualTo(null);
        assertThat(source1).isNotEqualTo(wps1);
        assertThat(wps1).isNotEqualTo(source1);
        assertThat("aString").isNotEqualTo(wps1);
        assertThat(wps2).isEqualTo(wps1);
        assertThat(wps1).isNotEqualTo(wps3);
        assertThat(wps2.hashCode()).isEqualTo(wps1.hashCode());
        assertThat(wps1.hashCode()).isNotEqualTo(wps3.hashCode());
        assertThat(wps1.toString().contains("name=testEqualsName")).isTrue();
    }

    private class MockedWrappablePropertySource implements PropertySource{
        
        private String name = "MockedWrappablePropertySource";

        @Override
        public int getOrdinal() {
            return 10;
        }

        @Override
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public PropertyValue get(String key) {
            return PropertyValue.of(key, "valueFromMockedWrappablePropertySource", "MockedWrappablePropertySource");
        }

        @Override
        public Map<String, PropertyValue> getProperties() {
            Map<String, PropertyValue> returnable = new HashMap<>();
            returnable.put("someKey", this.get("someKey"));
            return returnable;
        }
        
        @Override
        public boolean isScannable(){
            return true;
        }
    
    }
}
