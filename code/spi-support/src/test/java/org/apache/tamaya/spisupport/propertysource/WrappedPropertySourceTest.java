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
import static org.junit.Assert.*;

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
        assertEquals("MockedWrappablePropertySource", instance.getName());
        WrappedPropertySource instance2 = WrappedPropertySource.of(instance);
        assertEquals("MockedWrappablePropertySource", instance2.getName());
    }

    /**
     * Test of getOrdinal method, of class WrappedPropertySource.
     */
    @Test
    public void testOrdinalsAndOrdinalConstructors() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource(), null);
        assertEquals(10, instance.getOrdinal());
        instance.setOrdinal(20);
        assertEquals(20, instance.getOrdinal());
        
        WrappedPropertySource instance2 = WrappedPropertySource.of(instance, null);
        assertEquals(10, instance2.getOrdinal());
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
        assertEquals(first, instance.getDelegate());
        instance.setDelegate(second);
        assertEquals(second, instance.getDelegate());
        
    }


    /**
     * Test of get method, of class WrappedPropertySource.
     */
    @Test
    public void testGet() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource());
        PropertyValue result = instance.get("thisKey");
        assertEquals("valueFromMockedWrappablePropertySource", result.getValue());
    }

    /**
     * Test of getProperties method, of class WrappedPropertySource.
     */
    @Test
    public void testGetProperties() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource());
        Map<String, PropertyValue> result = instance.getProperties();
        assertTrue(result.containsKey("someKey"));
        assertEquals(1, result.size());
    }

    /**
     * Test of isScannable method, of class WrappedPropertySource.
     */
    @Test
    public void testIsScannable() {
        WrappedPropertySource instance = WrappedPropertySource.of(new MockedWrappablePropertySource());
        assertTrue(instance.isScannable());
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

        assertEquals(wps1, wps1);
        assertNotEquals(null, wps1);
        assertNotEquals(source1, wps1);
        assertNotEquals(wps1, source1);
        assertNotEquals("aString", wps1);
        assertEquals(wps1, wps2);
        assertNotEquals(wps1, wps3);
        assertEquals(wps1.hashCode(), wps2.hashCode());
        assertNotEquals(wps1.hashCode(), wps3.hashCode());
        assertTrue(wps1.toString().contains("name=testEqualsName"));
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
