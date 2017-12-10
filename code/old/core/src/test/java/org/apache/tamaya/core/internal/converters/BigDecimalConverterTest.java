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
package org.apache.tamaya.core.internal.converters;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Tests the default converter for bytes.
 */
public class BigDecimalConverterTest {

	/**
	 * Test conversion. The value are provided by
	 * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvert_BigDecimal_Decimal() throws Exception {
		Configuration config = ConfigurationProvider.getConfiguration();
		BigDecimal valueRead = config.get("tests.converter.bd.decimal", BigDecimal.class);
		assertThat(valueRead).isNotNull();
		assertEquals(new BigDecimal(101), valueRead);
	}

	/**
	 * Test conversion. The value are provided by
	 * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvert_BigDecimal_Hex() throws Exception {
		Configuration config = ConfigurationProvider.getConfiguration();
		BigDecimal valueRead = config.get("tests.converter.bd.hex.lowerX", BigDecimal.class);
		assertThat(valueRead).isNotNull();
		assertEquals(new BigDecimal("47"), valueRead);
		valueRead = config.get("tests.converter.bd.hex.upperX", BigDecimal.class);
		assertThat(valueRead).isNotNull();
		assertEquals(new BigDecimal("63"), valueRead);
	}

	/**
	 * Test conversion. The value are provided by
	 * {@link org.apache.tamaya.core.internal.converters.ConverterTestsPropertySource}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvert_NotPresent() throws Exception {
		Configuration config = ConfigurationProvider.getConfiguration();
		BigDecimal valueRead = config.get("tests.converter.bd.foo", BigDecimal.class);
		assertNull(valueRead);
	}

	/**
	 * Test conversion. The value are provided by
	 * {@link ConverterTestsPropertySource}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvert_BigDecimal_BigValue() throws Exception {
		Configuration config = ConfigurationProvider.getConfiguration();
		BigDecimal valueRead = config.get("tests.converter.bd.big", BigDecimal.class);
		assertThat(valueRead).isNotNull();
		assertEquals(new BigDecimal("101666666666666662333337263723628763821638923628193612983618293628763"),
				valueRead);
	}

	/**
	 * Test conversion. The value are provided by
	 * {@link ConverterTestsPropertySource}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testConvert_BigDecimal_BigFloatValue() throws Exception {
		Configuration config = ConfigurationProvider.getConfiguration();
		BigDecimal valueRead = config.get("tests.converter.bd.bigFloat", BigDecimal.class);
		assertThat(valueRead).isNotNull();
		assertEquals(new BigDecimal("1016666666666666623333372637236287638216389293628763.1016666666666666623333372"
				+ "63723628763821638923628193612983618293628763"), valueRead);
	}

	@Test
	public void converterHandlesNullValueCorrectly() throws Exception {
		ConversionContext context = mock(ConversionContext.class);

		BigDecimalConverter converter = new BigDecimalConverter();
		BigDecimal value = converter.convert("", context);

		assertThat(value).isNull();
	}

	@Test
	public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
		ConversionContext context = mock(ConversionContext.class);

		BigDecimalConverter converter = new BigDecimalConverter();
		BigDecimal value = converter.convert("", context);

		assertThat(value).isNull();
		verify(context).addSupportedFormats(BigDecimalConverter.class, "<bigDecimal> -> new BigDecimal(String)");
	}
}