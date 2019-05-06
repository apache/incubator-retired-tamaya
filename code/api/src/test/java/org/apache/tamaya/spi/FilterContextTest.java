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
package org.apache.tamaya.spi;

import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FilterContext}.
 */
public class FilterContextTest {

    @Test
    public void constructorWithContext() {
        PropertyValue val = new PropertyValue( "");
        FilterContext ctx = new FilterContext(val, ConfigurationContext.EMPTY);
        assertThat(val).isEqualTo(ctx.getProperty());
        assertThat(ConfigurationContext.EMPTY).isEqualTo(ctx.getConfigurationContext());
        assertThat(ctx.getConfigEntries()).isNotNull();
        assertThat(ctx.getAllValues()).hasSize(1);
    }

//    @Test
//    public void setNullContext() {
//        FilterContext.setPropertyValue(null);
//    }
//
//    @Test
//    public void setGetContext() {
//        PropertyValue val = new PropertyValue( "getKey", "v", "");
//        FilterContext ctx = new FilterContext(val,
//                new HashMap<String,PropertyValue>(), ConfigurationContext.EMPTY);
//        FilterContext.setPropertyValue(ctx);
//        assertEquals(ctx, FilterContext.getPropertyValue());
//    }
//
//    @Test
//    public void resetContext() {
//        PropertyValue val = new PropertyValue( "getKey", "v", "");
//        FilterContext ctx = new FilterContext(val,
//                new HashMap<String,PropertyValue>(), ConfigurationContext.EMPTY);
//        FilterContext.setPropertyValue(ctx);
//        assertNotNull(FilterContext.getPropertyValue());
//        FilterContext.reset();
//        assertNull(FilterContext.getPropertyValue());
//    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullPropertyValueTwoParameterVariant1() {
        new FilterContext((PropertyValue)null, ConfigurationContext.EMPTY);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullPropertyValueTwoParameterVariant2() {
        new FilterContext((List<PropertyValue>)null, ConfigurationContext.EMPTY);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullConfigurationContextTwoParameterVariant() {
        new FilterContext(Collections.singletonList(new PropertyValue( "a", "b")), null);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullPropertyValueThreeParameterVariant() {
        new FilterContext(null, Collections.EMPTY_MAP, ConfigurationContext.EMPTY);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullConfigurationContextThreeParameterVariant() {
        new FilterContext(new PropertyValue( "a", "b"), Collections.EMPTY_MAP, null);
    }

    @Test(expected = NullPointerException.class)
    public void constructorRequiresNonNullMapForConfigEntriesThreeParameterVariant() {
        new FilterContext(new PropertyValue("a", "b"), null, ConfigurationContext.EMPTY);
    }

    @Test
    public void getKey() throws Exception {
        PropertyValue val = new PropertyValue( "getKey", "v");
        FilterContext ctx = new FilterContext(val,
                new HashMap<String,PropertyValue>(), ConfigurationContext.EMPTY);
        assertThat(ctx.getProperty()).isEqualTo(val);
    }

    @Test
    public void isSinglePropertyScoped() throws Exception {
        PropertyValue val = new PropertyValue( "isSinglePropertyScoped", "v");
        FilterContext ctx = new FilterContext(val, new HashMap<String,PropertyValue>(), ConfigurationContext.EMPTY);
        assertThat(ctx.isSinglePropertyScoped()).isEqualTo(false);
        ctx = new FilterContext(Collections.singletonList(val), ConfigurationContext.EMPTY);
        assertThat(ctx.isSinglePropertyScoped()).isEqualTo(true);
    }

    @Test
    public void getConfigEntries() throws Exception {
        Map<String,PropertyValue> config = new HashMap<>();
        for(int i=0;i<10;i++) {
            config.put("key-"+i, new PropertyValue( "key-"+i, "value-"+i));
        }
        PropertyValue val = new PropertyValue( "getConfigEntries", "v");
        FilterContext ctx = new FilterContext(val, config, ConfigurationContext.EMPTY);
        assertThat(ctx.getConfigEntries()).isEqualTo(config);
        assertThat(config != ctx.getConfigEntries()).isTrue();
    }

    @Test
    public void testToString() throws Exception {
        Map<String, PropertyValue> config = new HashMap<>();
        for (int i = 0; i < 2; i++) {
            config.put("key-" + i, new PropertyValue("key-" + i, "value-" + i));
        }
        PropertyValue val = new PropertyValue("testToString", "val");
        FilterContext ctx = new FilterContext(val, config, ConfigurationContext.EMPTY);
        String toString = ctx.toString();

        assertThat(toString).isNotNull();
        System.out.println(toString);
        assertThat(toString).contains("FilterContext{value='[val]', configEntries=[",
                "key-0", "key-1").endsWith("}");
    }

}
