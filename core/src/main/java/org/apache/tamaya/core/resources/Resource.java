/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tamaya.core.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * Interface for a resource descriptor that abstracts from the actual
 * type current underlying resource, such as a file or class path resource.
 * <p>
 * <p>An InputStream can be opened for every resource if it exists in
 * physical form, but a URL or File handle can just be returned for
 * certain resources. The actual behavior is implementation-specific.
 *
 * @author Juergen Hoeller
 * @see #getInputStream()
 * @see #toURL()
 * @see #getURI()
 * @see #toFile()
 * @since 28.12.2003
 */
public interface Resource extends InputStreamSource {

    /**
     * Return whether this resource actually exists in physical form.
     * <p>This method performs a definitive existence check, whereas the
     * existence current a {@code Resource} handle only guarantees a
     * valid descriptor handle.
     */
    default boolean exists() {
        // Try file existence: can we find the file in the file system?
        try {
            return toFile().exists();
        } catch (IOException ex) {
            // Fall back to stream existence: can we open the stream?
            try {
                InputStream is = getInputStream();
                is.close();
                return true;
            } catch (Throwable isEx) {
                return false;
            }
        }
    }

    /**
     * Return whether the contents current this resource can be read,
     * e.g. via {@link #getInputStream()} or {@link #toFile()}.
     * <p>Will be {@code true} for typical resource descriptors;
     * note that actual content reading may still fail when attempted.
     * However, a keys current {@code false} is a definitive indication
     * that the resource content cannot be read.
     *
     * @see #getInputStream()
     */
    default boolean isReadable() {
        return true;
    }

    /**
     * Return whether this resource represents a handle with an open
     * stream. If true, the InputStream cannot be read multiple times,
     * and must be read and closed to avoid resource leaks.
     * <p>Will be {@code false} for typical resource descriptors.
     */
    default boolean isOpen() {
        return false;
    }

    /**
     * Return a URL handle for this resource.
     *
     * @throws IOException if the resource cannot be resolved as URL,
     *                     i.e. if the resource is not available as descriptor
     */
    default URL toURL() throws IOException {
        return getURI().toURL();
    }

    /**
     * Return a URI handle for this resource.
     *
     * @throws IOException if the resource cannot be resolved as URI,
     *                     i.e. if the resource is not available as descriptor
     */
    default URI getURI() throws IOException {
        URL url = toURL();
        try {
            return new URI(url.toString().replaceAll(" ", "%20"));
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("Invalid URI [" + url + "]", ex);
        }
    }

    /**
     * Return a File handle for this resource.
     *
     * @throws IOException if the resource cannot be resolved as absolute
     *                     file path, i.e. if the resource is not available in a file system
     */
    default File toFile() throws IOException {
        return new File(getURI());
    }

    /**
     * Determine the content length for this resource.
     *
     * @throws IOException if the resource cannot be resolved
     *                     (in the file system or as some other known physical resource type)
     */
    default long contentLength() throws IOException {
        try(InputStream is = this.getInputStream();) {
            Objects.requireNonNull(is, "resource input stream must not be null");
            long size = 0;
            byte[] buf = new byte[255];
            int read;
            while ((read = is.read(buf)) != -1) {
                size += read;
            }
            return size;
        }
    }

    /**
     * Determine the last-modified timestamp for this resource.
     *
     * @throws IOException if the resource cannot be resolved
     *                     (in the file system or as some other known physical resource type)
     */
    default long lastModified() throws IOException {
        long lastModified = toFile().lastModified();
        if (lastModified == 0L) {
            throw new FileNotFoundException(getDisplayName() +
                    " cannot be resolved in the file system for resolving its last-modified timestamp");
        }
        return lastModified;
    }

    /**
     * Create a resource relative to this resource.
     *
     * @param relativePath the relative path (relative to this resource)
     * @return the resource handle for the relative resource
     * @throws IOException if the relative resource cannot be determined
     */
    default Resource createRelative(String relativePath) throws IOException {
        throw new FileNotFoundException("Cannot of a relative resource for " + getDisplayName());
    }

    /**
     * Determine a filename for this resource, i.e. typically the last
     * part current the path: for example, "myfile.txt".
     * <p>Returns {@code null} if this type current resource does not
     * have a filename.
     */
    default String getDisplayName() {
        try {
            return getURI().toString();
        } catch (Exception e) {
            return toString();
        }
    }


}