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

import org.apache.tamaya.resource.ResourceLocator;

import javax.annotation.Priority;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * {@link ResourceLocator} for locating local files.
 */
@Priority(80)
public class FileResourceLocator implements ResourceLocator{
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(FileResourceLocator.class.getName());

    /**
     * Tries to evaluate the location passed by loading from the classloader.
     * @param classLoader the class loader to use
     * @param expression the path expression
     * @return the resources found.
     */
    @Override
    public Collection<URL> lookup(ClassLoader classLoader, String expression) {
        List<URL> resources = new ArrayList<>();
        try {
            File file = new File(expression);
            if (file.exists()) {
                resources.add(file.toURI().toURL());
            }
            return resources;
        } catch (IOException | RuntimeException e) {
            LOG.finest("Failed to load resource from file: " + expression);
            return Collections.emptySet();
        }
    }

    @Override
    public String toString() {
        return "FileResourceLocator -> file:<expression>, e.g. file:./config/**/*.xml";
    }

}
