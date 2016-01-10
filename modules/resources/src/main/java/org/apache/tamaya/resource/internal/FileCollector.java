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
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Collector that searches files based on ant styled patterns. For example the following patterns would be matched:
 * <pre>
 *     file:C:/temp/*.txt
 *     file:C:\**\*.ini
 *     C:\Programs\**&#47;*.ini
 *     /user/home/A*b101_?.pid
 *     /var/logs&#47;**&#47;*.log
 * </pre>
 */
public class FileCollector {
    /** The prefix used to explicitly select this collector. */
    public static final String FILE_PREFIX = "file:";

    /** The logger instance. */
    private static final Logger LOG = Logger.getLogger(FileCollector.class.getName());

    /**
     * private constructor.
     */
    private FileCollector() {
    }

    /**
     * Collects the files given the expression.
     * @param expression the expression in Ant-styled format, not null.
     * @return the URLs found.
     */
    public static Collection<URL> collectFiles(String expression) {
        expression = expression.replace("\\", "/");
        Locator locator = Locator.of(expression);
        List<URL> result = new ArrayList<>();
        String rootPath = locator.getRootPath();
        if (rootPath.startsWith(FILE_PREFIX)) {
            rootPath = rootPath.substring(FILE_PREFIX.length());
        }
        File file = new File(rootPath);
        if (file.exists()) {
            List<String> subTokens = locator.getSubPathTokens();
            result.addAll(traverseAndSelectFromChildren(file, subTokens, 0));
        }
        return result;
    }

    /**
     * Internal method to traverse the file system down, hereby comparing the new path elements with the
     * elements given by {@code subTokens}, starting at the given {@code tokenIndex}.
     * @param dir the directory to start
     * @param subTokens the overall subtoken to be analyzed
     * @param tokenIndex the index where in the token list to start comparing
     * @return the URLs matching the tokens
     */
    static Collection<URL> traverseAndSelectFromChildren(File dir, List<String> subTokens, int tokenIndex) {
        if (tokenIndex >= subTokens.size() || dir.isFile() || !dir.isDirectory()) {
            return Collections.emptyList();
        }
        List<URL> result = new ArrayList<>();
        String token = subTokens.get(tokenIndex);
        if (token.equals("**")) {
            result.addAll(traverseAndSelectFromChildren(dir, getSubExpression(subTokens, tokenIndex + 1)));
        } else {
            token = token.replace("*", ".*").replace("?", ".?");
            File[] files = dir.listFiles();
            if (tokenIndex == subTokens.size() - 1) {
                // select files!
                if(files!=null) {
                    for (File f : files) {
                        if (f.isFile() && f.getName().matches(token)) {
                            result.add(getURL(f));
                        }
                    }
                }
            } else {
                // check directory pattern
                if(files!=null) {
                    for (File f : files) {
                        if (f.isDirectory() && f.getName().matches(token)) {
                            result.addAll(traverseAndSelectFromChildren(f, subTokens, tokenIndex + 1));
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Internal method to traverse the file system and comparing all child file names with the given expression.
     * @param file the root directory
     * @param expression the regular expression to match
     * @return the URLs matching the expression
     */
    static Collection<URL> traverseAndSelectFromChildren(File file, String expression) {
        List<URL> result = new ArrayList<>();
        File[] files = file.listFiles();
        if(files==null){
            return result;
        }
        for (File childFile : files) {
            if (childFile.isFile()) {
                if (childFile.getName().matches(expression)) {
                    try {
                        result.add(getURL(childFile));
                    } catch (Exception e) {
                        LOG.warning("File not convertible to URL: " + childFile);
                    }
                }
            } else if (childFile.isDirectory()) {
                result.addAll(traverseAndSelectFromChildren(childFile, expression));
            }
        }
        return result;
    }

    /**
     * Get an URL from a file.
     *
     * @param file the file, not null.
     * @return the URL, never null.
     * @throws java.lang.IllegalStateException if it fails to create the URL
     */
    private static URL getURL(File file) {
        Objects.requireNonNull(file);
        try {
            return file.toURI().toURL();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create URL from file: " + file);
        }
    }

    /**
     * Constructs a sub expression, using the tokens from {@code subTokens} starting at index {@code startIndex}.
     *
     * @param subTokens  the token list, not null
     * @param startIndex the start index from where tokens should be taken to produce the path.
     * @return the constructed path, never null.
     */
    private static String getSubExpression(List<String> subTokens, int startIndex) {
        StringBuilder b = new StringBuilder();
        for (int i = startIndex; i < subTokens.size(); i++) {
            b.append(subTokens.get(i));
            b.append('/');
        }
        if (b.length() > 0) {
            b.setLength(b.length() - 1);
        }
        return b.toString().replaceAll("\\*", ".*").replaceAll("\\?", ".?");
    }
}
