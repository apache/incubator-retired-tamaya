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
package org.apache.tamaya.core.internal.config;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.tamaya.Configuration;

/**
 * Implementation of Configuration which the information is from xml or properties files.
 * Once the File modified, it will commit automatically by provider.
 * @see FilesPropertiesConfigProvider
 * @see FileChangeObserver
 * @author otaviojava
 */
class FileConfiguration implements Configuration, FileChangeObserver {

	private Map<String, String> configurationMap;

	public FileConfiguration(Map<String, String> configurationMap) {
        this.configurationMap = configurationMap;
    }

    @Override
	public Optional<String> get(String key) {
		return Optional.ofNullable(configurationMap.get(key));
	}

    @Override
	public String getName() {
		return "files.config";
	}

	@Override
	public Map<String, String> getProperties() {
		return configurationMap;
	}

    @Override
    public void update(Map<String, String> configurationMap) {
        synchronized (this) {
            this.configurationMap = configurationMap;
        }
    }

    @Override
    public String toString() {
        return "org.apache.tamaya.core.internal.config.FileConfiguration: " + configurationMap.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(configurationMap);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(Configuration.class.isInstance(obj)) {
            Configuration other = Configuration.class.cast(obj);
            return Objects.equals(configurationMap, other.getProperties());
        }

        return false;
    }
}
