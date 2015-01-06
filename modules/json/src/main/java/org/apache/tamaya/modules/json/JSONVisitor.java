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
package org.apache.tamaya.modules.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import org.apache.tamaya.ConfigException;

import java.util.*;

public class JSONVisitor {
    private final ObjectNode rootNode;
    private final HashMap<String, String> targetStore;

    public JSONVisitor(ObjectNode startNode, HashMap<String, String> target) {
        rootNode = startNode;
        targetStore = target;
    }

    public void run() {
        Deque<VisitingContext> stack = new ArrayDeque<>();

        stack.add(new VisitingContext(rootNode));
        boolean goOn = stack.peek().hasNext();

        if (goOn) {
            do {
                Map.Entry<String, JsonNode> current = stack.peek().nextElement();

                if (current.getValue() instanceof ValueNode) {
                    String key = stack.peek().getNSPrefix() + current.getKey();
                    String value = current.getValue().asText();
                    targetStore.put(key, value);
                } else if (current.getValue() instanceof ObjectNode) {
                    String key = stack.peek().getNSPrefix() + current.getKey();
                    ObjectNode node = (ObjectNode) current.getValue();
                    stack.push(new VisitingContext(node, key));
                } else {
                    throw new ConfigException("Internal failure while processing JSON document.");
                }

                goOn = stack.peek().hasNext();

                while (!goOn && stack.size() > 0) {
                    stack.remove();
                    goOn = stack.size() > 0 ? stack.peek().hasNext() : false;
                }
            } while (goOn);
        }
    }

    private class VisitingContext {
        private String namespace;
        private final ObjectNode node;
        private final Iterator<Map.Entry<String, JsonNode>> elements;

        public VisitingContext(ObjectNode node) {
            this(node, "");
        }

        public VisitingContext(ObjectNode rootNode, String currentNamespace) {
            namespace = currentNamespace;
            node = rootNode;
            elements = node.fields();
        }

        public Map.Entry<String, JsonNode> nextElement() {
            return elements.next();
        }


        public boolean hasNext() {
            boolean hasNext = elements.hasNext();
            return hasNext;
        }

        public String getNSPrefix() {
            return namespace.isEmpty() ? namespace : namespace + ".";
        }
    }
}
