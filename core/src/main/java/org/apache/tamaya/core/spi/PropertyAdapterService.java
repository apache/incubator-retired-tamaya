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
package org.apache.tamaya.core.spi;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tamaya.PropertyAdapter;

public interface PropertyAdapterService{

	public default PropertyAdapter<URL> getURLAdapter(){
        return PropertyAdapter.class.cast(getClassAdapter(URL.class));
    }

	public default PropertyAdapter<InputStream> getClasspathResourceAdapter(){
        return PropertyAdapter.class.cast(getClassAdapter(InputStream.class));
    }

	public default PropertyAdapter<File> getFileAdapter(){
        return PropertyAdapter.class.cast(getClassAdapter(File.class));
    }

	public default PropertyAdapter<Set<String>> getSetAdapter(){
        return PropertyAdapter.class.cast(getClassAdapter(Set.class));
    }

	public default PropertyAdapter<Map<String, String>> getMapAdapter(){
        return PropertyAdapter.class.cast(getClassAdapter(Map.class));
    }

	public default PropertyAdapter<List<String>> getListAdapter(){
        return PropertyAdapter.class.cast(getClassAdapter(List.class));
    }

    public default <T> PropertyAdapter<Class<? extends T>> getClassAdapter(Class<T> requiredType){
        return getClassAdapter(requiredType, Thread.currentThread().getContextClassLoader());
    }

	public <T> PropertyAdapter<Class<? extends T>> getClassAdapter(Class<T> requiredType,
			ClassLoader... classLoaders);

}
