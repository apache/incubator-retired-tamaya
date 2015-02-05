/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tamaya.core.internal;

import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;
import org.mockito.Mockito;

import javax.annotation.Priority;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.apache.tamaya.core.NotMockedAnswer.NOT_MOCKED_ANSWER;
import static org.apache.tamaya.spi.PropertyValueCombinationPolicy.DEFAULT_OVERRIDING_COLLECTOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DefaultConfigurationTest {
    private FilterAppendingA FILTER_APPENDING_A = new FilterAppendingA();
    private FilterPrependingB FILTER_PREPENDING_B = new FilterPrependingB();
    private FilterPrependingC FILTER_PREPENDING_C = new FilterPrependingC();
    private FilterPrependingD FILTER_PREPENDING_D = new FilterPrependingD();

    /* =
     * =- Section with tests for filtering properties with PropertyFilters
     * =
     */

    @Test
    public void filteringOfGivenValueWorksWithOneFilterWithoutPriority() {
        PropertySource source = Mockito.mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("V").when(source).get(eq("key"));
        doReturn(0).when(source).getOrdinal();
        doReturn("source").when(source).getName();

        ConfigurationContext context = Mockito.mock(ConfigurationContext.class, NOT_MOCKED_ANSWER);

        doReturn(singletonList(source)).when(context).getPropertySources();
        doReturn(singletonList(FILTER_APPENDING_A)).when(context).getPropertyFilters();
        doReturn(DEFAULT_OVERRIDING_COLLECTOR).when(context).getPropertyValueCombinationPolicy();

        DefaultConfiguration configuration = new DefaultConfiguration(context);

        String value = configuration.get("key");

        verify(source, atLeastOnce()).get(eq("key"));
        verifyNoMoreInteractions(source);

        assertThat(value, equalTo("VA$"));
    }

    @Test
    public void filteringOfGivenValueWorksWithTwoFiltersWithoutPriority() {
        List<PropertyFilter> filters = new ArrayList<>();

        FilterAppendingA filterA = spy(FILTER_APPENDING_A);
        FilterPrependingB filterB = spy(FILTER_PREPENDING_B);
        filters.add(filterA);
        filters.add(filterB);

        PropertySource source = Mockito.mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("V").when(source).get(eq("key"));
        doReturn(0).when(source).getOrdinal();
        doReturn("source").when(source).getName();

        ConfigurationContext context = Mockito.mock(ConfigurationContext.class, NOT_MOCKED_ANSWER);

        doReturn(singletonList(source)).when(context).getPropertySources();
        doReturn(filters).when(context).getPropertyFilters();
        doReturn(DEFAULT_OVERRIDING_COLLECTOR).when(context).getPropertyValueCombinationPolicy();

        DefaultConfiguration configuration = new DefaultConfiguration(context);

        String value = configuration.get("key");

        assertThat(value, equalTo("$BVA$"));

        verify(source, atLeastOnce()).get(eq("key"));
        verifyNoMoreInteractions(source);
    }

    @Test
    public void filteringOfGivenValueWorksWithOneFilterWithPriority() {
        PropertySource source = Mockito.mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("V").when(source).get(eq("key"));
        doReturn(0).when(source).getOrdinal();
        doReturn("source").when(source).getName();

        ConfigurationContext context = Mockito.mock(ConfigurationContext.class, NOT_MOCKED_ANSWER);

        doReturn(singletonList(source)).when(context).getPropertySources();
        doReturn(singletonList(FILTER_PREPENDING_C)).when(context).getPropertyFilters();
        doReturn(DEFAULT_OVERRIDING_COLLECTOR).when(context).getPropertyValueCombinationPolicy();

        DefaultConfiguration configuration = new DefaultConfiguration(context);

        String value = configuration.get("key");

        verify(source, atLeastOnce()).get(eq("key"));
        verifyNoMoreInteractions(source);

        assertThat(value, equalTo("$CV"));
    }

    @Test
    public void filteringOfGivenValueWorksWithTwoFiltersWithDifferentPriorities() {
        assertThat(FILTER_PREPENDING_C.getClass().isAnnotationPresent(Priority.class), is(true));
        assertThat(FILTER_PREPENDING_D.getClass().isAnnotationPresent(Priority.class), is(true));

        List<PropertyFilter> filters = new ArrayList<>();

        filters.add(FILTER_PREPENDING_C);
        filters.add(FILTER_PREPENDING_D);

        PropertySource source = Mockito.mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("V").when(source).get(eq("key"));
        doReturn(0).when(source).getOrdinal();
        doReturn("source").when(source).getName();

        ConfigurationContext context = Mockito.mock(ConfigurationContext.class, NOT_MOCKED_ANSWER);

        doReturn(singletonList(source)).when(context).getPropertySources();
        doReturn(filters).when(context).getPropertyFilters();
        doReturn(DEFAULT_OVERRIDING_COLLECTOR).when(context).getPropertyValueCombinationPolicy();

        DefaultConfiguration configuration = new DefaultConfiguration(context);

        String value = configuration.get("key");

        verify(source, atLeastOnce()).get(eq("key"));
        verifyNoMoreInteractions(source);

        assertThat(value, equalTo("$D$CV"));
    }

    private static class FilterAppendingA implements PropertyFilter {
        @Override
        public String filterProperty(String key, String value) {
            return value.endsWith("A$")
                    ? value
                    : value + "A$";
        }
    }

    private static class FilterPrependingB implements PropertyFilter {
        @Override
        public String filterProperty(String key, String value) {
            return value.startsWith("$B")
                    ? value
                    : "$B" + value;
        }
    }

    @Priority(987)
    private static class FilterPrependingC implements PropertyFilter {
        @Override
        public String filterProperty(String key, String value) {
            return value.contains("$C")
                    ? value
                    : "$C" + value;
        }
    }

    @Priority(111)
    private static class FilterPrependingD implements PropertyFilter {
        @Override
        public String filterProperty(String key, String value) {
            return value.contains("$D")
                    ? value
                    : "$D" + value;
        }
    }

}