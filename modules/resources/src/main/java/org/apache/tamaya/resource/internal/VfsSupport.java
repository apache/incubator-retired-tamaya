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
package org.apache.tamaya.resource.internal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

/**
 * Internal support class dealing with JBoss VFS in the classpath.
 * <p>
 * This code is compatible with JBoss AS 6+ and JBoss AS 7 and
 * WildFly 8.
 */
class VfsSupport {

    private static final String VFS3_PKG = "org.jboss.vfs.";
    private static final String VFS_PROTOCOL = "VFS";

    private static Method methodGetRootUrl = null;
    private static Method methodToUrl;
    private static Method methodGetPathName;
    private static Class<?> fileVisitorInterface;
    private static Method methodVisit;
    private static Field visitorAttributesField = null;
    private static Method methodGetPhysicalFile = null;

    /**
     * Private constructor.
     */
    private VfsSupport(){}

    /*
     * Initialize glue reflection code for communicating with VFS systems.
     */
    static {
        ClassLoader loader = VfsSupport.class.getClassLoader();
        try {
            Class<?> vfsClass = loader.loadClass(VFS3_PKG + VFS_PROTOCOL);
            methodGetRootUrl = findMethod(vfsClass, "getChild", URL.class);
            Class<?> virtualFile = loader.loadClass(VFS3_PKG + "VirtualFile");
            methodToUrl = findMethod(virtualFile, "toURL");
            methodGetPathName = findMethod(virtualFile, "getPathName");
            methodGetPhysicalFile = findMethod(virtualFile, "getPhysicalFile");
            fileVisitorInterface = loader.loadClass(VFS3_PKG + "VirtualFileVisitor");
            methodVisit = findMethod(virtualFile, "visit", fileVisitorInterface);
            Class<?> visitorAttributesClass = loader.loadClass(VFS3_PKG + "VisitorAttributes");
            visitorAttributesField = findField(visitorAttributesClass, "RECURSE");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("JBoss VFS not available.", ex);
        }
    }

    /**
     * Visit a VFS resource with the given visitor, modeled as dynamic {@link java.lang.reflect.InvocationHandler}.
     *
     * @param resource the resource
     * @param visitor  the visitor.
     * @throws IOException
     */
    static void visit(Object resource, InvocationHandler visitor) throws IOException {
        Object visitorProxy = Proxy.newProxyInstance(
                fileVisitorInterface.getClassLoader(),
                new Class<?>[]{fileVisitorInterface}, visitor);
        invokeVfsMethod(methodVisit, resource, visitorProxy);
    }

    /**
     * Helper method to invoke an operation on VFS.
     *
     * @param method the method to invoke
     * @param target the target instance
     * @param args   any arguments
     * @return the result
     * @throws IOException if something fails.
     */
    private static Object invokeVfsMethod(Method method, Object target, Object... args) throws IOException {
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            throw new IOException("Failed to evaluated method: " + method, ex);
        }

    }

    /**
     * Transform a VFS resource into an URL.
     *
     * @param vfsResource the cfw resource, not null
     * @return the corresponding URL
     * @throws IOException
     */
    static URL getURL(Object vfsResource) throws IOException {
        return (URL) invokeVfsMethod(methodToUrl, vfsResource);
    }

    /**
     * Get a to root VFS resource for the given URL.
     *
     * @param url the url
     * @return the corresponding VFS resource.
     * @throws IOException
     */
    static Object getRelative(URL url) throws IOException {
        return invokeVfsMethod(methodGetRootUrl, null, url);
    }

    /**
     * Transform the given VFS resource of a file.
     *
     * @param vfsResource the VFS resource
     * @return the file.
     * @throws IOException
     */
    static File getFile(Object vfsResource) throws IOException {
        return (File) invokeVfsMethod(methodGetPhysicalFile, vfsResource);
    }

    /**
     * Convert the given URL to the correspinoding root URL.
     *
     * @param url the url
     * @return the root resource.
     * @throws IOException
     */
    static Object getRoot(URL url) throws IOException {
        return invokeVfsMethod(methodGetRootUrl, null, url);
    }

    /**
     * Access the attributes from the current visitor context.
     *
     * @return the attributes.
     */
    static Object getVisitorAttributes() {
        return readField(visitorAttributesField, null);
    }

    /**
     * Access the corresponding path to the given VFS resource.
     *
     * @param resource the VFS resource
     * @return the corresponding path.
     */
    static String getPath(Object resource) {
        try {
            return (String) methodGetPathName.invoke(resource);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to get path name - " + resource, e);
        }
    }


    /**
     * Attempt to find a {@link Method} on the supplied class with the supplied name
     * and parameter types. Searches all superclasses up to {@code Object}.
     * <p>Returns {@code null} if no {@link Method} can be found.
     *
     * @param clazz      the class to introspect
     * @param name       the name of the method
     * @param paramTypes the parameter types of the method
     *                   (may be {@code null} to indicate any signature)
     * @return the Method object, or {@code null} if none found
     */
    private static Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());
            for (Method method : methods) {
                if (name.equals(method.getName()) &&
                        (paramTypes == null || Arrays.equals(paramTypes, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }


    /**
     * Get the field represented by the supplied {@link Field field object} on the
     * specified {@link Object target object}. In accordance with {@link Field#get(Object)}
     * semantics, the returned value is automatically wrapped if the underlying field
     * has a primitive type.
     * <p>Thrown exceptions are rethrown as {@link IllegalStateException}.
     *
     * @param field  the field to get
     * @param target the target object from which to get the field
     * @return the field's current value
     */
    private static Object readField(Field field, Object target) {
        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(target);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to read field: " + field.toGenericString(), e);
        }
    }

    /**
     * Attempt to find a {@link Field field} on the supplied {@link Class} with the
     * supplied {@code name}. Searches all superclasses up to {@link Object}.
     *
     * @param clazz the class to introspect
     * @param name  the name of the field
     * @return the corresponding Field object, or {@code null} if not found
     */
    private static Field findField(Class<?> clazz, String name) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(name, "Name must not be null.");
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (name.equals(field.getName())) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

}
