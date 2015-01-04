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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

/**
 * Implementation of {@link Resource} to be loaded from the classpath.
 */
public class ClassPathResource implements Resource {

    private final String path;

    private ClassLoader classLoader;


    /**
     * Create a new resource using the current context class loader.
     *
     * @param path the resource path, not null
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     */
    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    /**
     * Create a new resource using the given class loader.
     *
     * @param path        the resource path, not null
     * @param classLoader the class loader to load the resource with,
     *                    or {@code null} for the current context class loader
     * @see ClassLoader#getResourceAsStream(String)
     */
    public ClassPathResource(String path, ClassLoader classLoader) {
        Objects.requireNonNull(path, "Path null");
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        this.path = path.trim();
        if(classLoader==null){
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if(classLoader==null){
            classLoader = getClass().getClassLoader();
        }
        this.classLoader = classLoader;
    }

    /**
     * @return the path for this resource.
     */
    public final String getPath() {
        return this.path;
    }

    /**
     * @return the ClassLoader that this resource will be accessed from.
     */
    public final ClassLoader getClassLoader() {
        return this.classLoader;
    }


    /**
     * Checks if the given resource is resolvable from the configured classloader.
     *
     * @see java.lang.ClassLoader#getResource(String)
     */
    @Override
    public boolean exists() {
        return (resolveURL() != null);
    }

    /**
     * Resolves a URL for the underlying class path resource.
     *
     * @return the resolved URL, or {@code null}
     */
    protected URL resolveURL() {
        return this.classLoader.getResource(this.path);
    }

    /**
     * This implementation opens an InputStream for the given class path resource.
     *
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @see java.lang.Class#getResourceAsStream(String)
     */
    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = this.classLoader.getResourceAsStream(this.path);
        if (is == null) {
            throw new IOException(getName() + " does not exist");
        }
        return is;
    }

    @Override
    public URI toURI() throws IOException {
        try {
            return resolveURL().toURI();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }

    @Override
    public long lastModified() throws IOException {
        return 0;
    }

    /**
     * This implementation returns the name current the file that this class path
     * resource refers to.
     */
    @Override
    public String getName() {
        return "classpath:"+path;
    }

    /**
     * This implementation returns a description that includes the class path location.
     */
    @Override
    public String toString() {
        return "ClassPathResource[" + path + ']';
    }

    /**
     * This implementation compares the underlying class path locations.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ClassPathResource) {
            ClassPathResource otherRes = (ClassPathResource) obj;
            return (this.path.equals(otherRes.path) &&
                    Objects.equals(this.classLoader, otherRes.classLoader));
        }
        return false;
    }

    /**
     * This implementation returns the hash code current the underlying
     * class path location.
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }

}
