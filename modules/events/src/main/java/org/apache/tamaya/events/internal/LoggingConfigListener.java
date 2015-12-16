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
package org.apache.tamaya.events.internal;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.events.ConfigEvent;
import org.apache.tamaya.events.ConfigEventListener;
import org.apache.tamaya.events.ConfigurationChange;

import java.util.logging.Logger;

/**
 * Simple ConfigListener that simply logs any detected config changes to INFO level.
 */
public class LoggingConfigListener implements ConfigEventListener {

    private static final Logger LOG = Logger.getLogger(LoggingConfigListener.class.getName());

    @Override
    public void onConfigEvent(ConfigEvent<?> event) {
        if(event.getResourceType()== Configuration.class) {
            LOG.info("Configuration changed: " + event);
        }
    }
}
