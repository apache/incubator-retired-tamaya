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
package org.apache.tamaya.inject.internal;

import org.apache.tamaya.core.propertysource.SystemPropertySource;
import org.apache.tamaya.event.PropertyChangeSet;
import org.apache.tamaya.event.PropertyChangeSetBuilder;
import org.junit.Test;

import static org.junit.Assert.*;

public class WeakConfigListenerManagerTest {

    private PropertyChangeSetListener consumer = new PropertyChangeSetListener() {
        @Override
        public void propertyChange(PropertyChangeSet set) {
            loggedSet = set;
        }
    };
    private PropertyChangeSet loggedSet;

    @Test
    public void testOf() throws Exception {
        assertNotNull(WeakConfigListenerManager.of());
    }

    @Test
    public void testRegisterConsumer() throws Exception {
        SystemPropertySource sysSrc = new SystemPropertySource();
        WeakConfigListenerManager.of().registerConsumer(this, new PropertyChangeSetListener() {
            @Override
            public void propertyChange(PropertyChangeSet set) {
                loggedSet = set;
            }
        });
        PropertyChangeSet change = PropertyChangeSetBuilder.of(sysSrc).add("aaa", "aaaValue").build();
        WeakConfigListenerManager.of().registerConsumer(this, consumer);
        WeakConfigListenerManager.of().publishChangeEvent(change);
        assertNotNull(loggedSet);
        loggedSet = null;
        WeakConfigListenerManager.of().unregisterConsumer(consumer);
    }

    @Test
    public void testUnregisterConsumer() throws Exception {
        SystemPropertySource sysSrc = new SystemPropertySource();
        PropertyChangeSet change = PropertyChangeSetBuilder.of(sysSrc).add("aaa", "aaaValue").build();
        PropertyChangeSetListener tempConsumer = new PropertyChangeSetListener() {
            @Override
            public void propertyChange(PropertyChangeSet set) {
                loggedSet = set;
            }
        };
        WeakConfigListenerManager.of().registerConsumer(this, tempConsumer);
        WeakConfigListenerManager.of().publishChangeEvent(change);
        assertNotNull(loggedSet);
        loggedSet = null;
        WeakConfigListenerManager.of().unregisterConsumer(this);
        assertNull(loggedSet);
        WeakConfigListenerManager.of().publishChangeEvent(change);
        assertNull(loggedSet);
    }

    @Test
    public void testPublishChangeEvent() throws Exception {
        SystemPropertySource sysSrc = new SystemPropertySource();
        WeakConfigListenerManager.of().publishChangeEvent(
                PropertyChangeSetBuilder.of(sysSrc).add("aaa", "aaaValue").build());
    }

    @Test
    public void testToString() throws Exception {
        assertNotNull(WeakConfigListenerManager.of());
    }
}