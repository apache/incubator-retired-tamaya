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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.tamaya.MetaInfo;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.PropertySource;

/**
 * Abstract base class for implementing a {@link org.apache.tamaya.PropertySource}.
 */
public abstract class AbstractPropertySource implements PropertySource, Serializable{
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -6553955893879292837L;

    protected MetaInfo metaInfo;

    /**
     * The underlying sources.
     */
    private volatile Set<String> sources = new HashSet<>();

    /**
     * Constructor.
     */
    protected AbstractPropertySource(MetaInfo metaInfo){
        Objects.requireNonNull(metaInfo);
        this.metaInfo = metaInfo;
    }

    @Override
    public MetaInfo getMetaInfo(){
        return metaInfo;
    }


    /**
     * Method that allows an additional source to be added, to be used by
     * subclasses.
     *
     * @param source the source, not {@code null}.
     */
    protected void addSource(String source){
        Objects.requireNonNull(source);
        this.sources.add(source);
    }


    protected void addSources(Collection<String> sources){
        Objects.requireNonNull(sources);
        this.sources.addAll(sources);
    }

    @Override
    public boolean containsKey(String key){
        return toMap().containsKey(key);
    }

    @Override
    public Optional<String> get(String key){
        return Optional.ofNullable(toMap().get(key));
    }

    @Override
    public Set<String> keySet(){
        return toMap().keySet();
    }

    @Override
    public String toString(){
        StringBuilder b = new StringBuilder(getClass().getSimpleName()).append("{\n");
        b.append("  ").append("(").append(MetaInfoBuilder.NAME).append(" = ").append(getMetaInfo().get(MetaInfoBuilder.NAME)).append(")\n");
        printContents(b);
        return b.append('}').toString();
    }

    protected String printContents(StringBuilder b){
        Map<String,String> sortMap = toMap();
        if(!(sortMap instanceof SortedMap)){
            sortMap = new TreeMap<>(sortMap);
        }
        for(Map.Entry<String,String> en : sortMap.entrySet()){
            b.append("  ").append(en.getKey()).append(" = \"").append(en.getValue().replaceAll("\\\"", "\\\\\"").replaceAll("=", "\\=")).append("\"\n");
        }
        return b.toString();
    }

}
