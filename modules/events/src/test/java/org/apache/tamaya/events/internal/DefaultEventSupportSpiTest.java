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
package org.apache.tamaya.events.internal;

import org.apache.tamaya.events.Listener;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link org.apache.tamaya.events.internal.DefaultEventSupportSpi}.
 */
public class DefaultEventSupportSpiTest {

    private DefaultEventSupportSpi spi = new DefaultEventSupportSpi();
    private String testAddListenerValue;

    @Test
    public void testAddListener() throws Exception {
        Listener<String> testListener = new Listener<String>() {

            @Override
            public void onEvent(String event) {
                testAddListenerValue = event;
            }
        };
        spi.addListener(testListener);
        spi.fireEvent("Event1", String.class);
        assertEquals(testAddListenerValue, "Event1");
        spi.removeListener(testListener);
        spi.fireEvent("Event2", String.class);
        assertEquals(testAddListenerValue, "Event1");

    }

    @Test
    public void testRemoveListener() throws Exception {
        Listener<String> testListener = new Listener<String>() {

            @Override
            public void onEvent(String event) {
                testAddListenerValue = event;
            }
        };
        spi.removeListener(testListener);
        spi.removeListener(testListener);
    }

}