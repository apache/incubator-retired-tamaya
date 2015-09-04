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
package org.apache.tamaya.examples.remote.client;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import org.apache.catalina.Context;
import org.apache.tamaya.ConfigurationProvider;

import java.io.File;

/**
 * Simple Remote client class. Usage:
 * {@code java -jar tamaya-remote-client.jar [-Dport=<port>][-DclientId=<clientId>]}.
 * Hereby the clientId is used as a scope identifier passed as a parameter to the
 * configuration server to define the effective configuration to be provided to
 * the client. Hereby this class also reads the port to be used from the remote
 * configuration server.
 */
public class Client {

    /**
     * Util class constructor.
     */
    private Client(){}

    /**
     * Main method. Usage:
     * {@code java -jar tamaya-remote-client.jar [-Dport=<port>][-DclientId=<clientId>]}.
     * @param args the arguments passed.
     */
    public static void main(String... args){
        // init configuration
        Tomcat tomcat = new Tomcat();

        String portValue = System.getProperty("port");
        if(portValue==null){
            portValue = ConfigurationProvider.getConfiguration().get("port");
        }
        int port = portValue!=null?Integer.parseInt(portValue):8080;
        tomcat.setPort(port);
        File base = new File(System.getProperty("java.io.tmpdir"));
        Context rootCtx = tomcat.addContext("/client", base.getAbsolutePath());
        Tomcat.addServlet(rootCtx, "printConfigServlet", new PrintConfigServlet());
        rootCtx.addServletMapping("/config", "printConfigServlet");
        try {
            System.out.println("Starting client: " + getClientId() + " with port " + port);
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
        tomcat.getServer().await();
    }

    /**
     * Evaluates the current client id in the following order:
     * <ul>
     *     <li>Checking for an <b>environment</b> variable CLIENT_ID</li>
     *     <li>Checking for a system property <b>clientId</b>.</li>
     *     <li>If not found <i>default</i> is returned.</li>
     * </ul>
     * @return the client identifier to be used.
     */
    public static String getClientId() {
        String clientId = System.getenv("CLIENT_ID");
        if(clientId == null){
            clientId = System.getProperty("clientId");
        }
        if(clientId == null){
            clientId = "default";
        }
        return clientId;
    }


}
