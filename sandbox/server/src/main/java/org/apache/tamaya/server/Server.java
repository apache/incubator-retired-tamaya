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
package org.apache.tamaya.server;

/**
 * Simple abstraction of the Server interface.
 */
public interface Server {
    /**
     * Starts the server on the given port-
     * @param port the target port.
     */
    void start(int port);

    /**
     * Checks if the server us started.
     * @return true if the server us started.
     */
    boolean isStarted();

    /**
     * Stops the server, but does not destroy it, so it might be restarted.
     */
    void stop();

    /**
     * Destroy the server instance.
     */
    void destroy();
}
