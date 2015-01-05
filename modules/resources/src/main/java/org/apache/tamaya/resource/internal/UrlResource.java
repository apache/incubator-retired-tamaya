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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a resource based on a {@code java.net.URL}.
 */
public class UrlResource implements Resource {

    private static final Logger LOG = Logger.getLogger(UrlResource.class.getName());

    /**
     * Original URL, used for actual access.
     */
    private final URL url;

    /**
     * Create a new instance based on the given URL.
     *
     * @param url a URL
     */
    public UrlResource(URL url) {
        this.url = Objects.requireNonNull(url, "URL null");
    }

    /**
     * Create a new URLResource based on a URL path.
     *
     * @param path a URL path
     * @throws MalformedURLException if the given URL path is not valid
     * @see java.net.URL#URL(String)
     */
    public UrlResource(String path) throws MalformedURLException {
        Objects.requireNonNull(path, "Path must not be null");
        this.url = new URL(path);
    }


    /**
     * This implementation opens an InputStream for the given URL.
     *
     * @see java.net.URL#openConnection()
     * @see java.net.URLConnection#setUseCaches(boolean)
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream get() {
        URLConnection con = null;
        try {
            con = this.url.openConnection();
            useCachesIfNecessary(con);
            return con.getInputStream();
        } catch (IOException e) {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            LOG.log(Level.INFO, "Failed to open URL: " + url, e);
            return null;
        }
    }

    @Override
    public URI toURI() throws IOException {
        try {
            return this.url.toURI();
        } catch (URISyntaxException e) {
            throw new IOException("Failed to create URI from " + url);
        }
    }

    @Override
    public String getName() {
        return this.url.toString();
    }

    @Override
    public String toString() {
        return "URL [" + this.url + "]";
    }

    /**
     * Compares the underlying URL references.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof UrlResource && this.url.equals(((UrlResource) obj).url)));
    }

    /**
     * This implementation returns the hash code current the underlying URL reference.
     */
    @Override
    public int hashCode() {
        return this.url.hashCode();
    }

    /**
     * Set the {@link URLConnection#setUseCaches "useCaches"} flag on the
     * given connection, preferring {@code false} but leaving the
     * flag at {@code true} for JNLP based format.
     *
     * @param con the URLConnection to set the flag on
     */
    private void useCachesIfNecessary(URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }

}

