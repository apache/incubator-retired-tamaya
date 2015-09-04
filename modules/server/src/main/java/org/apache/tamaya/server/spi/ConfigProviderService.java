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
package org.apache.tamaya.server.spi;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * Configuration serving RESTful service interface.
 */
public interface ConfigProviderService {

    @GET
    @Path("/config/filtered/{path}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN,MediaType.WILDCARD})
    String getConfigurationWithPath(@PathParam("path") String path,
                                        @Context HttpServletRequest request);

    @GET
    @Path("/config")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN,MediaType.WILDCARD })
    String getConfiguration(@Context HttpServletRequest request);

    @PUT
    @Path("/config")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    String updateConfiguration(@Context HttpServletRequest request);

    @DELETE
    @Path("/config/{paths}")
    String deleteConfiguration(@PathParam("paths") String paths, @Context HttpServletRequest request);
}
