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



import org.apache.tamaya.base.convert.ConversionContext;
import org.apache.tamaya.core.converters.BigDecimalConverter;
import org.junit.Test;

import javax.config.Config;
import javax.config.ConfigProvider;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
		Config config = ConfigProvider.getConfig();
		BigDecimal valueRead = config.getValue("tests.converter.bd.decimal", BigDecimal.class);
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
		Config config = ConfigProvider.getConfig();
		BigDecimal valueRead = config.getValue("tests.converter.bd.hex.lowerX", BigDecimal.class);
		assertThat(valueRead).isNotNull();
		assertEquals(new BigDecimal("47"), valueRead);
		valueRead = config.getValue("tests.converter.bd.hex.upperX", BigDecimal.class);
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
		Config config = ConfigProvider.getConfig();
		BigDecimal valueRead = config.getValue("tests.converter.bd.foo", BigDecimal.class);
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
		Config config = ConfigProvider.getConfig();
		BigDecimal valueRead = config.getValue("tests.converter.bd.big", BigDecimal.class);
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
		Config config = ConfigProvider.getConfig();
		BigDecimal valueRead = config.getValue("tests.converter.bd.bigFloat", BigDecimal.class);
		assertThat(valueRead).isNotNull();
		assertEquals(new BigDecimal("1016666666666666623333372637236287638216389293628763.1016666666666666623333372"
				+ "63723628763821638923628193612983618293628763"), valueRead);
	}

	@Test
	public void converterHandlesNullValueCorrectly() throws Exception {
		ConversionContext context = new ConversionContext.Builder("key", BigDecimal.class).build();
		ConversionContext.setContext(context);
		BigDecimalConverter converter = new BigDecimalConverter();
		BigDecimal value = converter.convert("");
		ConversionContext.reset();
		assertThat(value).isNull();
	}

	@Test
	public void callToConvertAddsMoreSupportedFormatsToTheContext() throws Exception {
		ConversionContext context = new ConversionContext.Builder("<nokey>", BigDecimal.class).build();
		ConversionContext.setContext(context);

		BigDecimalConverter converter = new BigDecimalConverter();
		BigDecimal value = converter.convert("");
		assertTrue(context.getSupportedFormats().contains("<BigDecimal> -> new BigDecimal(String) (BigDecimalConverter)"));
		ConversionContext.reset();
		assertThat(value).isNull();
	}
}