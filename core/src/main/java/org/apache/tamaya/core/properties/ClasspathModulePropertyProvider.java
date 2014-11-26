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

import org.apache.tamaya.core.spi.ResourceLoader;

import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.spi.Bootstrap;

import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class ClasspathModulePropertyProvider extends AbstractPropertyProvider{

    private static final long serialVersionUID = 8488347395634476626L;
	private ClassLoader classLoader;
	private String[] sources;

	public ClasspathModulePropertyProvider(ClassLoader classLoader, String... sources) {
        super(MetaInfoBuilder.of().set("classloader", classLoader.toString()).setSourceExpressions(sources).build());
		Objects.requireNonNull(classLoader);
		this.classLoader = classLoader;
		this.sources = sources.clone();
		load(); // trigger initialization
	}

	public ClassLoader getClassLoader() {
		return this.classLoader;
	}


	@Override
	public Map<String,String> toMap() {
        Map<String,String> content = new HashMap<>();
		for (String srcPattern : sources) {
			List<URI> urls = null;
			try {
				Bootstrap.getService(ResourceLoader.class).getResources(classLoader, srcPattern);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			if (urls == null) {
				return content;
			}
            MetaInfoBuilder mb = MetaInfoBuilder.of(getMetaInfo());
			for (URI url : urls) {
				InputStream is = null;
				try {
					Properties props = new Properties();
					is = url.toURL().openStream();
					props.loadFromXML(is);
					addSource(url.toString());
					for (Map.Entry<Object, Object> en : props.entrySet()) {
                        content.put(en.getKey().toString(), en.getValue().toString());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
        return content;
	}
}
