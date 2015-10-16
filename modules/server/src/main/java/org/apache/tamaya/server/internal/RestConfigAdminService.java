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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.server.spi.ConfigAdminService;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


/**
 * Implementation of the JAX-RS interface for serving configuration.
 */
public class RestConfigAdminService {

    private ConfigAdminService adminService = ServiceContextManager.getServiceContext()
            .getService(ConfigAdminService.class);

    @GET
    @Path("/config/admin/source")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    public String getPropertySource(@Context HttpServletRequest request){
        MediaType mediaType = MediaTypeUtil.getMediaType(request.getParameter("format"), request.getHeader(HttpHeaders.ACCEPT));
        String sourceId = request.getParameter("name");
        String sourceClass = request.getParameter("class");
        List<PropertySource> propertySources = adminService.collectPropertySources(sourceId, sourceClass);
        try {
            String response = adminService.formatPropertySource(mediaType, sourceId, sourceClass, request);
            if(response!=null){
                Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_ENCODING, "utf-8").type(mediaType);
                return response;
            }
            Response.status(Response.Status.BAD_REQUEST).header(HttpHeaders.CONTENT_ENCODING, "utf-8");
            return propertySources.toString();
        } catch(Exception e){
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @GET
    @Path("/config/admin/filter")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    public String getPropertyFilter(@Context HttpServletRequest request){
        MediaType mediaType = MediaTypeUtil.getMediaType(request.getParameter("format"), request.getHeader(HttpHeaders.ACCEPT));
        String filterClass = request.getParameter("class");
        List<PropertyFilter> propertyFilter = adminService.collectPropertyFilters(filterClass);
        try {
            String response = adminService.formatPropertyFilter(mediaType, filterClass, request);
            if(response!=null){
                Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_ENCODING, "utf-8").type(mediaType);
                return response;
            }
            Response.status(Response.Status.BAD_REQUEST).header(HttpHeaders.CONTENT_ENCODING, "utf-8");
            return propertyFilter.toString();
        } catch(Exception e){
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return null;
        }
    }

    @GET
    @Path("/config/admin/converter")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    public String getPropertyConverter(@Context HttpServletRequest request){
        MediaType mediaType = MediaTypeUtil.getMediaType(request.getParameter("format"), request.getHeader(HttpHeaders.ACCEPT));
        String converterClass = request.getParameter("class");
        String targetType = request.getParameter("target");
        List<PropertyConverter> propertyConverter = adminService.collectPropertyConverters(converterClass, targetType);
        try {
            String response = adminService.formatPropertyConverter(mediaType, propertyConverter, request);
            if(response!=null){
                Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_ENCODING, "utf-8").type(mediaType);
                return response;
            }
            Response.status(Response.Status.BAD_REQUEST).header(HttpHeaders.CONTENT_ENCODING, "utf-8");
            return propertyConverter.toString();
        } catch(Exception e){
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return null;
        }
    }


    @GET
    @Path("/config/admin/combination")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.WILDCARD})
    public String getCombinationPolicy(@Context HttpServletRequest request){
        MediaType mediaType = MediaTypeUtil.getMediaType(request.getParameter("format"), request.getHeader(HttpHeaders.ACCEPT));
        try {
            String response = adminService.formatCombinationPolicy(mediaType, ConfigurationProvider.getConfigurationContext().getPropertyValueCombinationPolicy(), request);
            if(response!=null){
                Response.status(Response.Status.OK).header(HttpHeaders.CONTENT_ENCODING, "utf-8").type(mediaType);
                return response;
            }
            Response.status(Response.Status.BAD_REQUEST).header(HttpHeaders.CONTENT_ENCODING, "utf-8");
            return null;
        } catch(Exception e){
            Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            return null;
        }
    }


}
