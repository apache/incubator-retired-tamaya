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
package org.apache.tamaya.examples.remote.server;


import org.apache.tamaya.server.Server;


/**
 * Main class to start the server part of this example. Usage:
 * <pre>
 *     java org.apache.tamaya.examples.remote.server.Start [-Dport=1234]
 * </pre>
 * The optional port value hereby overrides any port value received from the remote configuration.
 * By default port 8888 is used.
 */
public class Start {

    /**
     * Utility class only.
     */
    private Start(){}

    /**
     * Mein methods. Usage:
     * <pre>
     *     java org.apache.tamaya.examples.remote.server.Start [-Dport=1234]
     * </pre>
     * The optional port value hereby overrides any port value received from the remote configuration.
     * By default port 8888 is used.
     * @param args the args passed.
     */
    public static void main(String... args){
        // init configuration
        String portValue = System.getProperty("port");
        int port = portValue!=null?Integer.parseInt(portValue):8888;
        try {
            System.out.println("Starting server with port " + port);
            Server server = org.apache.tamaya.server.ConfigServer.createServer();
            server.start(port);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
