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

import java.util.ArrayList;
import java.util.List;

/**
 * Small helper class that manages the path parts of a location expression.
 */
final class Locator {
    /**
     * The tokenized location expression.
     */
    private List<String> tokens;

    /**
     * Creates a new instances based on the tokenized expression.
     *
     * @param tokens the tokenized expression, not null.
     */
    Locator(List<String> tokens) {
        this.tokens = tokens;
    }

    /**
     * Creates a new instance of the corresponding expression.
     *
     * @param expression the location expression, not null.
     * @return the tokenized instance.
     */
    public static Locator of(String expression) {
        List<String> expressions = new ArrayList<>();
        for (String expr : expression.split("/")) {
            if(expr.length()==0) {
                expressions.add("/");
            } else{
                expressions.add(expr);
            }
        }
        return new Locator(expressions);
    }

    /**
     * Access the root path, which is the location expression, before any wildcards or placeholders are used.
     * It is used as the entry point into the file system or for accessing base classpath resources, before
     * further analysis on the file or jar filesystem can be performed.
     *
     * @return the root path, never null.
     */
    public String getRootPath() {
        StringBuilder sj = new StringBuilder();
        for (String token : this.tokens) {
            if (containsPlaceholder(token)) {
                break;
            } else {
                if(!(sj.length()==0)) {
                    sj.append('/');
                }
                sj.append(token);
            }
        }
        return sj.toString();
    }

    /**
     * Return the sub expression path, which contains the second part of the expression, starting with a placeholder
     * or wildcard token.
     *
     * @return the sub expression part, never null.
     */
    public String getSubPath() {
        StringBuilder sj = new StringBuilder();
        for (String token : getSubPathTokens()) {
            if(!(sj.length()==0)) {
                sj.append('/');
            }
            sj.append(token);
        }
        return sj.toString();
    }

    /**
     * This method returns the single tokenized form of the sub expression.
     *
     * @return the tokenized version of the sub path.
     * @see #getSubPath()
     */
    public List<String> getSubPathTokens() {
        List<String> subTokens = new ArrayList<>();
        boolean subTokensStarted = false;
        for (String token : tokens) {
            if(subTokensStarted){
                subTokens.add(token);
            } else if (containsPlaceholder(token)) {
                subTokensStarted = true;
                subTokens.add(token);
            }
        }
        return subTokens;
    }

    /**
     * Access the full reconstructed path. In most cases this should match the original expression.
     *
     * @return the full expression path, never null.
     */
    public String getPath() {
        StringBuilder sj = new StringBuilder();
        for (String token : tokens) {
            if(!(sj.length()==0)) {
                sj.append('/');
            }
            sj.append(token);
        }
        return sj.toString();
    }

    /**
     * Short method that checks for '*' and '?' chars.
     *
     * @param token the token to check, not null
     * @return true, if it contains wildcard characters.
     */
    private boolean containsPlaceholder(String token) {
        return token.contains("*") || token.contains("?");
    }

    /**
     * Return the expressions' path.
     *
     * @return the locator path.
     */
    @Override
    public String toString() {
        return "Locator: " + getPath();
    }

}
