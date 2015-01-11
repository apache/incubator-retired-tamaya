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

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Collector that searches files based on ant styled patterns. For example the following patterns would be matched:
 * <pre>
 *     classpath:javax/annotations/*
 *     javax?/annotations&#47;**&#47;*.class
 *     org/apache/tamaya&#47;**&#47;tamayaconfig.properties
 * </pre>
 */
public class ClasspathCollector {

    /**
     * JAR protocol.
     */
    private static final String PROTOCOL_JAR = "jar";

    /**
     * Separator between JAR file URL and the internal jar file path.
     */
    private static final String JAR_URL_SEPARATOR = "!/";

    /**
     * ZIP protocol.
     */
    private static final String PROTOCOL_ZIP = "zip";

    /**
     * ZIP protocol for a JBoss jar file entry: "vfszip".
     */
    private static final String PROTOCOL_VFSZIP = "vfszip";

    /**
     * URL protocol for an WebSphere jar file: "wsjar".
     */
    private static final String PROTOCOL_WSJAR = "wsjar";

    /**
     * URL protocol for an entry from an OC4J jar.
     */
    private static final String PROTOCOL_CODE_SOURCE = "code-source";

    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(ClasspathCollector.class.getName());

    /**
     * Prefix used for explicitly selecting this collector.
     */
    public static final String CLASSPATH_PREFIX = "classpath:";

    /**
     * The classloader used to load the resources.
     */
    private ClassLoader classLoader;

    /**
     * Creates a new instance.
     *
     * @param classLoader the class loader to be used, not null.
     */
    public ClasspathCollector(ClassLoader classLoader) {
        this.classLoader = Objects.requireNonNull(classLoader);
    }

    /**
     * Collect all classpath resources given the expression.
     *
     * @param expression the expression, not null.
     * @return the resources found.
     */
    public Collection<URL> collectFiles(String expression) {
        if (expression.startsWith(CLASSPATH_PREFIX)) {
            expression = expression.substring(CLASSPATH_PREFIX.length());
        }
        if (expression.startsWith("/")) {
            expression = expression.substring(1);
        }
        Locator locator = Locator.of(expression);
        List<URL> result = new ArrayList<>();
        try {
            Enumeration<URL> rootResources = this.classLoader.getResources(locator.getRootPath());
            while (rootResources.hasMoreElements()) {
                URL resource = rootResources.nextElement();
                try {
                    if (isJarFile(resource)) {
                        result.addAll(doFindPathMatchingJarResources(resource, locator.getSubPath()));
                    } else {
                        result.addAll(FileCollector.traverseAndSelectFromChildren(getFile(resource),
                                locator.getSubPathTokens(), 0));
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error locating resources for: " + expression, e);
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error locating resources for: " + expression, e);
        }
        return result;
    }


    /**
     * Find all resources in jar files that match the given location pattern
     * via the Ant-style PathMatcher.
     *
     * @param rootDirResource the root directory as Resource
     * @param subPattern      the sub pattern to match (below the root directory)
     * @return the Set of matching Resource instances
     * @throws java.io.IOException in case of I/O errors
     * @see java.net.JarURLConnection
     */
    protected Collection<URL> doFindPathMatchingJarResources(URL rootDirResource, String subPattern)
            throws IOException, URISyntaxException {
        subPattern = subPattern.replace("*", ".*").replace("?", ".?").replace(".*.*", ".*");
        URLConnection con = rootDirResource.toURI().toURL().openConnection();
        JarFile jarFile;
        boolean newJarFile = false;
        String jarFileUrl;
        String rootEntryPath;
        boolean isFileExpression = !subPattern.contains("/");

        if (con instanceof JarURLConnection) {
            JarURLConnection jarCon = (JarURLConnection) con;
            jarCon.setUseCaches(false);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        } else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootDirResource.toURI().toURL().getFile();
            int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
            jarFileUrl = urlFile.substring(0, separatorIndex);
            if (jarFileUrl.startsWith("file:")) {
                jarFileUrl = jarFileUrl.substring("file:".length());
            }
            jarFile = new JarFile(jarFileUrl);
            newJarFile = true;
            jarFileUrl = "file:" + jarFileUrl;
            rootEntryPath = urlFile.substring(separatorIndex + JAR_URL_SEPARATOR.length());
        }

        try {
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Looking for matching resources in jar file [" + jarFileUrl + "]");
            }
            if (!rootEntryPath.isEmpty() && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash for correct matching
                rootEntryPath = rootEntryPath + '/';
            }
            Collection<URL> result = new ArrayList<>(10);
            for (Enumeration entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = (JarEntry) entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath)) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if(relativePath.contains("/") && isFileExpression){
                        continue;
                    }
                    if (relativePath.matches(subPattern)) {
                        result.add(createRelativeFrom(rootDirResource, relativePath));
                    }
                }
            }
            return result;
        } finally {
            // Close jar file, but only if freshly obtained -
            // not from JarURLConnection, which might cache the file reference.
            if (newJarFile) {
                jarFile.close();
            }
        }
    }

    /**
     * Creates a new URL based on the given root path and the relative path to be added.
     *
     * @param url          the root, not null
     * @param relativePath the relative path to be added, not null
     * @return the new URL instance
     * @throws MalformedURLException
     */
    private URL createRelativeFrom(URL url, String relativePath)
            throws MalformedURLException {
        String rootDirResource = url.toExternalForm();
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        if (!rootDirResource.endsWith("/")) {
            rootDirResource = rootDirResource + '/';
        }
        return new URL(rootDirResource + relativePath);
    }


    /**
     * Small check if a given URL is a jar file URL.
     *
     * @param url the URL to check, not null.
     * @return true if the URL has one of the following protocols: jar, zip, vfszip, wsjar, code-source.
     */
    private boolean isJarFile(URL url) {
        String protocol = Objects.requireNonNull(url).getProtocol();
        return (PROTOCOL_JAR.equals(protocol) ||
                PROTOCOL_ZIP.equals(protocol) ||
                PROTOCOL_VFSZIP.equals(protocol) ||
                PROTOCOL_WSJAR.equals(protocol) ||
                (PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().indexOf(JAR_URL_SEPARATOR) != -1));
    }

    /**
     * Creates a file from an URL.
     *
     * @param resourceUrl the url, not null.
     * @return a new file instance. The instance still may not exist. if the url's protocol is not 'file', {@code null}
     * is returned.
     */
    private File getFile(URL resourceUrl) {
        Objects.requireNonNull(resourceUrl, "Resource URL must not be null");
        if (!"file".equals(resourceUrl.getProtocol())) {
            return null;
        }
        try {
            return new File(resourceUrl.toURI().getSchemeSpecificPart());
        } catch (Exception ex) {
            // Fallback for URLs that are not valid URIs (should hardly ever happen).
            return new File(resourceUrl.getFile());
        }
    }

}
