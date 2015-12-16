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

import org.apache.tamaya.events.ConfigEvent;
import org.apache.tamaya.events.ConfigEventListener;
import org.apache.tamaya.events.SimpleEvent;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DefaultConfigEventManagerSpi}.
 */
public class DefaultConfigEventManagerSpiTest {

    private DefaultConfigEventManagerSpi spi = new DefaultConfigEventManagerSpi();
    private Object testAddListenerValue;

    @Test
    public void testAddListener() throws Exception {
        ConfigEventListener testListener = new ConfigEventListener() {
            @Override
            public void onConfigEvent(ConfigEvent<?> event) {
                testAddListenerValue = event.getResource();
            }
        };
        spi.addListener(testListener);
        spi.fireEvent(new SimpleEvent("Event1"));
        assertEquals(testAddListenerValue, "Event1");
        spi.removeListener(testListener);
        spi.fireEvent(new SimpleEvent("Event2"));
        assertEquals(testAddListenerValue, "Event1");

    }

    @Test
    public void testRemoveListener() throws Exception {
        ConfigEventListener testListener = new ConfigEventListener() {

            @Override
            public void onConfigEvent(ConfigEvent<?> event) {
                testAddListenerValue = event;
            }
        };
        spi.removeListener(testListener);
        spi.removeListener(testListener);
    }

}