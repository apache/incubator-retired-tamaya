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
import org.apache.tamaya.spi.PropertyValue;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author William.Lieurance 2018-02-10
 */
public class DefaultConfigValueEvaluatorTest {


    /**
     * Test of evaluateRawValue method, of class DefaultConfigValueEvaluator.
     */
    @Test
    public void testEvaluteRawValue() {
        Configuration config = Configuration.current();
        DefaultConfigValueEvaluator instance = new DefaultConfigValueEvaluator();
        PropertyValue result = instance.evaluateRawValue("confkey1", config.getContext());
        assertThat(result.getValue()).isEqualTo("javaconf-value1");
        result = instance.evaluateRawValue("missing", config.getContext());
        assertThat(result).isNull();
    }

    /**
     * Test of evaluateRawValues method, of class DefaultConfigValueEvaluator.
     */
    @Test
    public void testEvaluateRawValues() {
        Configuration config = Configuration.current();
        DefaultConfigValueEvaluator instance = new DefaultConfigValueEvaluator();
        Map<String, PropertyValue> result = instance.evaluateRawValues(config.getContext());
        assertThat(result.containsKey("confkey1")).isTrue();
        assertThat(result.get("confkey1").getValue()).isEqualTo("javaconf-value1");
    }

    @Test
    public void testToString(){
        assertNotNull(new DefaultConfigValueEvaluator().toString());
    }
    
}
