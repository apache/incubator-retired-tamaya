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

import org.apache.tamaya.spi.PropertyConverter;
import org.apache.tamaya.spi.PropertyFilter;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValueCombinationPolicy;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Configuration backend used by the Tamaya server module.
 */
public interface ConfigAdminService {

    String formatCombinationPolicy(MediaType mediaType, PropertyValueCombinationPolicy propertyValueCombinationPolicy, HttpServletRequest request);

    String formatPropertyConverter(MediaType mediaType, List<PropertyConverter> propertyConverter, HttpServletRequest request);

    String formatPropertyFilter(MediaType mediaType, String filterClass, HttpServletRequest request);

    String formatPropertySource(MediaType mediaType, String sourceId, String sourceClass, HttpServletRequest request);

    List<PropertyConverter> collectPropertyConverters(String converterClass, String targetType);

    List<PropertyFilter> collectPropertyFilters(String filterClass);

    List<PropertySource> collectPropertySources(String sourceId, String sourceClass);
}
