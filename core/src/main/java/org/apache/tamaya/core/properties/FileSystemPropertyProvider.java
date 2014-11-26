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
package org.apache.tamaya.core.properties;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileSystemPropertyProvider extends AbstractResourceConfigMap{

	private static final Logger LOG = Logger.getLogger(FileSystemPropertyProvider.class.getName());
    private static final long serialVersionUID = -2016119697552480056L;
	private Map<String, Map<String, String>> propMetaInfo = new HashMap<>();

	public FileSystemPropertyProvider(String resourcePath, ClassLoader classLoader,
                                      AbstractResourceConfigMap parentConfig) {
		super(classLoader, parentConfig, resourcePath);
	}

	public FileSystemPropertyProvider(String resourcePath, AbstractResourceConfigMap parentConfig) {
		super(getCurrentClassLoader(), parentConfig, resourcePath);
	}

	public FileSystemPropertyProvider(String resourcePath) {
		super(getCurrentClassLoader(), null, resourcePath);
	}


	@Override
	protected void readSource(Map<String, String> targetMap, String src) {
		try (InputStream is = getClassLoader().getResource(
				src).openStream()) {
			Properties props = new Properties();
			URL resource = getClassLoader().getResource(
					src);
//			if (isSourceRead(resource.toString())) {
//				// continue;
//				return;
//			}
			addSource(resource.toString());
			Map<String, String> mi = new HashMap<>();
			mi.put("source", resource.toString());
			if (Thread.currentThread().getContextClassLoader() != null) {
				mi.put("classloader", Thread.currentThread()
						.getContextClassLoader().toString());
			}
			props.load(is);
			for (Map.Entry<Object, Object> en : props.entrySet()) {
                targetMap.put(en.getKey().toString(),
						en.getValue().toString());
				propMetaInfo.put(en.getKey().toString(),
						mi);
			}
		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Error loading config unit.", e);
		}
	}

	protected Map<String, String> getMetaInfo(String key, String value) {
		Map<String, String> mi = propMetaInfo.get(key);
		if (mi != null) {
			return mi;
		}
		return Collections.emptyMap();
	}

	private static ClassLoader getCurrentClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			return FileSystemPropertyProvider.class.getClassLoader();
		}
		return cl;
	}

}
