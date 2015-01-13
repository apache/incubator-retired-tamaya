/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tamaya;

import org.apache.tamaya.spi.ServiceContextManager;

/**
 * Static access to the {@link org.apache.tamaya.Configuration} for the very application.
 *
 * Exists for Java7 backward compatibility only.
 * @deprecated Since Java 8, you better use {@link org.apache.tamaya.Configuration#current()}.
 */
@Deprecated
public final class ConfigurationProvider {
    private ConfigurationProvider() {
        // just to prevent initialisation
    }

    /**
     * Access the current configuration.
     *
     * @return the corresponding Configuration instance, never null.
     * @deprecated Since Java 8, you better use {@link org.apache.tamaya.Configuration#current()}.
     */
    @Deprecated
    public static Configuration getConfiguration() {
        return ServiceContextManager.getServiceContext().getService(Configuration.class).get();
    }
}
