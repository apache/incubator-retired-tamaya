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
package org.apache.tamaya.core.config;

import org.apache.tamaya.ConfigChangeSetBuilder;
import org.apache.tamaya.PropertyProvider;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.MetaInfoBuilder;
import org.apache.tamaya.core.properties.PropertyProviders;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Anatole on 29.03.14.
 */
final class FreezedConfiguration extends AbstractConfiguration implements Serializable{
    private static final long serialVersionUID = -6373137316556444171L;

    private PropertyProvider properties;
    private String version;

    private FreezedConfiguration(Configuration config){
        super(MetaInfoBuilder.of(config.getMetaInfo()).set("freezedAt", Instant.now().toString()).build());
        this.properties = PropertyProviders.freezed(config);
        this.version = Objects.requireNonNull(config.getVersion());
    }

    public static final FreezedConfiguration of(Configuration config){
        return new FreezedConfiguration(config);
    }

    @Override
    public Map<String,String> toMap(){
        return properties.toMap();
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FreezedConfiguration that = (FreezedConfiguration) o;

        if (!properties.equals(that.properties)) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = properties.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FreezedConfiguration{" +
                "properties=" + properties +
                ", version=" + version +
                '}';
    }
}
