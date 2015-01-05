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
package org.apache.tamaya.resource;

import java.util.Arrays;
import java.util.Collection;

/**
 * Interface to be implemented by modules. By default only direct file/resource resolution is supported, whereas
 * extension modules may add functionality to perform ant styled pattern resolution of format.
 */
public interface ResourceResolver {

    /**
     * Resolves resource expressions to a list of {@link Resource}s. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link Resource}s found, never
     * null.
     * .
     */
    default Collection<Resource> getResources(Collection<String> expressions) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return getResources(cl, expressions);
    }

    /**
     * Resolves resource expressions to a list of {@link Resource}s. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link Resource}s found, never
     * null.
     * .
     */
    default Collection<Resource> getResources(String... expressions) {
        return getResources(Arrays.asList(expressions));
    }

    /**
     * Resolves resource expressions to a list of {@link Resource}s, considerubg
     * the given classloader for classloader dependent format. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link Resource}s found, never
     * null.
     * .
     */
    default Collection<Resource> getResources(ClassLoader classLoader, String... expressions){
        return getResources(classLoader, Arrays.asList(expressions));
    }

    /**
     * Resolves resource expressions to a list of {@link Resource}s, considerubg
     * the given classloader for classloader dependent format. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link Resource}s found, never
     * null.
     * .
     */
    Collection<Resource> getResources(ClassLoader classLoader, Collection<String> expressions);

}
