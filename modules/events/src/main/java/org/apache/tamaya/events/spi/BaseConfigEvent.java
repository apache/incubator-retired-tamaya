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
package org.apache.tamaya.events.spi;

import org.apache.tamaya.events.ConfigEvent;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for implementing your own configuration events.
 * @param <T> the vent type
 */
public abstract class BaseConfigEvent<T> implements ConfigEvent<T> {
        private long timestamp = System.currentTimeMillis();
        protected String version = UUID.randomUUID().toString();
        protected T paylod;
        private Class<T> type;

        public BaseConfigEvent(T paylod, Class<T> type){
            this.paylod = Objects.requireNonNull(paylod);
            this.type = Objects.requireNonNull(type);
        }

        @Override
        public Class<T> getResourceType() {
            return type;
        }

        @Override
        public T getResource() {
            return paylod;
        }

        @Override
        public String getVersion() {
            return version;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + '{' +
                    "timestamp=" + timestamp +
                    ", version='" + version + '\'' +
                    ", paylod='" + paylod + '\'' +
                    '}';
        }
    }