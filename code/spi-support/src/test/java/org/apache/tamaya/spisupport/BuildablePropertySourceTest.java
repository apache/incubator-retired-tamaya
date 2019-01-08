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
package org.apache.tamaya.spisupport;

import java.util.HashMap;
import java.util.Map;

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spisupport.propertysource.BuildablePropertySource;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class BuildablePropertySourceTest {
    @Test
    public void getOrdinal() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withOrdinal(55).build();
        assertThat(ps1.getOrdinal()).isEqualTo(55);
    }

    @Test
    public void getName() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withName("test1").build();
        assertThat(ps1.getName()).isEqualTo("test1");
        ps1 = BuildablePropertySource.builder().build();
        assertThat(ps1.getName()).isNotNull();
    }

    @Test
    public void get() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withSimpleProperty("a", "b").build();
        assertThat(ps1.get("a").getValue()).isEqualTo("b");
    }
    
    @Test
    public void getProperties() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withSimpleProperty("a", "b").build();
        assertThat(ps1.getProperties()).isNotNull();
        assertThat(ps1.getProperties()).hasSize(1);
        assertThat(ps1.getProperties().get("a").getValue()).isEqualTo("b");
    }
    
    @Test
    public void testScannable() {
        BuildablePropertySource bps = BuildablePropertySource.builder().build();
        assertThat(bps.isScannable()).isTrue();
    }
    
    @Test
    public void testSource() {
        BuildablePropertySource bps = BuildablePropertySource.builder()
                .withSource("fakeSource")
                .withSimpleProperty("defaultSourceKey", "defaultSourceValue")
                .withSimpleProperty("namedSourceKey", "namedSourceValue", "namedSource")
                .build();
        
        assertThat(bps.get("defaultSourceKey").getSource()).isEqualTo("fakeSource");
        assertThat(bps.get("namedSourceKey").getSource()).isEqualTo("namedSource");
    }
    
    @Test
    public void testWithMaps() {
        Map<String, String> propertyFirst = new HashMap<>();
        propertyFirst.put("firstKey", "firstValue");
        Map<String, String> propertySecond = new HashMap<>();
        propertySecond.put("secondKey", "secondValue");
        
        Map<String, PropertyValue> propertyThird = new HashMap<>();
        propertyThird.put("thirdKey", PropertyValue.of("thirdPVKey", "thirdValue", "thirdSource"));
        
        //This seems wrong
        BuildablePropertySource bps = BuildablePropertySource.builder()
                .withSimpleProperties(propertyFirst)
                .withProperties(propertySecond, "secondSource")
                .withProperties(propertyThird)
                .build();
        
        assertThat(bps.get("firstKey")).isNull();
        assertThat(bps.get("secondKey")).isNull();
        assertThat(bps.get("thirdKey").getValue()).isEqualTo("thirdValue");
        assertThat(bps.get("thirdKey").getSource()).isEqualTo("thirdSource");
        assertThat(bps.get("thirdPVKey")).isNull();
        
        bps = BuildablePropertySource.builder()
                .withProperties(propertyThird)
                .withSimpleProperties(propertyFirst)
                .withProperties(propertySecond, "secondSource")
                .build();
        
        assertThat(bps.get("firstKey").getValue()).isEqualTo("firstValue");
        assertThat(bps.get("secondKey").getSource()).isEqualTo("secondSource");
        assertThat(bps.get("secondKey").getValue()).isEqualTo("secondValue");
        assertThat(bps.get("thirdKey").getValue()).isEqualTo("thirdValue");
        assertThat(bps.get("thirdKey").getSource()).isEqualTo("thirdSource");
        assertThat(bps.get("thirdPVKey")).isNull();
    }

    @Test
    public void equals() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withName("test1").build();
        BuildablePropertySource ps2 = BuildablePropertySource.builder()
                .withName("test1").build();
        assertThat(ps2).isEqualTo(ps1);
        ps2 = BuildablePropertySource.builder()
                .withName("test2").build();
        assertThat(ps1).isNotEqualTo(ps2);
        assertThat(ps2).isNotEqualTo(null);
        assertThat(ps1).isNotEqualTo("aString");
    }

    @Test
    public void testHashCode() throws Exception {
        BuildablePropertySource ps1 = BuildablePropertySource.builder()
                .withName("test1").build();
        BuildablePropertySource ps2 = BuildablePropertySource.builder()
                .withName("test1").build();
        assertThat(ps2.hashCode()).isEqualTo(ps1.hashCode());
        ps2 = BuildablePropertySource.builder()
                .withName("test2").build();
        assertThat(ps1.hashCode()).isNotEqualTo(ps2.hashCode());
    }

    @Test
    public void builder() throws Exception {
        assertThat(BuildablePropertySource.builder()).isNotNull();
        assertThat(BuildablePropertySource.builder().but()).isNotNull();
        assertThat(BuildablePropertySource.builder()).isNotEqualTo(BuildablePropertySource.builder());
    }

    @Test
    public void testToString(){
        assertThat(BuildablePropertySource.builder().toString()).isNotNull();
        assertThat(BuildablePropertySource.builder().build().toString()).isNotNull();
    }

    @Test
    public void testGetChangeSupport(){
        assertThat(ChangeSupport.IMMUTABLE).isEqualTo(BuildablePropertySource.builder().build().getChangeSupport());
    }


}
