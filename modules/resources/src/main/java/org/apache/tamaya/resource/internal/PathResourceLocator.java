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
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

/**
 * Created by atsticks on 01.03.16.
 */
@Priority(100)
public class PathResourceLocator implements ResourceLocator{
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(PathResourceLocator.class.getName());

    /**
     * Tries to evaluate the location passed by Ant path matching.
     * @param classLoader the class loader to use
     * @param expression the path expression
     * @return true, if the expression could be resolved.
     */
    @Override
    public Collection<URL> lookup(ClassLoader classLoader, String expression) {
        try {
            // 1: try file path
            Collection<URL> found = FileCollector.collectFiles(expression);
            if (found.isEmpty()) {
                found = new ClasspathCollector(classLoader).collectFiles(expression);
            }
            return found;
        } catch (RuntimeException e) {
            LOG.finest("Failed to load resource from CP: " + expression);
            return Collections.emptySet();
        }
    }

    @Override
    public String toString() {
        return "PathResourceLocator -> <fileExpression>,<classpathExpression>, e.g. /**/*.xml";
    }

}
