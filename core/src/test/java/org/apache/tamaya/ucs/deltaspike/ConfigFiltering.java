/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
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
package org.apache.tamaya.ucs.deltaspike;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.core.config.ConfigFunctions;
import org.apache.tamaya.core.config.ConfigurationBuilder;

import java.net.URL;

/**
 * This is one of possible multiple comparisons of Tamaya functionality with Deltapike.
 * <pre>
 * A company uses REST endpoints and need to talk to those.
 * So we need to configure a few things:
 * 1.) the endpoint URL
 * 2.) the username which should be used to connect (e.g. over https, BASIC auth, whatever)
 * 3.) the passphrase which should be used to connect.
 *
 * The security credentials (passphrase) should not get stored in plaintext but encrypted using PKI. It should of course also not get logged out in clear text but shall get masked if logging out the configured values is enabled.
 *
 * In DeltaSpike I'd just register a ConfigFilter to do the password decoding on the fly.
 * </pre>
 */
public class ConfigFiltering {

    public void scenario1UseAdapter(){
        URL endPoint = Configuration.current().get("endPointURL", URL.class).get();
        String uid = Configuration.current().get("endPointURL.user").get();
//        String pwd = Configuration.current().getAdapted("endPointURL.password", v -> MyPKI.decrypt(v));
    }

    public void scenario1AddFilterOnSPI(){
        URL endPoint = Configuration.current().get("endPointURL", URL.class).get();
        String uid = Configuration.current().get("endPointURL.user").get();
        String pwd = Configuration.current().get("endPointURL.password").get();

        // In the SPI
        ConfigurationBuilder.of().addPaths("...").filterValues((k,v) -> k.equals("endPointURL.password")?MyPKI.decrypt(v):v).build();
    }

    private static class MyPKI{
        public static String decrypt(String val){return val;};
    }
}
