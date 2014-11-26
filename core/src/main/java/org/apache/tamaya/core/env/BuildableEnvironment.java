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
package org.apache.tamaya.core.env;

import org.apache.tamaya.Environment;
import org.apache.tamaya.Stage;

import java.util.*;


class BuildableEnvironment implements Environment {

    private static final long serialVersionUID = 707575538680740130L;
    private Map<String,String> context = new TreeMap<>();
    private Environment parent;
    private String id;
    private String type;
    private Stage stage;

    BuildableEnvironment(EnvironmentBuilder builder){
        Objects.requireNonNull(builder);
        context.putAll(builder.contextData);
        this.id = builder.id;
        this.type = builder.type;
        this.stage = builder.stage;
        this.parent = builder.parent;
    }

    @Override
    public String getEnvironmentType() {
        return type;
    }

    @Override
    public String getContext() {
        return id;
    }

    @Override
    public String getEnvironmentId() {
        StringBuilder b = new StringBuilder();
        if(getParentEnvironment()!=null) {
            b.append(getParentEnvironment().getContext());
        }
        if(b.length()>0)
            b.append('.');
        b.append(getFullId());
        return b.toString();
    }

    public String getFullId() {
        return getContext()+'('+getEnvironmentType()+')';
    }

    @Override
    public Environment getParentEnvironment(){
        return parent;
    }

    @Override
    public Map<String, String> toMap() {
        return context;
    }

    @Override
    public Stage getStage(){
        return stage;
    }

    @Override
    public Optional<String> get(String key){
        String value =  context.get(key);
        if(value==null && parent!=null){
            return parent.get(key);
        }
        return Optional.ofNullable(value);
    }

    @Override
    public boolean containsKey(String key){
        return context.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return context.keySet();
    }

    @Override
    public Iterator<Environment> iterator() {
        List<Environment> envList = new ArrayList<>();
        Environment env = this;
        while(env.getParentEnvironment()!=null){
            envList.add(env);
            env = env.getParentEnvironment();
        }
        envList.add(env);
        return envList.iterator();
    }

    /*
     * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
    @Override
    public String toString(){
        if(parent==null) {
            return "Environment " + getFullId() + '\n' + getData();
        }
        return "Environment " + getFullId() +
                ",\n"  + getData() + ",\n  parent: " + parent.toString();
    }

    private String getData() {
        StringBuilder b = new StringBuilder();
        for(Map.Entry<String,String> en: this.context.entrySet()){
            b.append("    ").append(en.getKey()).append('=').append(escape(en.getValue())).append('\n');
        }
        if(b.length()>0)
            b.setLength(b.length()-1);
        return b.toString();
    }

    private String escape(String value){
        if(value==null)
            return null;
        return value.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r").replaceAll("\t", "\\\\t")
                .replaceAll("=", "\\\\=");
    }
}
