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
package org.apache.tamaya.se;

import org.apache.tamaya.Environment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * MBean implementation for accessing environment data.
 * Created by Anatole on 24.11.2014.
 */
public class ManagedEnvironment implements ManagedEnvironmentMBean{

    @Override
    public List<String> getEnvironmentHierarchy() {
        return Environment.getEnvironmentHierarchy();
    }

    @Override
    public String getEnvironmentInfo(String environmentContext) {
        try {
            // TODO
            return "EnvironmentInfo {}";
        }
        catch(Exception e){
            // TODO logging
            return "EnvironmentInfo{}";
        }
    }

    @Override
    public Map<String, String> getEnvironment(String environmentType, String context) {
        try {
            Optional<Environment> env = Environment.getInstance(environmentType, context);
            if (env.isPresent()) {
                return env.get().toMap();
            }
        } catch (Exception e) {
            // TODO logging
        }
        return Collections.emptyMap();
    }

    @Override
    public String getEnvironmentInfo() {
        // TODO
        return "EnvironmentInfo {}";
    }
}
