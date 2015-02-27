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
package org.apache.tamaya.events.tests;

import org.apache.tamaya.ConfigurationProvider;
import org.junit.Test;

/**
 * Test (currently manual) to test configuration changes.
 */
public class ObservedConfigTest {

    @Test
    public void testInitialConfig(){
        for(int i=0;i<20;i++){
            System.out.println("1: " + ConfigurationProvider.getConfiguration().get("1"));
            System.out.println("2: " + ConfigurationProvider.getConfiguration().get("2"));
            System.out.println("3: " + ConfigurationProvider.getConfiguration().get("3"));
            System.out.println("4: " + ConfigurationProvider.getConfiguration().get("4"));
            System.out.println("5: " + ConfigurationProvider.getConfiguration().get("5"));
            System.out.println("6: " + ConfigurationProvider.getConfiguration().get("6"));
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
}
