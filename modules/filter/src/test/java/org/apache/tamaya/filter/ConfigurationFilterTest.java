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
package org.apache.tamaya.filter;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.FilterContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ConfigurationFilter}. Created by atsticks on 11.02.16.
 */
public class ConfigurationFilterTest {

    @Test
    public void testMetadataFiltered() throws Exception {
        ConfigurationFilter.setMetadataFiltered(true);
        assertTrue(ConfigurationFilter.isMetadataFiltered());
        ConfigurationFilter.setMetadataFiltered(false);
        assertFalse(ConfigurationFilter.isMetadataFiltered());
    }

    @Test
    public void testGetSingleFilters() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        assertNotNull(ConfigurationFilter.getSingleFilters());
        PropertyFilter testFilter = new PropertyFilter() {
            @Override
            public String filterProperty(String value, FilterContext context) {
                return context.getKey() + ":testGetSingleFilters";
            }
        };
        ConfigurationFilter.getSingleFilters().addFilter(testFilter);
        assertEquals("user.home:testGetSingleFilters", config.get("user.home"));
        ConfigurationFilter.getSingleFilters().removeFilter(testFilter);
        assertNotSame("user.home:testGetSingleFilters", config.get("user.home"));
    }

    @Test
    public void testRemoveSingleFiltersAt0() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        assertNotNull(ConfigurationFilter.getSingleFilters());
        PropertyFilter testFilter = new PropertyFilter() {
            @Override
            public String filterProperty(String value, FilterContext context) {
                return context.getKey() + ":testGetSingleFilters";
            }
        };
        ConfigurationFilter.getSingleFilters().addFilter(testFilter);
        assertEquals("user.home:testGetSingleFilters", config.get("user.home"));
        ConfigurationFilter.getSingleFilters().removeFilter(0);
        assertNotSame("user.home:testGetSingleFilters", config.get("user.home"));
    }

    @Test
    public void testGetMapFilters() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        assertNotNull(ConfigurationFilter.getMapFilters());
        PropertyFilter testFilter = new PropertyFilter() {
            @Override
            public String filterProperty(String value, FilterContext context) {
                return context.getKey() + ":testGetMapFilters";
            }
        };
        ConfigurationFilter.getMapFilters().addFilter(testFilter);
        assertEquals("user.home:testGetMapFilters", config.getProperties().get("user.home"));
        ConfigurationFilter.getSingleFilters().removeFilter(testFilter);
        assertNotSame("user.home:testGetSingleFilters", config.getProperties().get("user.home"));
    }

    @Test
    public void testRemoveMapFilterAt0() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        assertNotNull(ConfigurationFilter.getMapFilters());
        PropertyFilter testFilter = new PropertyFilter() {
            @Override
            public String filterProperty(String value, FilterContext context) {
                return context.getKey() + ":testGetMapFilters";
            }
        };
        ConfigurationFilter.getMapFilters().addFilter(testFilter);
        assertEquals("user.home:testGetMapFilters", config.getProperties().get("user.home"));
        ConfigurationFilter.getMapFilters().removeFilter(0);
        assertNotSame("user.home:testGetSingleFilters", config.getProperties().get("user.home"));
    }

    @Test
    public void testClearFilters() throws Exception {
        Configuration config = ConfigurationProvider.getConfiguration();
        assertNotNull(ConfigurationFilter.getSingleFilters());
        PropertyFilter testFilter = new PropertyFilter() {
            @Override
            public String filterProperty(String value, FilterContext context) {
                return context.getKey() + ":testGetSingleFilters";
            }
        };
        ConfigurationFilter.getSingleFilters().addFilter(testFilter);
        assertEquals("user.home:testGetSingleFilters", config.get("user.home"));
        ConfigurationFilter.clearFilters();
        assertNotSame("user.home:testGetSingleFilters", config.get("user.home"));
    }

}