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
package org.apache.tamaya.management.internal;

import org.apache.tamaya.management.ConfigManagementSupport;
import org.apache.tamaya.management.ManagedConfig;
import org.apache.tamaya.management.ManagedConfigMBean;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by Anatole on 20.08.2015.
 */
public class ManagedConfigTest {

    private ManagedConfigMBean bean = new ManagedConfig();

    @org.junit.Test
    public void testGetJsonConfigurationInfo() throws Exception {
        String info = bean.getJsonConfigurationInfo();
        assertNotNull(info);
        assertTrue(info.contains("java.version"));
        System.out.println(bean.getJsonConfigurationInfo());
        assertTrue(info.contains("\"configuration\": "));
    }

    @org.junit.Test
    public void testGetXmlConfigurationInfo() throws Exception {
        String info = bean.getXmlConfigurationInfo();
        assertNotNull(info);
        assertTrue(info.contains("java.version"));
        assertTrue(info.contains("<configuration>"));
        System.out.println(bean.getXmlConfigurationInfo());
    }

    @org.junit.Test
    public void testGetConfiguration() throws Exception {
        Map<String,String> config = bean.getConfiguration();
        assertNotNull(config);
        for(Map.Entry<Object, Object> en:System.getProperties().entrySet()){
            assertEquals(config.get(en.getKey()),en.getValue());
        }
    }

    @org.junit.Test
    public void testGetConfigurationArea() throws Exception {
        Map<String,String> cfg = bean.getSection("java", false);
        for(Map.Entry<String,String> en:cfg.entrySet()){
            assertEquals(System.getProperty(en.getKey()), en.getValue());
        }
    }

    @org.junit.Test
    public void testGetAreas() throws Exception {
        Set<String> sections = (bean.getSections());
        assertNotNull(sections);
        assertTrue(sections.contains("java"));
        assertTrue(sections.contains("file"));
    }

    @org.junit.Test
    public void testGetTransitiveAreas() throws Exception {
        Set<String> sections = (bean.getTransitiveSections());
        Set<String> sectionsNT = (bean.getSections());
        assertNotNull(sections);
        assertTrue(sections.contains("java"));
        assertTrue(sections.contains("sun"));
        assertTrue(sections.contains("sun.os"));
        assertTrue(sectionsNT.size()<sections.size());
    }

    @org.junit.Test
    public void testIsAreaExisting() throws Exception {
        assertTrue(bean.isAreaExisting("java"));
        assertFalse(bean.isAreaExisting("sd.fldsfl.erlwsf"));
    }

    @org.junit.Test
    public void testRegisterMBean() throws Exception {
        ObjectName on = ConfigManagementSupport.registerMBean();
        ConfigManagementSupport.registerMBean();
        // Lookup object name
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        assertTrue(mbs.getMBeanInfo(on)!=null);
    }

    @org.junit.Test
    public void testRegisterMBean1() throws Exception {
        ObjectName on1 = ConfigManagementSupport.registerMBean("SubContext1");
        ConfigManagementSupport.registerMBean("SubContext1");
        ObjectName on2 = ConfigManagementSupport.registerMBean("SubContext2");
        // Lookup object name
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        assertTrue(mbs.getMBeanInfo(on1)!=null);
        assertTrue(mbs.getMBeanInfo(on2)!=null);
    }
}