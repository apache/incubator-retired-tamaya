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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by atsticks on 02.02.16.
 */
public class PropertyValueTest {

    @Test
    public void testGetKey() throws Exception {
        PropertyValue pv = PropertyValue.builder("k", "v").build();
        assertEquals("k", pv.getKey());
    }

    @Test
    public void testGetValue() throws Exception {
        PropertyValue pv = PropertyValue.builder("k", "v").build();
        assertEquals("v", pv.getValue());
    }

    @Test
    public void testGetContextData() throws Exception {
        PropertyValue pv = PropertyValue.builder("k", "v")
                .addContextData("k", "v2").build();
        assertEquals("v", pv.getValue());
        assertEquals("k", pv.getKey());
        assertEquals("v2", pv.getContextData().get("_k.k"));
    }

}