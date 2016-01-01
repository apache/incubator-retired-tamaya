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
package org.apache.tamaya.resolver.internal;

import org.apache.tamaya.resolver.spi.ExpressionResolver;
import org.apache.tamaya.resource.ResourceResolver;
import org.apache.tamaya.spi.ServiceContextManager;

import javax.annotation.Priority;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <p>Property resolver implementation that tries to load the given resource from the current file system.</p>
 *
 * <p>If the {@code Resources} module is available this module is used for resolving the expression. It can be
 * explicitly addressed by prefixing {@code file:}, e.g. {@code ${file:c:/temp/mytext.txt}}.</p>
 */
@Priority(400)
public final class FileResolver implements ExpressionResolver {
    /**
     * The looger used.
     */
    private final Logger LOG = Logger.getLogger(FileResolver.class.getName());

    /**
     * Flag that controls if the Tamaya Resource loader is available.
     */
    private static final boolean IS_RESOURCE_MODULE_AVAILABLE = checkResourceModule();

    /**
     * Checks if the Tamaya ResourceLoader can be loaded from the classpath.
     *
     * @return true, if the module is available.
     */
    private static boolean checkResourceModule() {
        try {
            Class.forName("org.apache.tamaya.resource.ResourceResolver.", false, FileResolver.class.getClassLoader());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String getResolverPrefix() {
        return "file:";
    }

    @Override
    public String evaluate(String expression) {
        URL url = getUrl(expression);

        try (InputStreamReader streamReader = new InputStreamReader(url.openStream(), UTF_8);
             BufferedReader bufferedReader = new BufferedReader(streamReader)) {
            StringBuilder builder = new StringBuilder();
            String inputLine;

            while ((inputLine = bufferedReader.readLine()) != null) {
                builder.append(inputLine).append("\n");
            }

            return builder.toString();
        } catch (Exception e) {
            LOG.log(Level.FINEST, "Could not resolve URL: " + expression, e);
            return null;
        }
    }

    private URL getUrl(String expression) {
        if (IS_RESOURCE_MODULE_AVAILABLE) {
            ResourceResolver resolver = ServiceContextManager.getServiceContext().getService(ResourceResolver.class);
            Collection<URL> resources = resolver.getResources("file:" + expression);
            if (!resources.isEmpty()) {
                if (resources.size() != 1) {
                    LOG.log(Level.WARNING, "Unresolvable expression (ambiguous resource): " + expression);
                    return null;
                }
                return resources.iterator().next();
            }
        } else {
            File file = new File(expression);
            if (file.exists()) {
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException e) {
                    LOG.log(Level.WARNING, "Unresolvable expression (cannot convert file to URL): " + expression, e);
                }
            }
        }
        return null; // no such resource found
    }

}
