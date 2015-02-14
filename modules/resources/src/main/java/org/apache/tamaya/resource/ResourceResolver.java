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

import org.apache.tamaya.ConfigException;
import org.apache.tamaya.spi.ServiceContext;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

/**
 * Interface to be implemented by modules. It supports loading of files or classpath resources either directly or by
 * defining a Ant-styled resource pattern:
 * <ul>
 *     <li>'*' is a placeholder for any character (0..n)</li>
 *     <li>'**' is a placeholder for any number of subdirectories going down a directory structure recursively.</li>
 *     <li>'?' is a placeholder for exact one character</li>
 * </ul>
 * Given that the following expressions are valid expressions:
 * <pre>
 *     classpath:javax/annotations/*
 *     javax?/annotations&#47;**&#47;*.class
 *     org/apache/tamaya&#47;**&#47;tamayaconfig.properties
 *     file:C:/temp/*.txt
 *     file:C:\**\*.ini
 *     C:\Programs\**&#47;*.ini
 *     /user/home/A*b101_?.pid
 *     /var/logs&#47;**&#47;*.log
 * </pre>
 */
public interface ResourceResolver {

    /**
     * Resolves resource expressions to a list of {@link URL}s. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link URL}s found, never
     * null.
     * .
     */
    default Collection<URL> getResources(Collection<String> expressions) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }
        return getResources(cl, expressions);
    }

    /**
     * Resolves resource expressions to a list of {@link URL}s. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link URL}s found, never
     * null.
     * .
     */
    default Collection<URL> getResources(String... expressions) {
        return getResources(Arrays.asList(expressions));
    }

    /**
     * Resolves resource expressions to a list of {@link URL}s, considerubg
     * the given classloader for classloader dependent format. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link URL}s found, never
     * null.
     * .
     */
    default Collection<URL> getResources(ClassLoader classLoader, String... expressions){
        return getResources(classLoader, Arrays.asList(expressions));
    }

    /**
     * Resolves resource expressions to a list of {@link URL}s, considerubg
     * the given classloader for classloader dependent format. Hereby
     * the ordering of format matches the input of the resolved expressions. Nevertheless be aware that
     * there is no determined ordering of format located within a classloader.
     *
     * @param expressions the expressions to be resolved, not empty.
     * @return the corresponding collection of current {@link URL}s found, never
     * null.
     * .
     */
    Collection<URL> getResources(ClassLoader classLoader, Collection<String> expressions);

    /**
     * Access the current ResourceResolver.
     * @return the current ResourceResolver instance, never null.
     * @throws ConfigException, if no ResourceResolver is available (should not happen).
     */
    public static ResourceResolver getInstance(){
        return ServiceContext.getInstance().getService(ResourceResolver.class).orElseThrow(
                () -> new ConfigException("ResourceResolver not available.")
        );
    }

}
