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
package org.apache.tamaya.server.internal;


import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.tamaya.server.Server;

/**
 * CXF based implementation of a JAX-RS server, serving the {@link DefaultConfigProviderService} service.
 */
public class SimpleServer implements Server {
    /** The CXF endpoint. */
    private org.apache.cxf.endpoint.Server cxfEndpoint;

    /**
     * Starts the CXF server under the port.
     * @param port the port.
     */
    public void start(int port) {
        if(cxfEndpoint!=null){
            if(cxfEndpoint.isStarted()){
                return;
            }
            // start it at the end...
        } else {
            JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
            sf.setServiceBeanObjects(new DefaultRestService());
            sf.setAddress("http://localhost:" + port + "/");
            cxfEndpoint = sf.create();
        }
        cxfEndpoint.start();
    }

    /**
     * Returns the current started state.
     * @return true, if the server is started.
     */
    public boolean isStarted(){
        if(cxfEndpoint!=null){
            return cxfEndpoint.isStarted();
        }
        return false;
    }

    /**
     * Stops the server if running.
     */
    public void stop(){
        if(cxfEndpoint!=null){
            cxfEndpoint.stop();
        }
    }

    /**
     * Destroy the server.
     */
    public void destroy(){
        if(cxfEndpoint!=null){
            cxfEndpoint.destroy();
            cxfEndpoint = null;
        }
    }

}