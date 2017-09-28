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

import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.osgi.service.component.annotations.Component;

import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converter, converting from String to Currency, the supported format is one of the following:
 * <ul>
 *     <li>CHF (currency code)</li>
 *     <li>123 (numeric currency value &gt;
 *     = 0)</li>
 *     <li>DE (ISO 2-digit country)</li>
 *     <li>de_DE, de_DE_123 (Locale)</li>
 * </ul>
 */
@Component(service = PropertyConverter.class)
public class CurrencyConverter implements PropertyConverter<Currency> {

    private static final Logger LOG = Logger.getLogger(CurrencyConverter.class.getName());

    @Override
    public Currency convert(String value, ConversionContext context) {
        context.addSupportedFormats(getClass(), "<currencyCode>, using Locale.ENGLISH", "<numericValue>", "<locale>");
        String trimmed = Objects.requireNonNull(value).trim();
        try {
            return Currency.getInstance(trimmed.toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            LOG.log(Level.FINEST, "Not a valid textual currency code: " + trimmed + ", checking for numeric...", e);
        }
        try {
            // Check for numeric code
            Integer numCode = Integer.parseInt(trimmed);
            for (Currency currency : Currency.getAvailableCurrencies()) {
                if (currency.getNumericCode() == numCode) {
                    return currency;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.FINEST, "Not a valid numeric currency code: " + trimmed + ", checking for locale...", e);
        }
        try {
            // Check for numeric code
            String[] parts = trimmed.split("\\_");
            Locale locale;
            switch (parts.length) {
                case 1:
                    locale = new Locale("", parts[0]);
                    break;
                case 2:
                    locale = new Locale(parts[0], parts[1]);
                    break;
                case 3:
                    locale = new Locale(parts[0], parts[1], parts[2]);
                    break;
                default:
                    locale = null;
            }
            if (locale != null) {
                return Currency.getInstance(locale);
            }
            LOG.finest("Not a valid currency: " + trimmed + ", giving up...");
        } catch (Exception e) {
            LOG.log(Level.FINEST, "Not a valid country locale for currency: " + trimmed + ", giving up...", e);
        }
        return null;
    }

    @Override
    public boolean equals(Object o){
        return getClass().equals(o.getClass());
    }

    @Override
    public int hashCode(){
        return getClass().hashCode();
    }
}
