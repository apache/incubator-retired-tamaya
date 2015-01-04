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
package org.apache.tamaya.core.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

/**
 * Interface for an abstract resource. The effective resource implementation can be completely arbitrary.
 * By default files, classpath resources and URLs are supported, but alternate implementations are possible.
 *
 * @see #getInputStream()
 * @see #toURI()
 */
public interface Resource extends InputStreamSupplier {

    /**
     * Return whether this resource actually exists. Depending on the resource this can delegate to
     * {@link java.io.File#exists()} or whatever may be appropriate to check accessibility of the resource.
     */
    default boolean exists() {
        // Try to open a file first, if that fails try to open the stream...
        try {
            return new File(toURI()).exists();
        } catch (IOException ex) {
            // Fallback
            try {
                InputStream is = getInputStream();
                is.close();
                return true;
            } catch (Exception e) {
                // ignore, just return false for non existing
                return false;
            }
        }
    }

    /**
     * Checks whether the resource is accessible, meaning {@link #getInputStream()} should return a InputStream for reading the
     * resource's content.
     *
     * @see #getInputStream()
     */
    default boolean isAccessible() {
        return true;
    }

    /**
     * Returns the resource as an URI.
     *
     * @throws IOException if the resource cannot be resolved as URI.
     */
    URI toURI() throws IOException;

    /**
     * Determines the length for this resource.
     *
     * @throws IOException if the resource is not readable.
     */
    default long length() throws IOException {
        try(InputStream is = this.getInputStream();) {
            Objects.requireNonNull(is, "resource not available");
            long length = 0;
            byte[] buf = new byte[256];
            int bytesRead;
            while ((bytesRead = is.read(buf)) > 0) {
                length += bytesRead;
            }
            return length;
        }
    }

    /**
     * Determine the last-modified timestamp for a resource, as UTC ms timestamp
     *
     * @throws IOException if the resource is not accessible.
     */
    default long lastModified() throws IOException{
        return new File(toURI()).lastModified();
    }

    /**
     * Get a name for the resource. The name should be identifying the resource and also
     * never change, so it must be eligible for hashcode/equals implementations.
     */
    default String getName() {
        try {
            return toURI().toString();
        } catch (Exception e) {
            return toString();
        }
    }


}