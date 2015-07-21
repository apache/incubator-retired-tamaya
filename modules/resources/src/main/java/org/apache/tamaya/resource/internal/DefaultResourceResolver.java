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

import org.apache.tamaya.resource.BaseResourceResolver;

import javax.annotation.Priority;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

/**
 * Simple default implementation of the resource loader, which does only support direct references to files.
 */
@Priority(0)
public class DefaultResourceResolver extends BaseResourceResolver {

    private static final Logger LOG = Logger.getLogger(DefaultResourceResolver.class.getName());

    @Override
    public List<URL> getResources(ClassLoader classLoader, Collection<String> expressions) {
        List<URL> resources = new ArrayList<>();
        for (String expression : expressions) {
            if (tryPath(classLoader, expression, resources) || tryClassPath(classLoader, expression, resources) || tryFile(expression, resources) ||
                    tryURL(expression, resources)) {
                continue;
            }
            LOG.warning("Failed to resolve resource: " + expression);
        }
        return resources;
    }

    /**
     * Tries to evaluate the location passed by Ant path matching.
     * @param classLoader the class loader to use
     * @param expression the path expression
     * @param resources the resources for adding the results
     * @return true, if the expression could be resolved.
     */
    private boolean tryPath(ClassLoader classLoader, String expression, List<URL> resources) {
        try {
            // 1: try file path
            Collection<URL> found = FileCollector.collectFiles(expression);
            if (found.isEmpty()) {
                found = new ClasspathCollector(classLoader).collectFiles(expression);
            }
            resources.addAll(found);
            return !found.isEmpty();
        } catch (RuntimeException e) {
            LOG.finest("Failed to load resource from CP: " + expression);
        }
        return false;
    }

    /**
     * Tries to evaluate the location passed by loading from the classloader.
     * @param classLoader the class loader to use
     * @param expression the path expression
     * @param resources the resources for adding the results
     * @return true, if the expression could be resolved.
     */
    private boolean tryClassPath(ClassLoader classLoader, String expression, List<URL> resources) {
        try {
            Enumeration<URL> urls = classLoader.getResources(expression);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                resources.add(url);
            }
            return !resources.isEmpty();
        } catch (IOException | RuntimeException e) {
            LOG.finest("Failed to load resource from CP: " + expression);
        }
        return false;
    }

    /**
     * Tries to evaluate the location passed by lokking up a file.
     * @param expression the path expression
     * @param resources the resources for adding the results
     * @return true, if the expression could be resolved.
     */
    private boolean tryFile(String expression, List<URL> resources) {
        try {
            File file = new File(expression);
            if (file.exists()) {
                resources.add(file.toURI().toURL());
                return true;
            }
        } catch (IOException | RuntimeException e) {
            LOG.finest("Failed to load resource from file: " + expression);
        }
        return false;
    }

    /**
     * Tries to interpret the expression as URL.
     * @param expression the path expression
     * @param resources the resources for adding the results
     * @return true, if the expression could be resolved.
     */
    private boolean tryURL(String expression, List<URL> resources) {
        try {
            URL url = new URL(expression);
            resources.add(url);
            return true;
        } catch (IOException | RuntimeException e) {
            LOG.finest("Failed to load resource from file: " + expression);
        }
        return false;
    }

}
