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

import java.util.Map;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author William.Lieurance 2018-02-10
 */
public class DefaultConfigValueEvaluatorTest {


    /**
     * Test of evaluteRawValue method, of class DefaultConfigValueEvaluator.
     */
    @Test
    public void testEvaluteRawValue() {
        Configuration config = ConfigurationProvider.getConfiguration();
        DefaultConfigValueEvaluator instance = new DefaultConfigValueEvaluator();
        PropertyValue result = instance.evaluteRawValue("confkey1", config.getContext());
        assertEquals("javaconf-value1", result.getValue());
        result = instance.evaluteRawValue("missing", config.getContext());
        assertNull(result);
    }

    /**
     * Test of evaluateRawValues method, of class DefaultConfigValueEvaluator.
     */
    @Test
    public void testEvaluateRawValues() {
        Configuration config = ConfigurationProvider.getConfiguration();
        DefaultConfigValueEvaluator instance = new DefaultConfigValueEvaluator();
        Map<String, PropertyValue> result = instance.evaluateRawValues(config.getContext());
        assertTrue(result.containsKey("confkey1"));
        assertEquals("javaconf-value1", result.get("confkey1").getValue());
    }

    
}
