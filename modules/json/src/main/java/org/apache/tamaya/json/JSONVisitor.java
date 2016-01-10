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
package org.apache.tamaya.json;

import org.apache.tamaya.ConfigException;

import java.util.*;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

/**
 * Visitor implementation to read a JSON formatted input source.
 */
class JSONVisitor {
    private final JsonObject rootNode;
    private final Map<String, String> targetStore;

    JSONVisitor(JsonObject startNode, Map<String, String> target) {
        rootNode = startNode;
        targetStore = target;
    }

    public void run() {
        Deque<VisitingContext> stack = new ArrayDeque<>();

        stack.add(new VisitingContext(rootNode));
        boolean goOn = stack.peek().hasNext();

        if (goOn) {
            do {
                Map.Entry<String, JsonValue> current = stack.peek().nextElement();

                if (!(current.getValue() instanceof JsonStructure)) {
                    String key = stack.peek().getNSPrefix() + current.getKey();
                    String value;
                    JsonValue jsonValue = current.getValue();
                    switch(jsonValue.getValueType()) {
                        case NULL: value = null; break;
                        case FALSE: value = Boolean.FALSE.toString(); break;
                        case TRUE: value = Boolean.TRUE.toString(); break;
                        case NUMBER: value = jsonValue.toString(); break;
                        case STRING: value = ((JsonString) jsonValue).getString(); break;
                        default:
                            throw new ConfigException("Internal failure while processing JSON document.");
                    }
                    
                    targetStore.put(key, value);
                } else if (current.getValue() instanceof JsonObject) {
                    String key = stack.peek().getNSPrefix() + current.getKey();
                    JsonObject node = (JsonObject) current.getValue();
                    stack.push(new VisitingContext(node, key));
                } else if (current.getValue() instanceof JsonArray) {
                    throw new ConfigException("Arrays are not supported at the moment.");
                } else {
                    throw new ConfigException("Internal failure while processing JSON document.");
                }

                goOn = stack.peek().hasNext();

                while (!goOn && stack.size() > 0) {
                    stack.remove();
                    goOn = (stack.size() > 0) && stack.peek().hasNext();
                }
            } while (goOn);
        }
    }

    /**
     * Context for a sub context visited.
     */
    private static class VisitingContext {
        private final String namespace;
        private final JsonObject node;
        private final Iterator<Map.Entry<String, JsonValue>> elements;

        public VisitingContext(JsonObject node) {
            this(node, "");
        }

        public VisitingContext(JsonObject rootNode, String currentNamespace) {
            namespace = currentNamespace;
            node = rootNode;
            elements = node.entrySet().iterator();
        }

        public Map.Entry<String, JsonValue> nextElement() {
            return elements.next();
        }


        public boolean hasNext() {
            return elements.hasNext();
        }

        public String getNSPrefix() {
            return namespace.isEmpty() ? namespace : namespace + ".";
        }
    }
}
