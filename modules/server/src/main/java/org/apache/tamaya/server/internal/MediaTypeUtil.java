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

import javax.ws.rs.core.MediaType;

/**
 * Simple utility class.
 */
public final class MediaTypeUtil {
    /** Singleton constructor. */
    private MediaTypeUtil(){}

    /**
     * Compares the given MIME type String and tries to evaluate the MIME type matching best.
     * Currently it select one of application/xml, application/json, text/plain and text/html.
     * if none is matching application/json is returned by default.
     * @param formats the formats to check.
     * @return the selected MediaType
     */
    public static MediaType getMediaType(String... formats) {
        for(String format:formats) {
            if (format.equalsIgnoreCase(MediaType.APPLICATION_XML)) {
                return MediaType.APPLICATION_XML_TYPE;
            } else if (format.equalsIgnoreCase(MediaType.APPLICATION_JSON)) {
                return MediaType.APPLICATION_JSON_TYPE;
            } else if (format.equalsIgnoreCase(MediaType.TEXT_HTML)) {
                return MediaType.TEXT_HTML_TYPE;
            } else if (format.equalsIgnoreCase(MediaType.TEXT_PLAIN)) {
                return MediaType.TEXT_PLAIN_TYPE;
            }
        }
        for(String format:formats) {
            if (format.contains(MediaType.APPLICATION_XML)) {
                return MediaType.APPLICATION_XML_TYPE;
            } else if (format.contains(MediaType.APPLICATION_JSON)) {
                return MediaType.APPLICATION_JSON_TYPE;
            }
        }
        return MediaType.APPLICATION_JSON_TYPE;
    }
}
