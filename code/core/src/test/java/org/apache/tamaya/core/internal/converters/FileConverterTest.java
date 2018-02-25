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

import java.io.File;
import java.net.URL;
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.spi.ConversionContext;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author William.Lieurance 2018-02-01
 */
public class FileConverterTest {
    
    ConversionContext context = new ConversionContext.Builder(TypeLiteral.of(File.class)).build();
    /**
     * Test of convert method, of class FileConverter.
     */
    @Test
    public void testConvert() {
        
        FileConverter instance = new FileConverter();
        File result;
        
        assertNull(instance.convert(null, context));
        
        URL testfileUrl = getClass().getResource("/testfile.properties");
        System.out.println(testfileUrl.toString());
        result = instance.convert(testfileUrl.toString(), context);
        assertNotNull(result);
        assertTrue(context.getSupportedFormats().contains("<File> (FileConverter)"));
    }
    
    @Test
    public void testHashCode(){
        FileConverter instance = new FileConverter();
        assertEquals(FileConverter.class.hashCode(), instance.hashCode());
    }
}
