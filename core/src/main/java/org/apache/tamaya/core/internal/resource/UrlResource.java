/*
 * Copyright 2002-2013 the original author or authors.
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
package org.apache.tamaya.core.internal.resource;

import org.apache.tamaya.core.util.StringUtils;
import org.apache.tamaya.core.resources.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * {@link Resource} implementation for {@code java.net.URL} locators.
 * Obviously supports resolution as URL, and also as File in case current
 * the "file:" protocol.
 *
 * @author Juergen Hoeller
 * @see java.net.URL
 * @since 28.12.2003
 */
public class UrlResource extends AbstractFileResolvingResource {

    /**
     * Original URI, if available; used for URI and File access.
     */
    private final URI uri;

    /**
     * Original URL, used for actual access.
     */
    private final URL url;

    /**
     * Cleaned URL (with normalized path), used for comparisons.
     */
    private final URL cleanedUrl;


    /**
     * Create a new UrlResource based on the given URI object.
     *
     * @param uri a URI
     * @throws MalformedURLException if the given URL path is not valid
     */
    public UrlResource(URI uri) throws MalformedURLException {
        Objects.requireNonNull(uri, "URI must not be null");
        this.uri = uri;
        this.url = uri.toURL();
        this.cleanedUrl = getCleanedUrl(this.url, uri.toString());
    }

    /**
     * Create a new UrlResource based on the given URL object.
     *
     * @param url a URL
     */
    public UrlResource(URL url) {
        Objects.requireNonNull(url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = getCleanedUrl(this.url, url.toString());
        this.uri = null;
    }

    /**
     * Create a new UrlResource based on a URL path.
     * <p>Note: The given path needs to be pre-encoded if necessary.
     *
     * @param path a URL path
     * @throws MalformedURLException if the given URL path is not valid
     * @see java.net.URL#URL(String)
     */
    public UrlResource(String path) throws MalformedURLException {
        Objects.requireNonNull(path, "Path must not be null");
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = getCleanedUrl(this.url, path);
    }

    /**
     * Create a new UrlResource based on a URI specification.
     * <p>The given parts will automatically get encoded if necessary.
     *
     * @param protocol the URL protocol to use (e.g. "jar" or "file" - without colon);
     *                 also known as "scheme"
     * @param location the location (e.g. the file path within that protocol);
     *                 also known as "scheme-specific part"
     * @throws MalformedURLException if the given URL specification is not valid
     * @see java.net.URI#URI(String, String, String)
     */
    public UrlResource(String protocol, String location) throws MalformedURLException {
        this(protocol, location, null);
    }

    /**
     * Create a new UrlResource based on a URI specification.
     * <p>The given parts will automatically get encoded if necessary.
     *
     * @param protocol the URL protocol to use (e.g. "jar" or "file" - without colon);
     *                 also known as "scheme"
     * @param location the location (e.g. the file path within that protocol);
     *                 also known as "scheme-specific part"
     * @param fragment the fragment within that location (e.g. anchor on an HTML page,
     *                 as following after a "#" separator)
     * @throws MalformedURLException if the given URL specification is not valid
     * @see java.net.URI#URI(String, String, String)
     */
    public UrlResource(String protocol, String location, String fragment) throws MalformedURLException {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
            this.cleanedUrl = getCleanedUrl(this.url, this.uri.toString());
        } catch (URISyntaxException ex) {
            MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }

    /**
     * Determine a cleaned URL for the given original URL.
     *
     * @param originalUrl  the original URL
     * @param originalPath the original URL path
     * @return the cleaned URL
     */
    private URL getCleanedUrl(URL originalUrl, String originalPath) {
        try {
            return new URL(StringUtils.cleanPath(originalPath));
        } catch (MalformedURLException ex) {
            // Cleaned URL path cannot be converted to URL
            // -> take original URL.
            return originalUrl;
        }
    }


    /**
     * This implementation opens an InputStream for the given URL.
     * It sets the "UseCaches" flag to {@code false},
     * mainly to avoid jar file locking on Windows.
     *
     * @see java.net.URL#openConnection()
     * @see java.net.URLConnection#setUseCaches(boolean)
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = null;
        try {
            con = this.url.openConnection();
            useCachesIfNecessary(con);
            return con.getInputStream();
        } catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    /**
     * This implementation returns the underlying URL reference.
     */
    @Override
    public URL toURL() throws IOException {
        return this.url;
    }

    /**
     * This implementation returns the underlying URI directly,
     * if possible.
     */
    @Override
    public URI getURI() throws IOException {
        if (this.uri != null) {
            return this.uri;
        } else {
            return super.getURI();
        }
    }

    /**
     * This implementation returns a File reference for the underlying URL/URI,
     * provided that it refers to a file in the file system.
     */
    @Override
    public File toFile() throws IOException {
        if (this.uri != null) {
            return super.getFile(this.uri);
        } else {
            return super.toFile();
        }
    }

    /**
     * This implementation creates a UrlResource, applying the given path
     * relative to the path current the underlying URL current this resource descriptor.
     *
     * @see java.net.URL#URL(java.net.URL, String)
     */
    @Override
    public Resource createRelative(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return new UrlResource(new URL(this.url, relativePath));
    }

    /**
     * This implementation returns the name current the file that this URL refers to.
     *
     * @see java.net.URL#getFile()
     * @see java.io.File#getName()
     */
    @Override
    public String getDisplayName() {
        return new File(this.url.getFile()).getName();
    }

    /**
     * This implementation returns a description that includes the URL.
     */
    @Override
    public String toString() {
        return "URL [" + this.url + "]";
    }


    /**
     * This implementation compares the underlying URL references.
     */
    @Override
    public boolean equals(Object obj) {
        return (obj == this ||
                (obj instanceof UrlResource && this.cleanedUrl.equals(((UrlResource) obj).cleanedUrl)));
    }

    /**
     * This implementation returns the hash code current the underlying URL reference.
     */
    @Override
    public int hashCode() {
        return this.cleanedUrl.hashCode();
    }

    /**
     * Set the {@link URLConnection#setUseCaches "useCaches"} flag on the
     * given connection, preferring {@code false} but leaving the
     * flag at {@code true} for JNLP based resources.
     *
     * @param con the URLConnection to set the flag on
     */
    private void useCachesIfNecessary(URLConnection con) {
        con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
    }

}

