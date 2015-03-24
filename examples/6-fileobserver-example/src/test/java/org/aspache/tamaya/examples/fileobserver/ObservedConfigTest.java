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
package org.aspache.tamaya.examples.fileobserver;

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.examples.fileobserver.TestConfigView;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

/**
 * Test (currently manual) to test configuration changes.
 */
public class ObservedConfigTest {

    @Test
    public void testInitialConfig(){
        for(int i=0;i<100;i++){
            System.out.println(dump(ConfigurationProvider.getConfiguration().with(TestConfigView.of()).getProperties()));
            System.out.println("=======================================================================");
            try{
                Thread.sleep(2000L);
            }
            catch(Exception e){
                // ignore
                e.printStackTrace();
            }
        }

    }

    private static String dump(Map<String, String> properties) {
        StringBuilder b = new StringBuilder();
        new TreeMap<>(properties).forEach((k,v)->b.append("  " + k + " = " + v + '\n'));
        return b.toString();
    }
}
