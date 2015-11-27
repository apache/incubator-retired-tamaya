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
package org.apache.tamaya.integration.osgi.base;

/**
 * Mapping function for mapping Tamaya configuration sections to OSGI pids.
 */
// @FunctionalInterface
public interface OSGIConfigRootMapper {

    /**
     * Map the given OSGI pid to a corresponding configuration section in Tamaya. Es an example (and this is also the
     * default implemented) a configuration mapping for {@code pid/factoryPid==myBundle} could be {@code [bundle:myBundle]}.
     * This mapping is used as a prefix when collecting the corresponding entries for the OSGI configuration.
     * @param pid the OSGI pid, or null
     * @param factoryPid the OSGI factoryPid, or null
     * @return return the corresponding config root section. For ommitting any root section simply return an empty
     * String.
     */
    String getTamayaConfigRoot(String pid, String factoryPid);
}
