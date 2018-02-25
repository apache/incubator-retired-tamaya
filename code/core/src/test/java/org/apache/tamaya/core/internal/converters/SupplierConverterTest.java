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
import static org.junit.Assert.*;
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
        ConversionContext stringContext = new ConversionContext.Builder(listStringTypeLiteral).build();
        
        stringResult = instance.convert(null, stringContext);
        assertNull(stringResult.get());
        
        stringResult = instance.convert("aString", stringContext);
        assertEquals("aString", stringResult.get());
        
        Supplier<InetAddress> addressResult;
        
        Configuration mockConfig = Mockito.mock(Configuration.class);
        Mockito.when(mockConfig.query(any())).thenReturn(Mockito.mock(InetAddress.class));
        
        TypeLiteral myConverterTypeLiteral = new TypeLiteral<MyConverter<InetAddress>> () {};
        ConversionContext myConverterContext = new ConversionContext.Builder(myConverterTypeLiteral)
                .setConfiguration(mockConfig)
                .build();

        addressResult = instance.convert("someKey", myConverterContext);
        assertTrue(addressResult.get() instanceof InetAddress);

}
        
    @Test
    public void testHashCode(){
        SupplierConverter instance = new SupplierConverter();
        assertEquals(SupplierConverter.class.hashCode(), instance.hashCode());
    }

    private class MyConverter<T extends InetAddress> implements PropertyConverter<InetAddress> {

        @Override
        public InetAddress convert(String value, ConversionContext context) {
            return Mockito.mock(InetAddress.class);
        }
    }
    
}
