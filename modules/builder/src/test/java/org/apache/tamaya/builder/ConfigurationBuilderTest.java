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
package org.apache.tamaya.builder;

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.PropertySource;
import org.junit.Ignore;
import org.junit.Test;

import static org.apache.tamaya.builder.util.mockito.NotMockedAnswer.NOT_MOCKED_ANSWER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

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

    @Test(expected = NullPointerException.class)
    public void addPropertySourcesDoesNotAcceptNullValue() {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.addPropertySources((PropertySource[])null);
    }

    @Test(expected = IllegalStateException.class)
    public void propertySourceCanNotBeAddedAfterBuildingTheConfiguration() {
        PropertySource first = mock(PropertySource.class, NOT_MOCKED_ANSWER);

        doReturn("first").when(first).getName();

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

        ConfigurationBuilder builder = new ConfigurationBuilder().addPropertySources(sourceOne)
                                                                 .addPropertySources(sourceTwo);

        Configuration config = builder.build();

        String valueOfA = config.get("keyOfA");

        assertThat(valueOfA, notNullValue());
        assertThat(valueOfA, equalTo("b"));
    }
}
