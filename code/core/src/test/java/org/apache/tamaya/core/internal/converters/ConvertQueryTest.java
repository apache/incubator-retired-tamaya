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
package org.apache.tamaya.core.internal.converters;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author William.Lieurance 2018-02-10
 */
public class ConvertQueryTest {

    /**
     * Test of query method, of class ConvertQuery.
     */
    @Test
    public void testIntegerQuery() {
        Configuration config = Configuration.current();
        ConvertQuery<Integer> converter = new ConvertQuery<>("101", TypeLiteral.of(Integer.class));
        Integer result = converter.apply(config);
        assertThat(result.longValue()).isEqualTo(101);
    }

    /**
     * Test of query method, of class ConvertQuery.
     */
    @Test
    public void testConfigUsingIntegerQuery() {
        Configuration config = Configuration.current();
        ConvertQuery<Integer> converter = new ConvertQuery<>("101", TypeLiteral.of(Integer.class));
        Integer result = config.adapt(converter);
        assertThat(result.longValue()).isEqualTo(101);
    }

    /**
     * Test of query method, of class ConvertQuery.
     */
    @Test
    public void testNonGenericQuery() {
        Configuration config = Configuration.current();

        Integer intResult = (Integer) new ConvertQuery("101", TypeLiteral.of(Integer.class)).apply(config);
        assertThat(intResult.longValue()).isEqualTo(101);

        Boolean booleanResult = (Boolean) new ConvertQuery("true", TypeLiteral.of(Boolean.class)).apply(config);
        assertThat(booleanResult).isEqualTo(Boolean.TRUE);
    }

    /**
     * Test of query method, of class ConvertQuery.
     */
    @Test
    public void testNullWithoutSuccess() {
        Configuration config = Configuration.current();

        Integer intResult = (Integer) new ConvertQuery("invalid", TypeLiteral.of(Integer.class)).apply(config);
        assertThat(intResult).isNull();
    }

}
