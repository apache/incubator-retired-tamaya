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
package org.apache.tamaya.spisupport.propertysource;

import java.io.File;
import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spisupport.propertysource.SimplePropertySource.Builder;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class SimplePropertySourceTest {

    @Test
    public void successfulCreationWithPropertiesFromXMLPropertiesFile() throws URISyntaxException {
        URL resource = getClass().getResource("/valid-properties.xml");
        File resourceFile = new File(resource.toURI());
        SimplePropertySource source = new SimplePropertySource(resourceFile);

        assertThat(source).isNotNull();
        assertThat(source.getProperties()).hasSize(2); // double the getNumChilds for .source values.
        assertThat(source.getProperties()).contains(entry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(source.getProperties()).contains(entry("b", PropertyValue.of("b", "1", resource.toString())));
    }

    @Test(expected=ConfigException.class)
    public void successfulCreationWithPropertiesFromInvalidsFile() throws URISyntaxException {
        File resourceFile = new File("fooe.file");
        new SimplePropertySource(resourceFile);
    }

    @Test
    public void successfulCreationWithPropertiesFromURL() throws URISyntaxException {
        URL resource = getClass().getResource("/valid-properties.xml");
        SimplePropertySource source = new SimplePropertySource(resource);

        assertThat(source).isNotNull();
        assertThat(source.getProperties()).hasSize(2); // double the getNumChilds for .source values.
        assertThat(source.getProperties()).contains(entry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(source.getProperties()).contains(entry("b", PropertyValue.of("b", "1", resource.toString())));
    }

    @Test
    public void successfulCreationWithPropertiesFromXMLPropertiesResource() {
        URL resource = getClass().getResource("/valid-properties.xml");

        SimplePropertySource source = new SimplePropertySource(resource);

        assertThat(source).isNotNull();
        assertThat(source.getProperties()).hasSize(2); // double the getNumChilds for .source values.
        assertThat(source.getProperties()).contains(entry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(source.getProperties()).contains(entry("b", PropertyValue.of("b", "1", resource.toString())));
    }

    @Test
    public void successfulCreationWithProperties() {
        URL resource = getClass().getResource("/valid-properties.xml");
        Map<String,String> props = new HashMap<>();
        props.put("a", "b");
        props.put("b", "1");
        SimplePropertySource source = new SimplePropertySource("test", props);

        assertThat(source).isNotNull();
        assertThat(source.getProperties()).hasSize(2); // double the getNumChilds for .source values.
        assertThat(source.getProperties()).contains(entry("a", PropertyValue.of("a", "b", "test")));
        assertThat(source.getProperties()).contains(entry("b", PropertyValue.of("b", "1", "test")));
    }

    @Test
    public void getChangeSupport(){
        URL resource = getClass().getResource("/valid-properties.xml");
        SimplePropertySource source = new SimplePropertySource(resource);
        assertThat(ChangeSupport.IMMUTABLE).isEqualTo(source.getChangeSupport());
    }

    @Test
    public void failsToCreateFromNonXMLPropertiesXMLFile() {
        URL resource = getClass().getResource("/non-xml-properties.xml");
        ConfigException catchedException = null;

        try {
            new SimplePropertySource(resource);
        } catch (ConfigException ce) {
            catchedException = ce;
        }

        assertThat(catchedException.getMessage())
                .startsWith("Error loading properties from")
                .endsWith("non-xml-properties.xml");
    }

    @Test
    public void failsToCreateFromInvalidPropertiesXMLFile() {
        URL resource = getClass().getResource("/invalid-properties.xml");
        ConfigException catchedException = null;

        try {
            new SimplePropertySource(resource);
        } catch (ConfigException ce) {
            catchedException = ce;
        }

        assertThat(catchedException.getMessage())
                .startsWith("Error loading properties from")
                .endsWith("invalid-properties.xml");
    }

    @Test
    public void successfulCreationWithPropertiesFromSimplePropertiesFile() {
        URL resource = getClass().getResource("/testfile.properties");

        SimplePropertySource source = new SimplePropertySource(resource);

        assertThat(source).isNotNull();
        assertThat(source.getProperties()).hasSize(5); // double the getNumChilds for .source values.
    }

    @Test
    public void testWithMap() {
        Map<String, String> propertyFirst = new HashMap<>();
        propertyFirst.put("firstKey", "firstValue");

        SimplePropertySource source = new SimplePropertySource("testWithMap", propertyFirst, 166);
        assertThat(source.getName()).isEqualTo("testWithMap");
        assertThat(source.getDefaultOrdinal()).isEqualTo(166);
        assertThat(source.getProperties()).containsKey("firstKey");

    }

    @Test
    public void builder() throws Exception {
        assertThat(SimplePropertySource.newBuilder()).isNotNull();
        assertThat(SimplePropertySource.newBuilder()).isNotEqualTo(SimplePropertySource.newBuilder());
    }

    @Test
    public void getOrdinal() throws Exception {
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName()
                .withOrdinal(55)
                .withDefaultOrdinal(166)
                .build();

        assertThat(ps1.getOrdinal()).isEqualTo(55);
        assertThat(ps1.getDefaultOrdinal()).isEqualTo(166);
    }

    @Test
    public void getName() throws Exception {
        //SimplePropertySource ps1 = SimplePropertySource.newBuilder()
        //        .withName("test1")
        //        .build();
        //assertThat(ps1.getName()).isEqualTo("test1");
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName().build();
        assertThat(UUID.fromString(ps1.getName())).isNotNull();
    }

    @Test
    public void get() throws Exception {
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperty("a", "b").build();
        assertThat(ps1.get("a").getValue()).isEqualTo("b");
    }

    @Test
    public void getProperties() throws Exception {
        SimplePropertySource ps1 = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperty("a", "b")
                .build();
        assertThat(ps1.getProperties()).isNotNull();
        assertThat(ps1.getProperties()).hasSize(1);
        assertThat(ps1.getProperties().get("a").getValue()).isEqualTo("b");
    }

    @Test
    public void testScannable() {
        SimplePropertySource sps = SimplePropertySource.newBuilder().withUuidName().build();
        assertThat(sps.isScannable()).isTrue();
    }

    @Test
    public void testBuilderWithMaps() {
        URL resource = getClass().getResource("/valid-properties.xml");
        File resourceAsFile = new File(resource.getPath());

        Map<String, String> propertyFirst = new HashMap<>();
        propertyFirst.put("firstKey", "firstValue");

        SimplePropertySource sps = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperties(propertyFirst)
                .withProperties(resource)
                .build();

        assertThat(sps.get("firstKey").getValue()).isEqualTo("firstValue");
        assertThat(sps.getProperties()).contains(entry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(sps.getProperties()).contains(entry("b", PropertyValue.of("b", "1", resource.toString())));

        sps = SimplePropertySource.newBuilder()
                .withUuidName()
                .withProperties(propertyFirst)
                .withProperties(resourceAsFile)
                .build();

        assertThat(sps.get("firstKey").getValue()).isEqualTo("firstValue");
        assertThat(sps.getProperties()).contains(entry("a", PropertyValue.of("a", "b", resource.toString())));
        assertThat(sps.getProperties()).contains(entry("b", PropertyValue.of("b", "1", resource.toString())));
    }
    
    @Test
    public void buildingWithValidName() {
    	final String KEY = "myTestKey"; 
    	Builder builder = SimplePropertySource.newBuilder().withName(KEY);
    	assertThat(builder.build().getName()).isEqualTo(KEY);
    }
    
    @Test(expected = NullPointerException.class)
    public void buildingWithInvalidNameYieldsNPE() {
    	SimplePropertySource.newBuilder().withName(null);
    }
    
}
