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
package org.apache.tamaya.mutableconfig;

import org.apache.tamaya.events.delta.ConfigurationChange;
import org.apache.tamaya.events.delta.PropertySourceChange;

import java.io.Serializable;
import java.util.*;

/**
 * Summaray class of a configuration change operation.
 * This class is immutable, read-only and thread-safe.
 */
public final class ChangeSummary implements Serializable{

    private static final long serialVersionUID = 1L;

    private String changeId;
    private ConfigurationChange configChange;
    private List<PropertySourceChange> sourceChanges = new ArrayList<>();
    private long commitTS;

    private ChangeSummary(Builder builder){
        this.changeId = builder.changeId;
        this.commitTS = builder.commitTS;
        this.configChange = Objects.requireNonNull(builder.configChange);
        this.sourceChanges.addAll(builder.sourceChanges);
    }

    public String getChangeId(){
        return changeId;
    }

    public long getTimestampMillis(){
        return commitTS;
    }

    public ConfigurationChange getConfigurationChange(){
        return configChange;
    }

    public Collection<PropertySourceChange> getPropertySourceChanges(){
        return Collections.unmodifiableList(this.sourceChanges);
    }





    public static final class Builder {

        private String changeId;
        private ConfigurationChange configChange;
        private List<PropertySourceChange> sourceChanges = new ArrayList<>();
        private long commitTS = System.currentTimeMillis();

        public Builder(){
            this(UUID.randomUUID().toString());
        }

        public Builder(String changeId){
            this.changeId = Objects.requireNonNull(changeId);
        }

        public Builder setConfigChange(ConfigurationChange configChange){
            this.configChange = configChange;
            return this;
        }

        public Builder addPropertySourceChange(PropertySourceChange propertyChange){
            this.sourceChanges.add(propertyChange);
            return this;
        }

        public Builder setTimestamp(long timestamp){
            this.commitTS = timestamp;
            return this;
        }

        public ChangeSummary build(){
            return new ChangeSummary(this);
        }


    }


}
