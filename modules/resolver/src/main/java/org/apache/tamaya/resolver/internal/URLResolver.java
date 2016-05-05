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

import javax.annotation.Priority;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Property resolver implementation that interprets the resolver expression as an URL to be resolved.
 * It can be explicitly addressed by prefixing {@code url:}, e.g. {@code ${url:http//www.oracle.com}}.
 */
@Priority(500)
public final class URLResolver implements ExpressionResolver {

    private final Logger LOG = Logger.getLogger(URLResolver.class.getName());

    @Override
    public String getResolverPrefix() {
        return "url:";
    }

    @Override
    public String evaluate(String expression) {
        try {
            URL url = new URL(expression);
            try (InputStreamReader inputStreamReader = new InputStreamReader(url.openStream(), UTF_8);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader))
            {
                StringBuilder builder = new StringBuilder();
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    builder.append(inputLine).append("\n");
                }
                return builder.toString();
            }
        } catch (Exception e) {
            LOG.log(Level.FINEST, "Could not resolve URL: " + expression, e);
            return null;
        }
    }

}
