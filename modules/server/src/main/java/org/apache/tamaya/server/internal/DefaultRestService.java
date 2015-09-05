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

import org.apache.tamaya.server.spi.ConfigProviderService;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * Implementation of the JAX-RS interface for serving configuration.
 */
public class DefaultRestService {

    private ConfigProviderService providerService = ServiceContextManager.getServiceContext()
            .getService(ConfigProviderService.class);

    @GET
    @Path("/config/filtered/{path}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN, MediaType.WILDCARD})
    public String getConfigurationWithPath(@PathParam("path") String path,
                                    @Context HttpServletRequest request){
        String format = request.getParameter("format");
        if(format==null){
            format = request.getHeader(HttpHeaders.ACCEPT);
        }
        String scope = request.getParameter("scope");
        String scopeId = request.getParameter("scopeId");
        try {
            String response = providerService.getConfigurationWithPath(path, format, scope, scopeId, request);
            Response.ResponseBuilder rb = Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_ENCODING, "utf-8");
            if(format.contains(MediaType.APPLICATION_JSON)) {
                rb.type(MediaType.APPLICATION_JSON_TYPE);
                return response;
            }
            if(format.contains(MediaType.APPLICATION_XML)) {
                rb.type(MediaType.APPLICATION_XML_TYPE);
                return response;
            }
            if(format.contains(MediaType.TEXT_HTML)) {
                rb.type(MediaType.TEXT_HTML_TYPE);
                return response;
            }
            if(format.contains(MediaType.TEXT_PLAIN)) {
                rb.type(MediaType.TEXT_PLAIN_TYPE);
                return response;
            }
            rb.status(Response.Status.BAD_REQUEST);
            return null;
        } catch(Exception e){
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return null;
        }
    }



    @GET
    @Path("/config")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN, MediaType.WILDCARD})
    public String getConfiguration(@Context HttpServletRequest request) {
        String format = request.getParameter("format");
        if(format==null){
            format = request.getHeader(HttpHeaders.ACCEPT);
        }
        String scope = request.getParameter("scope");
        String scopeId = request.getParameter("scopeId");
        try {
            String response = providerService.getConfiguration(format, scope, scopeId, request);
            Response.ResponseBuilder rb = Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_ENCODING, "utf-8");
            if(format.contains(MediaType.APPLICATION_JSON)) {
                rb.type(MediaType.APPLICATION_JSON_TYPE);
                return response;
            }
            if(format.contains(MediaType.APPLICATION_XML)) {
                rb.type(MediaType.APPLICATION_XML_TYPE);
                return response;
            }
            if(format.contains(MediaType.TEXT_HTML)) {
                rb.type(MediaType.TEXT_HTML_TYPE);
                return response;
            }
            if(format.contains(MediaType.TEXT_PLAIN)) {
                rb.type(MediaType.TEXT_PLAIN_TYPE);
                return response;
            }
            rb.status(Response.Status.BAD_REQUEST);
            return null;
        } catch(Exception e){
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @Path("/config/update")
    @PUT
    public String updateConfiguration(String payload, @Context HttpServletRequest request) {
        Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        return "UPDATE Configuration: Not implemented";
    }

    @Path("/config/delete")
    @DELETE
    public String deleteConfiguration(String paths, @Context HttpServletRequest request) {
        Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        return "DELETE Configuration: Not implemented";
    }
}
