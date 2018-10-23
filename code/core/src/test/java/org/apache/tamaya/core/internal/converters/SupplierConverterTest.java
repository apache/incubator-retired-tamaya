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

import java.net.InetAddress;
import java.util.List;
import java.util.function.Supplier;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.apache.tamaya.spi.PropertyConverter;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Matchers.any;
import org.mockito.Mockito;

/**
 *
 * @author William.Lieurance 2018-02-01
 */
public class SupplierConverterTest {

    /**
     * Test of convert method, of class SupplierConverter.
     */
    @Test
    public void testConvert() {
        SupplierConverter instance = new SupplierConverter();
        Supplier<String> stringResult;
        TypeLiteral listStringTypeLiteral = new TypeLiteral<List<String>> () {};
        ConversionContext context = new ConversionContext.Builder(listStringTypeLiteral).build();
        ConversionContext.set(context);
        stringResult = instance.convert(null);
        assertThat(stringResult.get()).isNull();
        
        stringResult = instance.convert("aString");
        assertThat(stringResult.get()).isEqualTo("aString");

        ConversionContext.reset();

        Supplier<InetAddress> addressResult;
        
        Configuration mockConfig = Mockito.mock(Configuration.class);
        Mockito.when(mockConfig.query(any())).thenReturn(Mockito.mock(InetAddress.class));
        
        TypeLiteral myConverterTypeLiteral = new TypeLiteral<MyConverter<InetAddress>> () {};
        ConversionContext myConverterContext = new ConversionContext.Builder(myConverterTypeLiteral)
                .setConfiguration(mockConfig)
                .build();
        ConversionContext.set(myConverterContext);
        addressResult = instance.convert("someKey");

        assertThat(addressResult.get() instanceof InetAddress).isTrue();
        ConversionContext.reset();

}
        
    @Test
    public void testHashCode(){
        SupplierConverter instance = new SupplierConverter();
        assertThat(instance.hashCode()).isEqualTo(SupplierConverter.class.hashCode());
    }

    @Test
    public void testEquality() {
        SupplierConverter converter = new SupplierConverter();

        assertThat(converter).isEqualTo(new SupplierConverter());
        assertThat(converter).isNotEqualTo(new URIConverter());
        assertThat(converter).isNotEqualTo(null);
    }

    private class MyConverter<T extends InetAddress> implements PropertyConverter<InetAddress> {

        @Override
        public InetAddress convert(String value) {
            return Mockito.mock(InetAddress.class);
        }
    }
    
}
