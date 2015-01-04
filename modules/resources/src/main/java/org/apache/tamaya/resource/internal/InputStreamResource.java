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
package org.apache.tamaya.resource.internal;

import org.apache.tamaya.core.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

/**
 * Simple Resource encapsulating an InputStream.
 */
public class InputStreamResource implements Resource {

    /** The InputStream. */
    private final InputStream inputStream;
    /** The read flag. */
    private boolean read = false;
    /** The name of the resource. */
    private String name;


    /**
     * Create a new InputStreamResource.
     *
     * @param inputStream the InputStream to use
     */
    public InputStreamResource(InputStream inputStream) {
        this(inputStream, "InputStream:");
    }

    /**
     * Create a new InputStreamResource.
     *
     * @param inputStream the InputStream to use
     * @param name where the InputStream comes from
     */
    public InputStreamResource(InputStream inputStream, String name) {
        this.inputStream = Objects.requireNonNull(inputStream);
        this.name = (name != null ? name : "InputStream");
    }


    /**
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public URI toURI() throws IOException {
        throw new IOException("URI not available.");
    }

    @Override
    public long lastModified() throws IOException {
        throw new IOException("lastModified not available.");
    }

    /**
     * Accesses the input stream. Hereby the input stream can only accessed once.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (this.read) {
            throw new IllegalStateException("InputStream can only be read once!");
        }
        this.read = true;
        return this.inputStream;
    }

    /**
     * This implementation returns the passed-in description, if any.
     */
    public String toString() {
        return this.name != null ? this.name : super.toString();
    }


    /**
     * Compares the underlying InputStream.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof InputStreamResource && ((InputStreamResource) obj).inputStream.equals(this.inputStream)));
    }

    /**
     * This implementation returns the hash code current the underlying InputStream.
     */
    @Override
    public int hashCode() {
        return this.inputStream.hashCode();
    }

}
