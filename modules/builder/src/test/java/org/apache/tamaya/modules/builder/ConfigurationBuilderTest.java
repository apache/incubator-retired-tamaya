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
package org.apache.tamaya.modules.builder;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.modules.builder.util.types.CustomTypeA;
import org.apache.tamaya.modules.builder.util.types.CustomTypeB;
import org.apache.tamaya.modules.builder.util.types.CustomTypeC;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertySourceProvider;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;


import java.net.URL;

import static java.util.Arrays.asList;
import static org.apache.tamaya.modules.builder.util.mockito.NotMockedAnswer.NOT_MOCKED_ANSWER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class ConfigurationBuilderTest {

    @Test
    public void buildCanBuildEmptyConfiguration() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.build();

        assertThat(config, notNullValue());
    }

    @Test(expected = IllegalStateException.class)
    public void buildCanBeCalledOnlyOnce() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.build();
        builder.build();
    }

    /*********************************************************************
     * Tests for adding P r o p e r t y S o u r c e s
     */

    @Test(expected = NullPointerException.class)
    public void addPropertySourcesDoesNotAcceptNullValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertySources((PropertySource[])null);
    }

    @Test(expected = IllegalStateException.class)
    public void propertySourceCanNotBeAddedAfterBuildingTheConfiguration() {
        PropertySource first = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("first").when(first).getName();
        doReturn(100).when(first).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(first);

        builder.build();

        PropertySource second = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("second").when(first).getName();

        builder.addPropertySources(second);
    }

    @Test
    public void singleAddedPropertySourceIsUsed() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(source).getName();
        doReturn("a").when(source).get("keyOfA");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(source);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("a"));
    }

    @Test
    public void twoAddedPropertySourcesAreUsed() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(10).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(10).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("a"));
    }

    @Ignore
    @Test(expected = ConfigException.class)
    public void twoPropertySourcesSamePrioritySameKey() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(20).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(20).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        config.get("keyOfA");
    }

    @Test
    public void twoPropertySourcesDiffPrioritySameKeyLowerAddedFirst() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(10).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(20).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("a"));
    }

    @Test
    public void twoPropertySourcesDiffPrioritySameKeyHigherAddedFirst() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn("b").when(sourceOne).get("keyOfA");
        doReturn(30).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn("a").when(sourceTwo).get("keyOfA");
        doReturn(20).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne, sourceTwo);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("b"));
    }

    @Test
    public void consecutiveCallsToAddPropertySourceArePossible() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn(null).when(sourceOne).get(anyString());
        doReturn("b").when(sourceOne).get("b");
        doReturn(30).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn(null).when(sourceTwo).get(anyString());
        doReturn("a").when(sourceTwo).get("a");
        doReturn(30).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        assertThat(config.get("b"), equalTo("b"));
        assertThat(config.get("a"), equalTo("a"));
    }

    @Test
    public void addMultiplePropertySourcesWhereOneIsNull() {
        PropertySource sourceOne = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("one").when(sourceOne).getName();
        doReturn(null).when(sourceOne).get(anyString());
        doReturn("b").when(sourceOne).get("b");
        doReturn(30).when(sourceOne).getOrdinal();

        PropertySource sourceTwo = mock(PropertySource.class, NOT_MOCKED_ANSWER);
        doReturn("two").when(sourceTwo).getName();
        doReturn(null).when(sourceTwo).get(anyString());
        doReturn("a").when(sourceTwo).get("a");
        doReturn(30).when(sourceTwo).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne, null, sourceTwo);

        Configuration config = builder.build();

        assertThat(config.get("b"), equalTo("b"));
        assertThat(config.get("a"), equalTo("a"));
    }

    /**
     * ******************************************************************
     * Tests for adding P r o p e r t y C o n v e r t e r
     */

    @Test(expected = NullPointerException.class)
    public void canNotAddNullPropertyConverter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter(TypeLiteral.of(CustomTypeA.class), null);
    }

    @Test(expected = NullPointerException.class)
    public void canNotAddNullTypeLiteralButPropertyConverter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter((TypeLiteral<CustomTypeA>)null,
                                     prop -> new CustomTypeA(prop, prop));
    }

    @Test
    public void addedPropertyConverterWithTypeLiteralIsUsedByConfiguration() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter(TypeLiteral.of(CustomTypeA.class),
                                     prop -> new CustomTypeA(prop, prop))
               .addPropertySources(source);

        Configuration config = builder.build();

        Object resultRaw = config.get("key", CustomTypeA.class);

        assertThat(resultRaw, CoreMatchers.notNullValue());

        CustomTypeA result = (CustomTypeA)resultRaw;

        assertThat(result.getName(), equalTo("AA"));
    }

    @Test
    public void addedPropertyConverterWithClassIsUsedByConfiguration() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyConverter(CustomTypeA.class,
                                     prop -> new CustomTypeA(prop, prop))
               .addPropertySources(source);

        Configuration config = builder.build();

        Object resultRaw = config.get("key", CustomTypeA.class);

        assertThat(resultRaw, CoreMatchers.notNullValue());

        CustomTypeA result = (CustomTypeA)resultRaw;

        assertThat(result.getName(), equalTo("AA"));
    }

    @Test
    public void canGetAndConvertPropertyViaOfMethod() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertySources(source);

        Configuration config = builder.build();

        Object resultRaw = config.get("key", CustomTypeB.class);

        assertThat(resultRaw, CoreMatchers.notNullValue());

        CustomTypeB result = (CustomTypeB)resultRaw;

        assertThat(result.getName(), equalTo("A"));
    }

    /*********************************************************************
     * Tests for adding P r o p e r t y F i l t e r
     */

    @Test(expected = NullPointerException.class)
    public void canNotAddNullAsPropertyFilter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertyFilters(null);
    }

    @Test
    public void canAddNonSPIPropertyFilter() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("M").when(source).get("key");
        doReturn("source").when(source).getName();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySources(source)
                                      .addPropertyFilters(new TestNonSPIPropertyFilterA())
                                      .build();

        String property = config.get("key");

        assertThat(property, CoreMatchers.notNullValue());
        assertThat(property, CoreMatchers.containsString("ABC"));
    }

    @Test
    public void canAddNonSPIPropertyFiltersViaConsecutiveCalls() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("M").when(source).get("key");
        doReturn("source").when(source).getName();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySources(source)
                                      .addPropertyFilters(new TestNonSPIPropertyFilterA())
                                      .addPropertyFilters(new TestNonSPIPropertyFilterB())
                                      .build();

        String property = config.get("key");

        assertThat(property, CoreMatchers.notNullValue());
        assertThat(property, CoreMatchers.containsString("ABC"));
        assertThat(property, CoreMatchers.containsString("XYZ"));
    }

    @Test
    public void canAddMultipleNonSPIPropertyFiltersWhileOneIsNull() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("M").when(source).get("key");
        doReturn("source").when(source).getName();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySources(source)
                                      .addPropertyFilters(new TestNonSPIPropertyFilterA(),
                                              null,
                                              new TestNonSPIPropertyFilterB())
                                      .build();

        String property = config.get("key");

        assertThat(property, CoreMatchers.notNullValue());
        assertThat(property, CoreMatchers.containsString("ABC"));
        assertThat(property, CoreMatchers.containsString("XYZ"));
    }

    @Test
    public void overhandedNullPropertyFilterIsSafelyHandled() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("M").when(source).get("key");
        doReturn("source").when(source).getName();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySources(source)
                                      .addPropertyFilters((PropertyFilter)null) // The cast is needed!
                                      .addPropertyFilters(new TestNonSPIPropertyFilterB())
                                      .build();

        String property = config.get("key");

        assertThat(property, CoreMatchers.notNullValue());
        assertThat(property, CoreMatchers.containsString("XYZ"));
    }

    @Test
    public void canAddMultipleNonSPIPropertyFilter() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("M").when(source).get("key");
        doReturn("source").when(source).getName();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySources(source)
                                      .addPropertyFilters(new TestNonSPIPropertyFilterA(),
                                                          new TestNonSPIPropertyFilterB())
                                      .build();

        String property = config.get("key");

        assertThat(property, CoreMatchers.notNullValue());
        assertThat(property, CoreMatchers.containsString("ABC"));
        assertThat(property, CoreMatchers.containsString("XYZ"));
    }

    /*********************************************************************
     * Tests for adding
     * P r o p e r t y S o u r c e P r o v i d e r s
     */

    @Test
    public void handlesSafelyPropertyProviderReturningNullInsteadOfPropertySource() {
        PropertySourceProvider nullReturning = mock(PropertySourceProvider.class, NOT_MOCKED_ANSWER);

        doReturn(asList((PropertySource)null)).when(nullReturning).getPropertySources();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySourceProviders(new TestPropertySourceProviderB(),
                                                                  nullReturning,
                                                                  new TestPropertySourceProvider())
                                      .build();

        assertThat(config.get("tpsp_a"), Matchers.equalTo("A"));
        assertThat(config.get("tpsp_b"), Matchers.equalTo("B"));
        assertThat(config.get("tpsp_x"), Matchers.equalTo("X"));
        assertThat(config.get("tpsp_y"), Matchers.equalTo("Y"));

        verify(nullReturning).getPropertySources();
    }

    @Test(expected = NullPointerException.class)
    public void cannotAddNullAsPropertyProvider() {
        new ConfigurationBuilder().addPropertySourceProviders(null);
    }

    @Test
    public void canAddMultipleNonSPIPropertySourceProviders() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySourceProviders(new TestPropertySourceProviderB(),
                                                                  new TestPropertySourceProvider())
                                      .build();

        assertThat(config.get("tpsp_a"), Matchers.equalTo("A"));
        assertThat(config.get("tpsp_b"), Matchers.equalTo("B"));
        assertThat(config.get("tpsp_x"), Matchers.equalTo("X"));
        assertThat(config.get("tpsp_y"), Matchers.equalTo("Y"));
    }

    @Test
    public void canAddMultipleNonSPIPropertySourceProvidersWhileOfOfThemIsNull() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySourceProviders(new TestPropertySourceProviderB(), null,
                                                                  new TestPropertySourceProvider())
                                      .build();

        assertThat(config.get("tpsp_a"), Matchers.equalTo("A"));
        assertThat(config.get("tpsp_b"), Matchers.equalTo("B"));
        assertThat(config.get("tpsp_x"), Matchers.equalTo("X"));
        assertThat(config.get("tpsp_y"), Matchers.equalTo("Y"));
    }


    /*********************************************************************
     * Tests for adding
     * P r o p e r t y V a l u e C o m b i n a t i o n P o l i c y
     */

    // @todo TAYAMA-60 Write more tests

    /*********************************************************************
     * Tests for enabling and disabling of automatic loading of
     * P r o p e r t y S o u r c e s
     */

    @Test
    public void enablingOfProvidedPropertySourceServiceProvidersIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.disableProvidedPropertyConverters()
               .enableProvidedPropertyConverters();

        assertThat(builder.isPropertyConverterLoadingEnabled(), is(true));
    }

    @Test
    public void disablingOfProvidedPropertySourceServiceProvidersIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.enableProvidedPropertyConverters()
               .disableProvidedPropertyConverters();

        assertThat(builder.isPropertyConverterLoadingEnabled(), is(false));
    }

    @Test(expected = ConfigException.class)
    public void loadingOrPropertyConvertersCanBeDisabled() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(source)
                                                                 .enableProvidedPropertyConverters()
                                                                 .disableProvidedPropertyConverters();

        Configuration config = builder.build();

        config.get("key", CustomTypeC.class);
    }

    @Test
    public void loadingOfPropertyConvertersCanBeEnabled() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(source)
                                                                 .disableProvidedPropertyConverters()
                                                                 .enableProvidedPropertyConverters();

        Configuration config = builder.build();

        CustomTypeC result = config.get("key", CustomTypeC.class);

        assertThat(result, notNullValue());
        assertThat(result.getValue(), equalTo("A"));
    }

    /*********************************************************************
     * Tests for enabling and disabling of automatic loading of
     * P r o p e r t y S o u r c e s
     */

    @Test
    public void enablingOfPropertySourceLoadingIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.disableProvidedPropertySources()
               .enableProvidedPropertySources();

        assertThat(builder.isPropertySourcesLoadingEnabled(), is(true));
    }

    @Test
    public void disablingPropertySourceLoadingIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.enableProvidedPropertySources()
               .disableProvidedPropertySources();

        assertThat(builder.isPropertySourcesLoadingEnabled(), is(false));
    }

    @Test
    public void loadingOfPropertySourcesCanBeEnabled() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.disableProvidedPropertySources()
                                      .enableProvidedPropertySources()
                                      .build();


        assertThat(builder.isPropertySourcesLoadingEnabled(), is(true));
        assertThat(config.get("tps_a"), Matchers.equalTo("A"));
    }

    @Test
    public void loadingOfPropertySourcesCanBeDisabled() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.enableProvidedPropertySources()
                                      .disableProvidedPropertySources()
                                      .build();


        assertThat(builder.isPropertySourcesLoadingEnabled(), is(false));
        assertThat(config.get("tps_c"), Matchers.nullValue());
    }

    /*********************************************************************
     * Tests for enabling and disabling of automatic loading of
     * P r o p e r t y F i l t e r s
     */

    @Test
    public void enablingOfPropertyFiltersLoadingIsOk() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.disableProvidedPropertyFilters()
                                      .enabledProvidedPropertyFilters()
                                      .addPropertySources(source)
                                      .build();

        String property = config.get("key");

        assertThat(property, CoreMatchers.notNullValue());
        assertThat(property, Matchers.equalTo("AinBerlin"));
    }

    @Test
    public void disablingOfPropertyFiltersLoadingIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.enabledProvidedPropertyFilters()
               .disableProvidedPropertyFilters();

        assertThat(builder.isPropertyFilterLoadingEnabled(), is(false));
    }

    @Test
    public void loadingOfPropertyFiltersCanBeDisabled() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.disableProvidedPropertyFilters()
               .enabledProvidedPropertyFilters();

        assertThat(builder.isPropertyFilterLoadingEnabled(), is(true));
    }

    @Test
    public void loadingOfPropertyFiltersCanBeEnabled() {
        PropertySource source = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("source").when(source).getName();
        doReturn("A").when(source).get("key");
        doReturn(100).when(source).getOrdinal();

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.enabledProvidedPropertyFilters()
                                      .disableProvidedPropertyFilters()
                                      .addPropertySources(source)
                                      .build();

        String property = config.get("key");

        assertThat(property, CoreMatchers.notNullValue());
        assertThat(property, Matchers.equalTo("A"));
    }

    /*********************************************************************
     * Tests for enabling and disabling of automatic loading of
     * P r o p e r t y S o u r c e P r o v i d e r s
     */

    @Test
    public void disablingOfPropertySourceProvidersIsOk() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.enableProvidedPropertySourceProviders()
               .disableProvidedPropertySourceProviders()
               .build();

        assertThat(builder.isPropertySourceProvidersLoadingEnabled(), is(false));
    }

    @Test
    public void enablingOfPropertySourceProvidersIsOk() {

        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.disableProvidedPropertySourceProviders()
               .enableProvidedPropertySourceProviders()
               .build();

        assertThat(builder.isPropertySourceProvidersLoadingEnabled(), is(true));
    }

    @Test
    public void loadingOfPropertySourceProvidersCanBeEnabled() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.disableProvidedPropertySourceProviders()
                                      .enableProvidedPropertySourceProviders()
                                      .build();

        assertThat(builder.isPropertySourceProvidersLoadingEnabled(), is(true));
        assertThat(config.get("tpsp_x"), Matchers.equalTo("X"));
        assertThat(config.get("tpsp_y"), Matchers.equalTo("Y"));
    }

    @Test
    public void loadingOfPropertySourceProvidersCanBeDisabled() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.enableProvidedPropertySourceProviders()
                                      .disableProvidedPropertySourceProviders()
                                      .build();

        assertThat(builder.isPropertySourceProvidersLoadingEnabled(), is(false));
        assertThat(config.get("tpsp_x"), nullValue());
        assertThat(config.get("tpsp_x"), nullValue());
    }

    /*********************************************************************
     * Tests for loading resources via URL (as String)
     */

    @Test
    public void loadOneJSONPropertySourceViaStringURL() {
        URL resource = this.getClass().getResource("/configfiles/json/simple.json");

        assertThat(resource, CoreMatchers.notNullValue());

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySource(resource.toString())
                                      .build();

        assertThat(config, CoreMatchers.notNullValue());
        assertThat(config.get("a"), equalTo("A"));
        assertThat(config.get("b"), equalTo("B"));
    }

    @Test
    public void loadMultipleJSONPropertySourceViaStringURL() {
        URL first = this.getClass().getResource("/configfiles/json/first.json");
        URL second = this.getClass().getResource("/configfiles/json/second.json");
        URL third = this.getClass().getResource("/configfiles/json/third.json");

        assertThat(first, CoreMatchers.notNullValue());
        assertThat(second, CoreMatchers.notNullValue());
        assertThat(third, CoreMatchers.notNullValue());

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySource(first.toString(), second.toString(),
                                                         null, third.toString())
                                      .build();

        assertThat(config, CoreMatchers.notNullValue());

        // from first.json
        assertThat(config.get("d"), equalTo("D"));
        assertThat(config.get("e"), equalTo("E"));

        // from second.json
        assertThat(config.get("m"), equalTo("M"));
        assertThat(config.get("n"), equalTo("N"));

        // from thrid.json
        assertThat(config.get("p"), equalTo("P"));
        assertThat(config.get("q"), equalTo("Q"));
    }

    /**
     * ******************************************************************
     * Tests for loading resources via URL (as URL object)
     */

    @Test
    public void loadOneJSONPropertySourceViaURL() {
        URL resource = this.getClass().getResource("/configfiles/json/simple.json");

        assertThat(resource, CoreMatchers.notNullValue());

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySource(resource)
                                      .build();

        assertThat(config, CoreMatchers.notNullValue());
        assertThat(config.get("a"), equalTo("A"));
        assertThat(config.get("b"), equalTo("B"));
    }

    @Test
    public void loadMultipleJSONPropertySourceViaURL() {
        URL first = this.getClass().getResource("/configfiles/json/first.json");
        URL second = this.getClass().getResource("/configfiles/json/second.json");
        URL third = this.getClass().getResource("/configfiles/json/third.json");

        assertThat(first, CoreMatchers.notNullValue());
        assertThat(second, CoreMatchers.notNullValue());
        assertThat(third, CoreMatchers.notNullValue());

        ConfigurationBuilder builder = new ConfigurationBuilder();

        Configuration config = builder.addPropertySource(first, second,
                                                         null, third)
                                      .build();

        assertThat(config, CoreMatchers.notNullValue());

        // from first.json
        assertThat(config.get("d"), equalTo("D"));
        assertThat(config.get("e"), equalTo("E"));

        // from second.json
        assertThat(config.get("m"), equalTo("M"));
        assertThat(config.get("n"), equalTo("N"));

        // from thrid.json
        assertThat(config.get("p"), equalTo("P"));
        assertThat(config.get("q"), equalTo("Q"));
    }

}
