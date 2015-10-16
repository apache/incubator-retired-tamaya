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

import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;
import org.apache.tamaya.functions.ConfigurationFunctions;
import org.apache.tamaya.server.spi.ConfigService;
import org.apache.tamaya.server.spi.ScopeManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;


/**
 * Implementation of the ConfigProviderService backend interface for serving configuration.
 */
public class DefaultConfigService implements ConfigService {

    @Override
    public String getConfigurationWithPath(String path, MediaType mediaType, String scope, String scopeId, HttpServletRequest request){
        Map<String,String> requestInfo = new HashMap<>();
        requestInfo.put("filter",path);
        requestInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));
        requestInfo.put("format", mediaType.toString());
        if(scope!=null && scopeId!=null){
            return getScopedConfigurationWithPath(scope, scopeId, path, request, mediaType, requestInfo);
        }
        Configuration config = ConfigurationProvider.getConfiguration()
                .with(ConfigurationFunctions.sectionsRecursive(path.split(",")));
        if(mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.APPLICATION_XML_TYPE)) {
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_PLAIN_TYPE)) {
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        return null;
    }

    @Override
    public String getConfiguration(MediaType mediaType, String scope, String scopeId, HttpServletRequest request) {
        Map<String,String> requestInfo = new HashMap<>();
        requestInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));
        if(scope!=null && scopeId!=null){
            return getScopedConfiguration(scope, scopeId, request, mediaType, requestInfo);
        }
        Configuration config = ConfigurationProvider.getConfiguration();
        if(mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.APPLICATION_XML_TYPE)) {
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_PLAIN_TYPE)) {
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        return null;
    }

    private String getScopedConfigurationWithPath(String scope, String scopeId, String path, HttpServletRequest request,
                                                  MediaType mediaType, Map<String,String> requestInfo) {
        requestInfo.put("scope", scope);
        requestInfo.put("scopeId", scopeId);
        Configuration config = ConfigurationProvider.getConfiguration()
                .with(ScopeManager.getScope(scope, scopeId)).with(ConfigurationFunctions.sectionsRecursive(path.split(",")));
        if(mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.APPLICATION_XML_TYPE)) {
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_PLAIN_TYPE)) {
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        return null;
    }

    private String getScopedConfiguration(String scope, String scopeId, HttpServletRequest request, MediaType mediaType,
                                          Map<String,String> requestInfo) {
        requestInfo.put("scope", scope);
        requestInfo.put("scopeId", scopeId);
        Configuration config = ConfigurationProvider.getConfiguration().with(ScopeManager.getScope(scope, scopeId));
        if(mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.APPLICATION_XML_TYPE)) {
            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
        }
        if(mediaType.equals(MediaType.TEXT_PLAIN_TYPE)) {
            return config.query(ConfigurationFunctions.textInfo(requestInfo));
        }
        return null;
    }

    @Override
    public void updateConfiguration(String payload, HttpServletRequest request) {
        throw new UnsupportedOperationException("UPDATE Configuration: Not implemented");
    }

    @Override
    public void deleteConfiguration(String paths, HttpServletRequest request) {
        throw new UnsupportedOperationException("DELETE Configuration: Not implemented");
    }
}
