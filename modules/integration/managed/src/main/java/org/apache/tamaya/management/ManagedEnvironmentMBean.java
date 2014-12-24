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


import java.util.List;
import java.util.Map;

/**
 * Managed bean interface for accessing environment data.
 */
public interface ManagedEnvironmentMBean {

    /**
     * Returns the current environment hierarchy defined.
     * @see org.apache.tamaya.Environment#getEnvironmentType()
     * @see org.apache.tamaya.Environment#getEnvironmentHierarchy()
     * @return the current environment type hierarchy defined, never null.
     */
    public List<String> getEnvironmentHierarchy();

    /**
     * Get the common environment information in JSON format, which has the following form:
     * <pre>
     * Environment {
     *     id: "system:VM,domain:test",
     *     metaInfo {
     *         a: "aValue",
     *         b: "bValue"
     *     }
     *     entries{
     *         val1: "value1",
     *         val2: "value2",
     *     }
     * }
     * </pre>
     * @see org.apache.tamaya.Environment
     * @param environmentContext the identifier to access the environment instance
     * @return the environment JSON info, or null, if no such environment is accessible.
     */
    public String getEnvironmentInfo(String environmentContext);

    /**
     * Access the given environment as Map. the {@code environmentContext} is added to the
     * map using the key {@code __environmentId}.
     * @param environmentContext the identifier to access the environment instance
     * @param context the context, not null.
     * @return a map with the currently defined environment keys and values.
     */
    public Map<String,String> getEnvironment(String environmentContext, String context);

    /**
     * Get a general JSON info on the currently available environments current the form:
     * <pre>
     *     EnvironmentInfo{
     *         host: "hostName",
     *         ipAddress: "111.112.123.123",
     *         typeHierarchy: {"system", "domain", "ear", "war", "saas-scope", "tenant"}
     *         environments {
     *             Environment {
     *                 id: "system:VM,domain:test",
     *                 ...
     *             },
     *             ...
     *         }
     *     }
     * </pre>
     * @return
     */
    public String getEnvironmentInfo();

}