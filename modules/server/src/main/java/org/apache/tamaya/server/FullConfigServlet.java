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
package org.apache.tamaya.server;

import org.apache.http.HttpStatus;
import org.apache.tamaya.server.spi.ConfigProviderService;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Servlet to be registered for answering requests for accessing unscoped configuration. You can register this
 * servlet e.g. under {@code /config}. Then it allows to perform requests in the form of
 * {@code /config/java,sun?scope=CLIENT&scopeId=1.2.3.4&format=text/json}.
 */
public class FullConfigServlet extends HttpServlet{

    private static final Logger LOG = Logger.getLogger(FilteredConfigServlet.class.getName());

    private static final long serialVersionUID = 1L;

    /**
     * Gets the Service delegate to provide the configuration representation in different formats and scopes.
     */
    private ConfigProviderService getConfService(){
        return ServiceContextManager.getServiceContext()
                .getService(ConfigProviderService.class);
    }

    /**
     * Servlet to be registered for answering requests for accessing unscoped configuration. You can register this
     * servlet e.g. under {@code /config}. Then it allows to perform requests in the form of
     * {@code /config/java,sun?scope=CLIENT&scopeId=1.2.3.4&format=text/json}.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse res) throws ServletException, IOException {
        String format = request.getParameter("format");
        if(format==null){
            format = request.getHeader(HttpHeaders.ACCEPT);
        }
        String scope = request.getParameter("scope");
        String scopeId = request.getParameter("scopeId");
        try {
            String response = getConfService().getConfiguration(format, scope, scopeId, request);
            res.setStatus(HttpStatus.SC_OK);
            res.setHeader(HttpHeaders.CONTENT_ENCODING, "utf-8");
            if(format.contains(MediaType.APPLICATION_JSON)) {
                res.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE.toString());
                res.getOutputStream().print(response);
                return;
            }
            if(format.contains(MediaType.APPLICATION_XML)) {
                res.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_TYPE.toString());
                res.getOutputStream().print(response);
                return;
            }
            if(format.contains(MediaType.TEXT_HTML)) {
                res.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_TYPE.toString());
                res.getOutputStream().print(response);
                return;
            }
            if(format.contains(MediaType.TEXT_PLAIN)) {
                res.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_TYPE.toString());
                res.getOutputStream().print(response);
                return;
            }
            res.setStatus(HttpStatus.SC_BAD_REQUEST);
        } catch(Exception e){
            LOG.log(Level.SEVERE, "Error printing config.", e);
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
