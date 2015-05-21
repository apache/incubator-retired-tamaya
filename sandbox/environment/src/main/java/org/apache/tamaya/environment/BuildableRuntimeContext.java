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
package org.apache.tamaya.environment;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Environment class that is used by the {@link org.apache.tamaya.environment.RuntimeContextBuilder}.
 */
class BuildableRuntimeContext implements RuntimeContext {
    /** The context id, never null or empty. */
    private String contextId;

    /**
     * The environment data.
     */
    private Map<String, String> context = new TreeMap<>();

    /**
     * Constructor.
     *
     * @param builder the builder, not null.
     */
    BuildableRuntimeContext(RuntimeContextBuilder builder) {
        Objects.requireNonNull(builder);
        this.contextId = builder.contextId;
        context.putAll(builder.contextData);
    }

    @Override
    public Map<String, String> toMap() {
        return context;
    }

    @Override
    public String getContextId() {
        return contextId;
    }

    @Override
    public String get(String key) {
        return context.get(key);
    }

    @Override
    public String get(String key, String defaultValue) {
        return context.getOrDefault(key, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BuildableRuntimeContext that = (BuildableRuntimeContext) o;
        return context.equals(that.context);
    }

    @Override
    public int hashCode() {
        return context.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RuntimeContext: " + getContextId();
    }

    /**
     * Get the delta.
     *
     * @return
     */
    private String getData() {
        StringBuilder b = new StringBuilder();
        for (Map.Entry<String, String> en : this.context.entrySet()) {
            b.append("    ").append(en.getKey()).append('=').append(escape(en.getValue())).append('\n');
        }
        if (b.length() > 0) {
            b.setLength(b.length() - 1);
        }
        return b.toString();
    }

    /**
     * Escapes several characters.
     *
     * @param value
     * @return
     */
    private String escape(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r").replaceAll("\t", "\\\\t")
                .replaceAll("=", "\\\\=");
    }
}
