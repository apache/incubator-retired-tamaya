/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tamaya.core.internal.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URL;

/**
 * Utility for detecting and accessing JBoss VFS in the classpath.
 * <p>
 * <p>As current Spring 4.0, this class supports VFS 3.x on JBoss AS 6+ (package
 * {@code org.jboss.vfs}) and is in particular compatible with JBoss AS 7 and
 * WildFly 8.
 * <p>
 * <p>Thanks go to Marius Bogoevici for the initial patch.
 * <b>Note:</b> This is an internal class and should not be used outside the framework.
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @since 3.0.3
 */
class VfsUtils {

    private static final String VFS3_PACKAGE = "org.jboss.vfs.";
    private static final String VFS_NAME = "VFS";

    private static Method vfsMethodGetRootUrl = null;
    private static Method vfsMethodGetRootUri = null;

    private static Method virtualFileMethodExists = null;
    private static Method virtualFileMethodGetInputStream;
    private static Method virtualFileMethodGetSize;
    private static Method virtualFileMethodGetLastModified;
    private static Method virtualFileMethodToUrl;
    private static Method virtualFileMethodToUri;
    private static Method virtualFileMethodGetName;
    private static Method virtualFileMethodGetPathName;
    private static Method virtualFileMethodGetChild;

    protected static Class<?> virtualFileVisitorInterface;
    protected static Method virtualFileMethodVisit;

    private static Field visitorAttributesFieldRecurse = null;
    private static Method getPhysicalFile = null;

    static {
        ClassLoader loader = VfsUtils.class.getClassLoader();
        try {
            Class<?> vfsClass = loader.loadClass(VFS3_PACKAGE + VFS_NAME);
            vfsMethodGetRootUrl = ReflectionUtils.findMethod(vfsClass, "getChild", URL.class);
            vfsMethodGetRootUri = ReflectionUtils.findMethod(vfsClass, "getChild", URI.class);

            Class<?> virtualFile = loader.loadClass(VFS3_PACKAGE + "VirtualFile");
            virtualFileMethodExists = ReflectionUtils.findMethod(virtualFile, "exists");
            virtualFileMethodGetInputStream = ReflectionUtils.findMethod(virtualFile, "openStream");
            virtualFileMethodGetSize = ReflectionUtils.findMethod(virtualFile, "getSize");
            virtualFileMethodGetLastModified = ReflectionUtils.findMethod(virtualFile, "getLastModified");
            virtualFileMethodToUri = ReflectionUtils.findMethod(virtualFile, "toURI");
            virtualFileMethodToUrl = ReflectionUtils.findMethod(virtualFile, "toURL");
            virtualFileMethodGetName = ReflectionUtils.findMethod(virtualFile, "getName");
            virtualFileMethodGetPathName = ReflectionUtils.findMethod(virtualFile, "getPathName");
            getPhysicalFile = ReflectionUtils.findMethod(virtualFile, "getPhysicalFile");
            virtualFileMethodGetChild = ReflectionUtils.findMethod(virtualFile, "getChild", String.class);

            virtualFileVisitorInterface = loader.loadClass(VFS3_PACKAGE + "VirtualFileVisitor");
            virtualFileMethodVisit = ReflectionUtils.findMethod(virtualFile, "visit", virtualFileVisitorInterface);

            Class<?> visitorAttributesClass = loader.loadClass(VFS3_PACKAGE + "VisitorAttributes");
            visitorAttributesFieldRecurse = ReflectionUtils.findField(visitorAttributesClass, "RECURSE");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
        }
    }

    private VfsUtils() {
    }

    static void visit(Object resource, InvocationHandler visitor) throws IOException {
        Object visitorProxy = Proxy.newProxyInstance(
                virtualFileVisitorInterface.getClassLoader(),
                new Class<?>[]{virtualFileVisitorInterface}, visitor);
        invokeVfsMethod(virtualFileMethodVisit, resource, visitorProxy);
    }

    protected static Object invokeVfsMethod(Method method, Object target, Object... args) throws IOException {
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            Throwable targetEx = ex.getTargetException();
            if (targetEx instanceof IOException) {
                throw (IOException) targetEx;
            }
            ReflectionUtils.handleInvocationTargetException(ex);
        } catch (Exception ex) {
            ReflectionUtils.handleReflectionException(ex);
        }

        throw new IllegalStateException("Invalid code path reached");
    }

    static boolean exists(Object vfsResource) {
        try {
            return (Boolean) invokeVfsMethod(virtualFileMethodExists, vfsResource);
        } catch (IOException ex) {
            return false;
        }
    }

    static boolean isReadable(Object vfsResource) {
        try {
            return ((Long) invokeVfsMethod(virtualFileMethodGetSize, vfsResource) > 0);
        } catch (IOException ex) {
            return false;
        }
    }

    static long getSize(Object vfsResource) throws IOException {
        return (Long) invokeVfsMethod(virtualFileMethodGetSize, vfsResource);
    }

    static long getLastModified(Object vfsResource) throws IOException {
        return (Long) invokeVfsMethod(virtualFileMethodGetLastModified, vfsResource);
    }

    static InputStream getInputStream(Object vfsResource) throws IOException {
        return (InputStream) invokeVfsMethod(virtualFileMethodGetInputStream, vfsResource);
    }

    static URL getURL(Object vfsResource) throws IOException {
        return (URL) invokeVfsMethod(virtualFileMethodToUrl, vfsResource);
    }

    static URI getURI(Object vfsResource) throws IOException {
        return (URI) invokeVfsMethod(virtualFileMethodToUri, vfsResource);
    }

    static String getName(Object vfsResource) {
        try {
            return (String) invokeVfsMethod(virtualFileMethodGetName, vfsResource);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot get resource name", ex);
        }
    }

    static Object getRelative(URL url) throws IOException {
        return invokeVfsMethod(vfsMethodGetRootUrl, null, url);
    }

    static Object getChild(Object vfsResource, String path) throws IOException {
        return invokeVfsMethod(virtualFileMethodGetChild, vfsResource, path);
    }

    static File getFile(Object vfsResource) throws IOException {
        return (File) invokeVfsMethod(getPhysicalFile, vfsResource);
    }

    static Object getRoot(URI url) throws IOException {
        return invokeVfsMethod(vfsMethodGetRootUri, null, url);
    }

    // protected methods used by the support sub-package

    protected static Object getRoot(URL url) throws IOException {
        return invokeVfsMethod(vfsMethodGetRootUrl, null, url);
    }

    protected static Object getVisitorAttribute() {
        try {
            return visitorAttributesFieldRecurse.get(null);
        } catch (Exception e) {
            ReflectionUtils.handleReflectionException(e);
            return null; // never called
        }
    }

    protected static String getPath(Object resource) {
        try {
            return (String) virtualFileMethodGetPathName.invoke(resource);
        } catch (Exception e) {
            ReflectionUtils.handleReflectionException(e);
            return null; // never called
        }
    }

}
