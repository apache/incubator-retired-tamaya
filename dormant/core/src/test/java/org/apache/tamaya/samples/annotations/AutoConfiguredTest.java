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

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.properties.PropertySourceBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeTrue;

/**
 * Created by Anatole on 08.09.2014.
 */
public class AutoConfiguredTest {


    @Test
    public void testTemplateWithEnvironmentVariableOnUnixoidSystem(){
        AutoConfiguredClass config = new AutoConfiguredClass();
        Configuration.configure(config, Configuration.from(PropertySourceBuilder.of("default").addPaths("classpath:cfg/autoloaded.xml").build()));
        System.out.println(config);
    }

}
