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
package org.apache.tamaya.events;

import java.beans.PropertyChangeEvent;
import java.util.Collection;


/**
 * Event that contains a set current changes that were applied or could be applied.
 * @param <T> the event type
 */
public interface ChangeNotification<T>{

    /**
     * Get the underlying property provider/configuration.
     * @return the underlying property provider/configuration, never null.
     */
    T getResource();

    /**
     * Get the version relative to the observed resource. The version is required to be unique for
     * each change emmitted for a resource. There is no further requirement how this uniqueness is
     * modelled, so returning a UUID is cometely valid.
     * @return the base version.
     */
    String getVersion();

    /**
     * Get the timestamp in millis from the current epoch. it is expected that the timestamp and the version are unique to
     * identify a changeset.
     * @return the timestamp, when this changeset was created.
     */
    long getTimestamp();

    /**
     * Get the changes recorded.
     * @return the recorded changes, never null.
     */
    Collection<PropertyChangeEvent> getChanges();

    /**
     * Checks if the given key is added, or updated OR removed.
     * @param key the target key (can also be a regular expression), that matches the requested keys,
     *            not null.
     * @return true, if the given key was added, or updated BUT NOT removed.
     */
    boolean isKeyAffected(String key);

}
