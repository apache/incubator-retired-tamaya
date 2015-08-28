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

import org.apache.tamaya.ConfigOperator;
import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.server.spi.ConfigProviderService;
import org.apache.tamaya.server.spi.ScopeManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of the JAX-RS interface for serving configuration.
 */
public class DefaultConfigService implements ConfigProviderService {

    @Override
    @GET
    @Path("/config/filtered/{path}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    public String getConfigurationWithPath(@PathParam("path") String path,
                                    @Context HttpServletRequest request){
        Map<String,String> requestInfo = new HashMap<>();
        requestInfo.put("filter",path);
        requestInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));
        String format = request.getParameter("format");
        if(format==null){
            format = request.getHeader(HttpHeaders.ACCEPT);
        }
        requestInfo.put("format", format);
        String scope = request.getParameter("scope");
        if(scope!=null){
            return getScopedConfigurationWithPath(scope, path, request, format, requestInfo);
        }
        Configuration config = ConfigurationProvider.getConfiguration()
                .with(ConfigurationFunctions.sectionsRecursive(path.split(",")));
        if(format.contains(MediaType.APPLICATION_JSON)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_JSON_TYPE);
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(format.contains(MediaType.APPLICATION_XML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_XML_TYPE);
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_HTML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_HTML_TYPE);
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_PLAIN)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_PLAIN_TYPE);
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        Response.status(Response.Status.BAD_REQUEST).allow(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .build();
        return null;
    }



    @Override
    @GET
    @Path("/config")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN})
    public String getConfiguration(@Context HttpServletRequest request) {
        Map<String,String> requestInfo = new HashMap<>();
        requestInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));
        String format = request.getParameter("format");
        if(format==null){
            format = request.getHeader(HttpHeaders.ACCEPT);
        }
        String scope = request.getParameter("scope");
        if(scope!=null){
            return getScopedConfiguration(scope, request, format, requestInfo);
        }
        Configuration config = ConfigurationProvider.getConfiguration();
        if(format.contains(MediaType.APPLICATION_JSON)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_JSON_TYPE);
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(format.contains(MediaType.APPLICATION_XML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_XML_TYPE);
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_HTML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_HTML_TYPE);
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_PLAIN)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_PLAIN_TYPE);
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        Response.status(Response.Status.BAD_REQUEST).allow(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
        return null;
    }

    private String getScopedConfigurationWithPath(String scope, String path, HttpServletRequest request, String format, Map<String,String> requestInfo) {
        requestInfo.put("scope", scope);
        Configuration config = ConfigurationProvider.getConfiguration()
                .with(getScope(scope)).with(ConfigurationFunctions.sectionsRecursive(path.split(",")));
        if(format.contains(MediaType.APPLICATION_JSON)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_JSON_TYPE);
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(format.contains(MediaType.APPLICATION_XML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_XML_TYPE);
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_HTML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_HTML_TYPE);
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_PLAIN)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_PLAIN_TYPE);
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        Response.status(Response.Status.BAD_REQUEST).allow(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
        return null;
    }

    private String getScopedConfiguration(String scope, HttpServletRequest request, String format, Map<String,String> requestInfo) {
        requestInfo.put("scope", scope);
        Configuration config = ConfigurationProvider.getConfiguration().with(getScope(scope));
        if(format.contains(MediaType.APPLICATION_JSON)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_JSON_TYPE);
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(format.contains(MediaType.APPLICATION_XML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.APPLICATION_XML_TYPE);
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_HTML)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_HTML_TYPE);
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(format.contains(MediaType.TEXT_PLAIN)) {
            Response.status(Response.Status.OK).encoding("utf-8").type(MediaType.TEXT_PLAIN_TYPE);
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        Response.status(Response.Status.BAD_REQUEST).allow(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML)
                .build();
        return null;
    }

    private ConfigOperator getScope(String scope) {
        return ScopeManager.getScope(scope);
    }

    @Override
    public String updateConfiguration(@Context HttpServletRequest request) {
        Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        return "UPDATE Configuration: Not implemented";
    }

    @Override
    public String deleteConfiguration(String paths, @Context HttpServletRequest request) {
        Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        return "DELETE Configuration: Not implemented";
    }
}
