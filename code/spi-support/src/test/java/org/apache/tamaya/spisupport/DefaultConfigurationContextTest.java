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

import java.util.List;
import java.util.Map;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConfigurationContextBuilder;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author William.Lieurance 2018.02.18
 */
public class DefaultConfigurationContextTest {
    
    @Test
    public void testEqualsAndHashAndToStringValues() {
        PropertySource sharedSource = new MockedPropertySource();
        DefaultConfigurationContext ctx1 = (DefaultConfigurationContext) new DefaultConfigurationContextBuilder().build();
        ctx1.addPropertySources(sharedSource);
        DefaultConfigurationContext ctx2 = (DefaultConfigurationContext) new DefaultConfigurationContextBuilder().build();
        ctx2.addPropertySources(sharedSource);
        DefaultConfigurationContext ctx3 = (DefaultConfigurationContext) new DefaultConfigurationContextBuilder().build();
        ctx3.addPropertySources(new MockedPropertySource());

        assertEquals(ctx1, ctx1);
        assertNotEquals(null, ctx1);
        assertNotEquals("aString", ctx1);
        assertEquals(ctx1, ctx2);
        assertNotEquals(ctx1, ctx3);
        assertEquals(ctx1.hashCode(), ctx2.hashCode());
        assertNotEquals(ctx1.hashCode(), ctx3.hashCode());
        String spaces = new String(new char[70 - sharedSource.getName().length()]).replace("\0", " ");
        System.out.println(ctx1.toString());
        assertTrue(ctx1.toString().contains(sharedSource.getName() + spaces));
    }
}
