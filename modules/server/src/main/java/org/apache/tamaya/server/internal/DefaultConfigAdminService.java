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
import org.apache.tamaya.TypeLiteral;
import org.apache.tamaya.server.spi.ConfigAdminService;
import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;


/**
 * Implementation of the ConfigProviderService backend interface for serving configuration.
 */
public class DefaultConfigAdminService implements ConfigAdminService {
    @Override
    public String formatCombinationPolicy(MediaType mediaType, PropertyValueCombinationPolicy propertyValueCombinationPolicy, HttpServletRequest request) {
        return null;
    }

    @Override
    public String formatPropertyConverter(MediaType mediaType, List<PropertyConverter> propertyConverter, HttpServletRequest request) {
        return null;
    }

    @Override
    public String formatPropertyFilter(MediaType mediaType, String filterClass, HttpServletRequest request) {
        return null;
    }

    @Override
    public String formatPropertySource(MediaType mediaType, String sourceId, String sourceClass, HttpServletRequest request) {
        return null;
    }

    @Override
    public List<PropertyConverter> collectPropertyConverters(String converterClass, String targetType) {
        List<PropertyConverter> result = new ArrayList<>();
        TypeLiteral type = null;
        if(targetType!=null){
            try {
                type = TypeLiteral.of(Class.forName(targetType));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        List<PropertyConverter> list = ConfigurationProvider.getConfigurationContext().getPropertyConverters(type);
        for(PropertyConverter conv:list){
            if(converterClass!=null && !conv.getClass().getName().matches(converterClass)){
                continue;
            }
            if(targetType!=null && !conv.getClass().getName().matches(converterClass)){
                continue;
            }
            result.add(conv);
        }
        return result;
    }

    @Override
    public List<PropertyFilter> collectPropertyFilters(String filterClass) {
        List<PropertyFilter> result = new ArrayList<>();
        for(PropertyFilter conv:ConfigurationProvider.getConfigurationContext().getPropertyFilters()){
            if(filterClass!=null && !conv.getClass().getName().matches(filterClass)){
                continue;
            }
            result.add(conv);
        }
        return result;
    }

    @Override
    public List<PropertySource> collectPropertySources(String sourceId, String sourceClass) {
        List<PropertySource> result = new ArrayList<>();
        for(PropertySource conv:ConfigurationProvider.getConfigurationContext().getPropertySources()){
            if(sourceClass!=null && !conv.getClass().getName().matches(sourceClass)){
                continue;
            }
            if(sourceId!=null && !conv.getName().matches(sourceId)) {
                continue;
            }
            result.add(conv);
        }
        return result;
    }

//    @Override
//    public String getConfigurationWithPath(String path, MediaType mediaType, String scope, String scopeId, HttpServletRequest request){
//        Map<String,String> requestInfo = new HashMap<>();
//        requestInfo.put("filter",path);
//        requestInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));
//        requestInfo.put("format", mediaType.toString());
//        if(scope!=null && scopeId!=null){
//            return getScopedConfigurationWithPath(scope, scopeId, path, request, mediaType, requestInfo);
//        }
//        Configuration config = ConfigurationProvider.getConfiguration()
//                .with(ConfigurationFunctions.sectionsRecursive(path.split(",")));
//        if(mediaType.equals(MediaType.APPLICATION_JSON_TYPE)) {
//            return config.query(ConfigurationFunctions.jsonInfo(requestInfo));
//        }
//        if(mediaType.equals(MediaType.APPLICATION_XML_TYPE)) {
//            return config.query(ConfigurationFunctions.xmlInfo(requestInfo));
//        }
//        if(mediaType.equals(MediaType.TEXT_HTML_TYPE)) {
//            return config.query(ConfigurationFunctions.htmlInfo(requestInfo));
//        }
//        if(mediaType.equals(MediaType.TEXT_PLAIN_TYPE)) {
//            return config.query(ConfigurationFunctions.textInfo(requestInfo));
//        }
//        return null;
//    }

}
