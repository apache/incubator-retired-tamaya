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
package org.apache.tamaya.optional;

import static org.junit.Assert.*;

/**
 * Created by Anatole on 07.09.2015.
 */
public class OptionalConfigurationTest {

    @org.junit.Test
    public void testOf_OTHER_OVERRIDES_TAMAYA() throws Exception {
        OptionalConfiguration cfg = OptionalConfiguration.of(EvaluationPolicy.OTHER_OVERRIDES_TAMAYA, new ValueProvider() {
            @Override
            public <T> T get(String key, Class<T> type) {
                return (T)"result";
            }
        });
        assertNotNull(cfg);
        assertEquals(cfg.get("sdkjsdkjsdkjhskjdh"), "result");
        assertEquals(cfg.get("sdkjsdkjsdsdsdkjhskjdh", String.class), "result");
        assertEquals(cfg.get("java.version", String.class), "result");
    }

    @org.junit.Test
    public void testOf_TAMAYA_OVERRIDES_OTHER() throws Exception {
        OptionalConfiguration cfg = OptionalConfiguration.of(EvaluationPolicy.TAMAYA_OVERRIDES_OTHER, new ValueProvider() {
            @Override
            public <T> T get(String key, Class<T> type) {
                return (T)"result";
            }
        });
        assertNotNull(cfg);
        assertEquals(cfg.get("sdkjsdkjsdkjhskjdh"), "result");
        assertEquals(cfg.get("sdkjsdkjsdsdsdkjhskjdh", String.class), "result");
        assertEquals(cfg.get("java.version", String.class), System.getProperty("java.version"));
    }

    @org.junit.Test(expected = IllegalStateException.class)
    public void testOf_THROWS_EXCEPTION() throws Exception {
        OptionalConfiguration cfg = OptionalConfiguration.of(EvaluationPolicy.THROWS_EXCEPTION, new ValueProvider() {
            @Override
            public <T> T get(String key, Class<T> type) {
                if("java.version".equals(key)){
                    return (T)System.getProperty(key);
                }
                return (T)"result";
            }
        });
        assertNotNull(cfg);
        assertEquals(cfg.get("sdkjsdkjsdkjhskjdh"), "result");
        assertEquals(cfg.get("sdkjsdkjsdsdsdkjhskjdh", String.class), "result");
        assertEquals(cfg.get("java.version", String.class), System.getProperty("java.version"));
        assertEquals(cfg.get("java.version", String.class), "dfdf");
    }

    @org.junit.Test
    public void testOf_NOPROV_THROWS_EXCEPTION() throws Exception {
        OptionalConfiguration cfg = OptionalConfiguration.of(EvaluationPolicy.THROWS_EXCEPTION);
        assertNotNull(cfg);
        assertNull(cfg.get("sdkjsdkjsdkjhskjdh"));
        assertEquals(cfg.get("java.version"), System.getProperty("java.version"));
        assertEquals(cfg.get("java.version", String.class), System.getProperty("java.version"));
    }

    @org.junit.Test
    public void testOf_NOPROV_TAMAYA_OVERRIDES_OTHER() throws Exception {
        OptionalConfiguration cfg = OptionalConfiguration.of(EvaluationPolicy.TAMAYA_OVERRIDES_OTHER);
        assertNotNull(cfg);
        assertNull(cfg.get("sdkjsdkjsdkjhskjdh"));
        assertEquals(cfg.get("java.version"), System.getProperty("java.version"));
        assertEquals(cfg.get("java.version", String.class), System.getProperty("java.version"));
    }

    @org.junit.Test
    public void testOf_NOPROV_OTHER_OVERRIDES_TAMAYA() throws Exception {
        OptionalConfiguration cfg = OptionalConfiguration.of(EvaluationPolicy.OTHER_OVERRIDES_TAMAYA);
        assertNotNull(cfg);
        assertNull(cfg.get("sdkjsdkjsdkjhskjdh"));
        assertEquals(cfg.get("java.version"), System.getProperty("java.version"));
        assertEquals(cfg.get("java.version", String.class), System.getProperty("java.version"));
    }

}