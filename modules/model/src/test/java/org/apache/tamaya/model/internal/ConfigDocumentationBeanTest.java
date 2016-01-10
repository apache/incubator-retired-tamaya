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
package org.apache.tamaya.model.internal;

import org.apache.tamaya.model.ModelType;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Anatole on 19.08.2015.
 */
public class ConfigDocumentationBeanTest {

    private final ConfigDocumentationBean mbean = new ConfigDocumentationBean();

    @Test
    public void testValidate_NoUnknowns() throws Exception {
        String results = mbean.validate(false);
        assertNotNull(results);
        assertFalse(results.trim().isEmpty());
        assertTrue(results.contains("\"type\":\"Parameter\""));
        assertTrue(results.contains("\"result\":\"MISSING\""));
        assertFalse(results.contains("\"description\":\"Undefined key: "));
        assertFalse(results.contains(" \"result\":\"UNDEFINED\""));
    }

    @Test
    public void testValidate_WithUnknowns() throws Exception {
        String results = mbean.validate(true);
        assertNotNull(results);
        assertFalse(results.trim().isEmpty());
        // test transitive excludes of default sys properties
        assertFalse(results.contains("\"name\":\"java"));
        assertFalse(results.contains("\"name\":\"sun."));
        assertFalse(results.contains("\"name\":\"file."));
        // test others
        assertTrue(results.contains("\"type\":\"Parameter\""));
        assertTrue(results.contains("\"type\":\"Section\""));
        assertTrue(results.contains("\"result\":\"MISSING\""));
        assertTrue(results.contains("\"description\":\"Undefined key: "));
        assertTrue(results.contains(" \"result\":\"UNDEFINED\""));
    }

    @Test
    public void testGetConfigurationModel() throws Exception {
        String results = mbean.getConfigurationModel();
        assertNotNull(results);
        assertFalse(results.trim().isEmpty());
        assertTrue(results.contains("\"type\":\"Parameter\""));
        assertTrue(results.contains("\"name\":\"MyNumber\""));
        assertTrue(results.contains("\"name\":\"a.b.c\""));
        assertTrue(results.contains("\"required\":true"));
    }

    @Test
    public void testGetConfigurationModel_WithSection() throws Exception {
        String results = mbean.getConfigurationModel(ModelType.Parameter);
        assertNotNull(results);
        assertFalse(results.trim().isEmpty());
        assertTrue(results.contains("\"type\":\"Parameter\""));
        assertFalse(results.contains("\"type\":\"Section\""));
        assertTrue(results.contains("\"required\":true"));
    }

    @Test
    public void testFindConfigurationModels() throws Exception {
        String results = mbean.findConfigurationModels("a");
        assertNotNull(results);
        assertFalse(results.trim().isEmpty());
        assertFalse(results.contains("\"type\":\"Parameter\""));
        assertTrue(results.contains("\"type\":\"Section\""));
    }

    @Test
    public void testFindValidationModels() throws Exception {
        String results = mbean.findValidationModels(ModelType.Section, "a");
        assertNotNull(results);
        assertFalse(results.trim().isEmpty());
        assertFalse(results.contains("\"type\":\"Parameter\""));
        assertTrue(results.contains("\"type\":\"Section\""));
        results = mbean.findValidationModels(ModelType.CombinationPolicy, "a");
        assertFalse(results.trim().isEmpty());
        assertFalse(results.contains("\"type\":\"Parameter\""));
        assertFalse(results.contains("\"type\":\"Section\""));
        System.out.println(results);
    }

    @Test
    public void testToString() throws Exception {
        String toString = mbean.toString();
        System.out.println(toString);
    }
}