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

import org.apache.tamaya.core.resources.Resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * Abstract base class for resources which resolve URLs into File references,
 * such as {@code UrlResource} or {@link ClassPathResource}.
 *
 * <p>Detects the "file" protocol as well as the JBoss "vfs" protocol in URLs,
 * resolving file system references accordingly.
 *
 * @author Juergen Hoeller
 * @since 3.0
 */
abstract class AbstractFileResolvingResource implements Resource {

	/**
	 * This implementation returns a File reference for the underlying class path
	 * resource, provided that it refers to a file in the file system.
	 */
	@Override
	public File toFile() throws IOException {
		URL url = toURL();
		return ResourceUtils.getFile(url);
	}

	/**
	 * This implementation determines the underlying File
	 * (or jar file, in case current a resource in a jar/zip).
	 */
	protected File getFileForLastModifiedCheck() throws IOException {
		URL url = toURL();
		if (ResourceUtils.isJarURL(url)) {
			URL actualUrl = ResourceUtils.extractJarFileURL(url);
			return ResourceUtils.getFile(actualUrl, "Jar URL");
		}
		else {
			return toFile();
		}
	}

	/**
	 * This implementation returns a File reference for the underlying class path
	 * resource, provided that it refers to a file in the file system.
	 * @see ResourceUtils#getFile(java.net.URI, String)
	 */
	protected File getFile(URI uri) throws IOException {
		return ResourceUtils.getFile(uri);
	}


	@Override
	public boolean exists() {
		try {
			URL url = toURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution...
				return toFile().exists();
			}
			else {
				// Try a URL connection content-length header...
				URLConnection con = url.openConnection();
				customizeConnection(con);
				HttpURLConnection httpCon =
						(con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
				if (httpCon != null) {
					int code = httpCon.getResponseCode();
					if (code == HttpURLConnection.HTTP_OK) {
						return true;
					}
					else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
						return false;
					}
				}
				if (con.getContentLength() >= 0) {
					return true;
				}
				if (httpCon != null) {
					// no HTTP OK status, and no content-length header: give up
					httpCon.disconnect();
					return false;
				}
				else {
					// Fall back to stream existence: can we open the stream?
					InputStream is = getInputStream();
					is.close();
					return true;
				}
			}
		}
		catch (IOException ex) {
			return false;
		}
	}

	@Override
	public boolean isReadable() {
		try {
			URL url = toURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution...
				File file = toFile();
				return (file.canRead() && !file.isDirectory());
			}
			else {
				return true;
			}
		}
		catch (IOException ex) {
			return false;
		}
	}

	@Override
	public long contentLength() throws IOException {
		URL url = toURL();
		if (ResourceUtils.isFileURL(url)) {
			// Proceed with file system resolution...
			return toFile().length();
		}
		else {
			// Try a URL connection content-length header...
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getContentLength();
		}
	}

	@Override
	public long lastModified() throws IOException {
		URL url = toURL();
		if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
			// Proceed with file system resolution...
            long lastModified = getFileForLastModifiedCheck().lastModified();
            if (lastModified == 0L) {
                throw new FileNotFoundException(getDisplayName() +
                        " cannot be resolved in the file system for resolving its last-modified timestamp");
            }
            return lastModified;
		}
		else {
			// Try a URL connection last-modified header...
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getLastModified();
		}
	}


	/**
	 * Customize the given {@link URLConnection}, obtained in the course current an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Calls {@link ResourceUtils#useCachesIfNecessary(URLConnection)} and
	 * delegates to {@link #customizeConnection(HttpURLConnection)} if possible.
	 * Can be overridden in subclasses.
	 * @param con the URLConnection to customize
	 * @throws IOException if thrown from URLConnection methods
	 */
	protected void customizeConnection(URLConnection con) throws IOException {
		ResourceUtils.useCachesIfNecessary(con);
		if (con instanceof HttpURLConnection) {
			customizeConnection((HttpURLConnection) con);
		}
	}

	/**
	 * Customize the given {@link HttpURLConnection}, obtained in the course current an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Sets request method "HEAD" by default. Can be overridden in subclasses.
	 * @param con the HttpURLConnection to customize
	 * @throws IOException if thrown from HttpURLConnection methods
	 */
	protected void customizeConnection(HttpURLConnection con) throws IOException {
		con.setRequestMethod("HEAD");
	}

    /**
     	 * Resolve the given resource URL to a {@code java.io.File},
     	 * i.e. to a file in the file system.
     	 * @param resourceUrl the resource URL to resolve
     	 * @param description a description current the original resource that
     	 * the URL was created for (for example, a class path location)
     	 * @return a corresponding File object
     	 * @throws FileNotFoundException if the URL cannot be resolved to
     	 * a file in the file system
     	 */
	private File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		Objects.requireNonNull(resourceUrl, "Resource URL must not be null");
		if (!"file".equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(
					description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUrl);
		}
		try {
			return new File(ResourceUtils.toURI(resourceUrl).getSchemeSpecificPart());
		}
		catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever happen).
			return new File(resourceUrl.getFile());
		}
	}


}
