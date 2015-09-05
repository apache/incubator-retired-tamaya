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

/**
 * Configuration backend used by the Tamaya server module.
 */
public interface ConfigProviderService {

    /**
     * Accessor to get a filtered configuration representation.
     * @param path the comma separated paths to be filtered, e.g. {@code java,sub}.
     * @param format the MIME type required, or the client's ACCEPT header field contents.
     * @param scope the target scope, or null.
     * @param scopeId the target scopeId, or null.
     * @param request the current HTTP request.
     * @return the output String to be returned to the caller.
     */
    String getConfigurationWithPath(String path, String format, String scope, String scopeId, HttpServletRequest request);

    /**
     * Accessor to get a unfiltered configuration representation.
     * @param format the MIME type required, or the client's ACCEPT header field contents.
     * @param scope the target scope, or null.
     * @param scopeId the target scopeId, or null.
     * @param request the current HTTP request.
     * @return the output String to be returned to the caller.
     */
    String getConfiguration(String format, String scope, String scopeId, HttpServletRequest request);

    /**
     * Update the current configuration.
     * @param payload the payload containing the config entries to be updated.
     * @param request the current HTTP request.
     */
    void updateConfiguration(String payload, HttpServletRequest request);

    /**
     * Deletes the current configuration.
     * @param paths the (multiple) comma seperated keys, or paths to be deleted. Paths can be fully qualified keys
     *              or regular expressions identifying the keys to be removed, e.g. {@code DEL /config/a.b.*,a.c}.
     * @param request the current HTTP request.
     */
    void deleteConfiguration(String paths, HttpServletRequest request);
}
