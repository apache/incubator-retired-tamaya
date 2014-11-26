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
package org.apache.tamaya.samples.annotations;

import org.apache.tamaya.Configuration;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anatole on 08.09.2014.
 */
public class ConfiguredTest{

    @Test
    public void testTemplate(){
        ConfigTemplate template = Configuration.of(ConfigTemplate.class);
        assertNotNull(template);
        assertNotNull(template.computerName());
        assertNotNull(template.APPDATA());
        assertEquals(2233, template.int2());
        assertEquals(Integer.valueOf(5), template.int1());
        assertNotNull(System.getProperty("java.version"), template.javaVersion2());
    }

}
