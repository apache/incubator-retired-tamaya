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
package org.apache.tamaya;

import java.util.Map;

/**
 * Supplier for a property map.
 */
@FunctionalInterface
public interface PropertyMapSupplier {

    /**
     * Access the current properties as Map. The resulting Map may not return all items accessible, e.g.
     * when the underlying storage does not support iteration of its entries.
     *
     * @return the a corresponding map, never null.
     */
   Map<String,String> getProperties();

}
