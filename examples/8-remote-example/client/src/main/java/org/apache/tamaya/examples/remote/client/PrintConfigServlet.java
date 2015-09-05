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

import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * S simple servlet, which simply prints out the current local configuration. This is useful to demonstrate the
 * different client configurations read.
 */
public class PrintConfigServlet extends HttpServlet {

    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /**
     * The client id used.
     */
    private String clientId = Client.getClientId();
    /**
     * The current info map, containing the clientId. This is passed to be added to the configuration's info section
     * when shows.
     */
    private Map<String,String> info = new HashMap<>();

    /**
     * Constructor.
     */
    public PrintConfigServlet(){
        info.put("clientId", clientId);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("CONTENT-TYPE", "text/html");
        Map<String,String> info = new HashMap<>();
        info.put("clientId", clientId);
        resp.setStatus(HttpServletResponse.SC_OK);
        String filter = req.getPathInfo();
        if (filter != null) {
            if(filter.startsWith("/")){
                filter = filter.substring(1);
            }
            info.put("filter", filter);
            resp.getWriter().append(ConfigurationProvider.getConfiguration().with(ConfigurationFunctions
                    .sectionRecursive(false, filter.split(","))).query(ConfigurationFunctions.htmlInfo(info)));
        } else {
            info.put("filter", ".*");
            resp.getWriter().append(ConfigurationProvider.getConfiguration().query(ConfigurationFunctions.htmlInfo(info)));
        }
    }

}
