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

import org.apache.tamaya.resource.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of {@link Resource} to be loaded from a file.
 *
 * @see java.io.File
 */
public class FileResource implements Resource {

    private static final Logger LOG = Logger.getLogger(FileResource.class.getName());

    private final File file;

    /**
     * Creates a new instance.
     *
     * @param file a File, not null.
     */
    public FileResource(File file) {
        this.file = Objects.requireNonNull(file, "File must not be null");
    }

    /**
     * Crreates a new instance.
     *
     * @param filePath a file path
     */
    public FileResource(String filePath) {
        Objects.requireNonNull(filePath, "Path must not be null");
        this.file = new File(filePath);
    }


    /**
     * Get the file path for this resource.
     */
    public final String getPath() {
        return this.file.getPath();
    }


    /**
     * This implementation returns whether the underlying file exists.
     *
     * @see java.io.File#exists()
     */
    @Override
    public boolean exists() {
        return this.file.exists();
    }

    /**
     * This implementation checks whether the underlying file is marked as readable
     * (and corresponds to an actual file with content, not to a directory).
     *
     * @see java.io.File#canRead()
     * @see java.io.File#isDirectory()
     */
    @Override
    public boolean isAccessible() {
        return (this.file.canRead() && !this.file.isDirectory());
    }

    /**
     * This implementation opens a FileInputStream for the underlying file.
     *
     * @see java.io.FileInputStream
     */
    @Override
    public InputStream get() {
        try {
            return new FileInputStream(this.file);
        } catch (Exception e) {
            LOG.log(Level.INFO, "Failed to open file: " + file.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * This implementation returns a URI for the underlying file.
     *
     * @see java.io.File#toURI()
     */
    @Override
    public URI toURI() throws IOException {
        return this.file.toURI();
    }

    /**
     * Returns the underlying File's length.
     */
    @Override
    public long length() throws IOException {
        return this.file.length();
    }

    @Override
    public long lastModified() throws IOException {
        return file.lastModified();
    }

    /**
     * Returns the name of the current file.
     *
     * @see java.io.File#getName()
     */
    @Override
    public String getName() {
        return this.file.getName();
    }

    /**
     * Returns a description that includes the absolute
     * path of the current file.
     *
     * @see java.io.File#getAbsolutePath()
     */
    @Override
    public String toString() {
        return "File [" + this.file.getAbsolutePath() + "]";
    }


    // implementation current WritableResource

    /**
     * Compares the underlying Files.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof FileResource && this.file.equals(((FileResource) obj).file)));
    }

    /**
     * Returns hash code current the underlying File reference.
     */
    @Override
    public int hashCode() {
        return this.file.hashCode();
    }

}
