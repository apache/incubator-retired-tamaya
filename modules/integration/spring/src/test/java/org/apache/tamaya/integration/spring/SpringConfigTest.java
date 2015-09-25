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
package org.apache.tamaya.integration.spring;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Anatole on 25.09.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class SpringConfigTest {

    @Autowired
    private ConfiguredSpringBean configuredBean;

    @Test
    public void assertBeanNotNull(){
        assertNotNull(configuredBean);
    }

    @Test
    public void assert_JavaVersion_Injected(){
        assertNotNull(configuredBean.getJavaVersion());
        assertEquals(System.getProperty("java.version"), configuredBean.getJavaVersion());
    }

    @Test
    public void assert_Number_Injected(){
        assertEquals(configuredBean.getTestNumber(), 23);
    }

    @Test
    public void assert_Number_From_Environment(){
        assertEquals("value11", configuredBean.getEnv().getProperty("myConfiguredValue"));
    }

}
