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

import org.apache.tamaya.MetaInfo;
import java.util.*;

public class ClasspathPropertyProvider extends AbstractPropertyProvider{

    private static final long serialVersionUID = -2193109047946712701L;
    private Map<ClassLoader,ClasspathModulePropertyProvider> configs = new HashMap<>();
	private String[] resources;

	public ClasspathPropertyProvider(MetaInfo metaInfo, String... resources) {
        super(metaInfo);
        Objects.requireNonNull(resources);
        this.resources = resources;
	}


    @Override
    public Map<String,String> toMap(){
        return new Map<String,String>(){

            @Override
            public int size(){
                return getLoaderDependentDelegate().size();
            }

            @Override
            public boolean isEmpty(){
                return getLoaderDependentDelegate().isEmpty();
            }

            @Override
            public boolean containsKey(Object key){
                return getLoaderDependentDelegate().containsKey(key);
            }

            @Override
            public boolean containsValue(Object value){
                return getLoaderDependentDelegate().containsValue(value);
            }

            @Override
            public String get(Object key){
                return getLoaderDependentDelegate().get(key);
            }

            @Override
            public String put(String key, String value){
                return getLoaderDependentDelegate().put(key,value);
            }

            @Override
            public String remove(Object key){
                return getLoaderDependentDelegate().remove(key);
            }

            @Override
            public void putAll(Map<? extends String,? extends String> m){
                getLoaderDependentDelegate().putAll(m);
            }

            @Override
            public void clear(){
                getLoaderDependentDelegate().clear();
            }

            @Override
            public Set<String> keySet(){
                return getLoaderDependentDelegate().keySet();
            }

            @Override
            public Collection<String> values(){
                return getLoaderDependentDelegate().values();
            }

            @Override
            public Set<Entry<String,String>> entrySet(){
                return getLoaderDependentDelegate().entrySet();
            }

        };
    }

	private Map<String, String> getLoaderDependentDelegate() {
		Map<String, String> props = new HashMap<>();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = getClass().getClassLoader();
		}
		while (cl != null) {
			ClasspathModulePropertyProvider cfg = this.configs.get(cl);
			if (cfg == null) {
				cfg = new ClasspathModulePropertyProvider(cl, this.resources);
				this.configs.put(cl, cfg);
			}
			props.putAll(cfg.toMap());
			cl = cl.getParent();
		}
		return props;
	}

	@Override
	public void load() {
		Map<String, String> props = new HashMap<>();
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (cl == null) {
			cl = getClass().getClassLoader();
		}
		while (cl != null) {
			ClasspathModulePropertyProvider cfg = this.configs.get(cl);
			if (cfg != null) {
				cfg.load();
			}
			cl = cl.getParent();
		}
        super.load();
	}

    @Override
    public String toString(){
        return "ClasspathPropertyProvider{" +
                "configs=" + configs +
                ", resources=" + Arrays.toString(resources) +
                '}';
    }
}
