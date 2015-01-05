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
import org.apache.tamaya.resource.ResourceResolver;

import javax.annotation.Priority;
import java.io.File;
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
public class DefaultResourceResolver implements ResourceResolver {

    private static final Logger LOG = Logger.getLogger(DefaultResourceResolver.class.getName());

    @Override
    public List<Resource> getResources(ClassLoader classLoader, Collection<String> expressions) {
        List<Resource> resources = new ArrayList<>();
        for (String expression : expressions) {
            if (tryClassPath(classLoader, expression, resources) || tryFile(expression, resources) ||
                    tryURL(expression, resources)) {
                continue;
            }
            LOG.warning("Failed to resolve resource: " + expression);
        }
        return resources;
    }

    private boolean tryClassPath(ClassLoader classLoader, String expression, List<Resource> resources) {
        try {
            Enumeration<URL> urls = classLoader.getResources(expression);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                resources.add(new UrlResource(url));
            }
            return !resources.isEmpty();
        } catch (Exception e) {
            LOG.finest(() -> "Failed to load resource from CP: " + expression);
        }
        return false;
    }

    private boolean tryFile(String expression, List<Resource> resources) {
        try {
            File file = new File(expression);
            if (file.exists()) {
                resources.add(new FileResource(file));
                return true;
            }
        } catch (Exception e) {
            LOG.finest(() -> "Failed to load resource from file: " + expression);
        }
        return false;
    }

    private boolean tryURL(String expression, List<Resource> resources) {
        try {
            URL url = new URL(expression);
            resources.add(new UrlResource(url));
            return true;
        } catch (Exception e) {
            LOG.finest(() -> "Failed to load resource from file: " + expression);
        }
        return false;

    }

}
