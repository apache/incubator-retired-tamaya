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
package org.apache.tamaya.extras.json;

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
        Queue<VisitingContext> stack = new ArrayDeque<>();

        stack.add(new VisitingContext(rootNode));

        while(!stack.peek().completed()) {
            Map.Entry<String, JsonNode> current = stack.peek().nextElement();

            if (current.getValue() instanceof ValueNode) {
                System.out.println("I:" + current.getValue().asText());
            } else {
                // @todo
                throw new ConfigException("");
            }

            System.out.println(current);
        }


    }

    private class VisitingContext {
        private final ObjectNode node;
        private final Iterator<Map.Entry<String, JsonNode>> elements;

        public VisitingContext(ObjectNode rootNode) {
            node = rootNode;
            elements = node.fields();
        }

        public Map.Entry<String, JsonNode> nextElement() {
            return elements.next();
        }


        public boolean completed() {
            return elements.hasNext() == false;
        }
    }
}
