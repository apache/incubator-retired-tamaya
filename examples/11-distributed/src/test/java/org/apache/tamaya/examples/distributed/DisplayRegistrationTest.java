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

package org.apache.tamaya.examples.distributed;

import io.vertx.core.json.Json;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by atsticks on 13.11.16.
 */
public class DisplayRegistrationTest {
    @Test
    public void getDisplayModel() throws Exception {

    }

    @Test
    public void getDisplayName() throws Exception {

    }

    @Test
    public void getHost() throws Exception {

    }

    @Test
    public void getId() throws Exception {

    }

    @Test
    public void testEquals() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }

    @org.junit.Test
    public void testJson() throws Exception {
        DisplayRegistration reg = new DisplayRegistration("myDisplay", "VT100");
        String val = Json.encode(reg);
        DisplayRegistration decoded = Json.decodeValue(val, DisplayRegistration.class);
        assertNotNull(decoded);
        assertEquals(reg, decoded);
    }

}