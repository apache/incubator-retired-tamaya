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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationSnapshot;
import org.apache.tamaya.spi.ConfigurationContext;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

public class DefaultConfigurationSnapshotTest {

    @Test
    public void getFrozenAtReturnsTheCorrectTimestamp() throws InterruptedException {
        Configuration source = Mockito.mock(Configuration.class);
        Mockito.when(source.getContext()).thenReturn(ConfigurationContext.EMPTY);
        Mockito.when(source.getSnapshot(Mockito.anyCollection())).thenReturn(ConfigurationSnapshot.EMPTY);
        Mockito.when(source.getSnapshot()).thenReturn(ConfigurationSnapshot.EMPTY);
        Mockito.when(source.getProperties()).thenReturn(Collections.emptyMap());

        long poiStart = System.nanoTime();
        Thread.sleep(10L);
        DefaultConfigurationSnapshot fc = new DefaultConfigurationSnapshot(source);
        Thread.sleep(10L);

        long poiEnd = System.nanoTime();

        assertTrue(fc.getTimestamp()>poiStart);
        assertTrue(fc.getTimestamp()<poiEnd);
    }


    @Test
    public void idMustBeNotNull() {
        Configuration source = Mockito.mock(Configuration.class);

        Mockito.when(source.getContext()).thenReturn(ConfigurationContext.EMPTY);
        Mockito.when(source.getProperties()).thenReturn(Collections.emptyMap());

        DefaultConfigurationSnapshot fc = new DefaultConfigurationSnapshot(source);

        assertNotNull(fc);
    }

    /*
     * All tests for equals() and hashCode() go here...
     */
    @Test
    public void twoFrozenAreDifferentIfTheyHaveADifferentIdAndFrozenAtTimestamp() {
        Map<String, String> properties = new HashMap<>();
        properties.put("key", "createValue");

        Configuration configuration = Mockito.mock(Configuration.class);
        Mockito.when(configuration.getContext()).thenReturn(ConfigurationContext.EMPTY);
        doReturn(properties).when(configuration).getProperties();

        DefaultConfigurationSnapshot fcA = new DefaultConfigurationSnapshot(configuration);
        DefaultConfigurationSnapshot fcB = new DefaultConfigurationSnapshot(configuration);

        assertNotEquals(fcA, fcB);
    }

    /*
     * END OF ALL TESTS for equals() and hashCode()
     */
}