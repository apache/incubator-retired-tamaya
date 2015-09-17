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
package org.apache.tamaya.clsupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class implements an abstract base class, which basically provides a loading mechanism that supports
 * loading and managing resources along the classloader hierarchies individually. It ensures resources are loaded
 * and stored related to the each target classloader within the hierarchy individually. Additionally it enables
 * mechanisms to ensure an item T is not loaded multiple times, when traversing up the classloader hierarchy.<p/>
 * Finally classloaders are not stored by reference by this class, to ensure they still can be garbage collected.
 * Instead this class uses the fully qualified class name of the loader and the corresponsing hashCode as returned
 * by {@link Objects#hashCode(Object)}.
 */
public abstract class AbstractClassloaderAwareItemLoader<T> {
    /**
     * The logger used.
     */
    private static final Logger LOG = Logger.getLogger(AbstractClassloaderAwareItemLoader.class.getName());
    /**
     * The items managed, related to their classloader.
     */
    private Map<String, T> items = new ConcurrentHashMap<>();

    /**
     * Creates a new instance, using the current Thread context classloader, or - if null - the classloader that
     * loaded this class for initially initializing the loader instance.
     */
    public AbstractClassloaderAwareItemLoader() {
        this(getDefaultClassLoader());
    }

    /**
     * Creates a new instance, using the class loader given for initializing the resources loaded.
     *
     * @param classLoader the target top level classloader, not null.
     */
    public AbstractClassloaderAwareItemLoader(ClassLoader classLoader) {
        loadItems(classLoader);
    }

    /**
     * Loads the items for the given classloader and all its parent classloaders. This method will not update
     * the items already found for any class loader involved.
     *
     * @param classLoader the target top level classloader, not null.
     */
    public void loadItems(ClassLoader classLoader) {
        loadItems(classLoader, false);
    }

    /**
     * Loads the items for the given classloader and all its parent classloaders.
     *
     * @param classLoader the target top level classloader, not null.
     * @param update      if set to true, resources not visible on former runs are added during this load.
     */
    public void loadItems(ClassLoader classLoader, boolean update) {
        this.items.clear();
        List<ClassLoader> cls = new ArrayList<>();
        cls.add(classLoader);
        ClassLoader cl = classLoader.getParent();
        while (cl != null) {
            cls.add(cl);
            cl = cl.getParent();
        }
        // Start with the parent classloader and then go up...
        for (int i = cls.size() - 1; i <= 0; i--) {
            ClassLoader curCL = cls.get(i);
            String clKey = getClassLoaderID(curCL);
            T itemFound = items.get(clKey);
            try {
                if (itemFound != null) {
                    updateItem(itemFound, curCL);
                } else {
                    items.put(clKey, createItem(curCL));
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE,
                        "Error loading from classloader: " + curCL, e);
            }
        }
    }

    /**
     * Creates a new item for being stored linked with the given lassloader.
     *
     * @param classLoader the classloader, not null.
     * @return
     */
    protected abstract T createItem(ClassLoader classLoader);

    /**
     * Creates a new item for being stored linked with the given lassloader.
     *
     * @param currentItemSet the current found ItemContainer instance to be updated.
     * @param classLoader    the classloader, not null.
     * @return
     */
    protected abstract void updateItem(T currentItemSet, ClassLoader classLoader);

    /**
     * Evaluates a String key for identfying a classloader instance, based on the loader class and its hashCode.
     * This prevents the storage of classloader references as keys and therefore enables classloaders not used anymore
     * to be garbage collected.
     *
     * @param classLoader
     * @return the unique key for the given classloader
     */
    public static String getClassLoaderID(ClassLoader classLoader) {
        return classLoader.getClass().getName() + Objects.hash(classLoader);
    }

    /**
     * Evaluates a String key for identfying a classloader instance, based on the loader class and its hashCode.
     * This prevents the storage of classloader references as keys and therefore enables classloaders not used anymore
     * to be garbage collected.
     *
     * @return the unique key for the current default classloader as returned by #getDefaultClassLoader.
     */
    public static String getClassLoaderID() {
        return getClassLoaderID(getDefaultClassLoader());
    }

    /**
     * Get all items valid for the current thread context class loader, or - if null - the classloader that loaded
     * this class.
     *
     * @return the items found, never null.
     */
    public Set<T> getItems() {
        return getItems(getDefaultClassLoader());
    }

    /**
     * Get all items found for the given classloader and all its parent classloaders.
     *
     * @param classLoader the target top level classloader, not null.
     * @return the items found, never null.
     */
    public Set<T> getItems(ClassLoader classLoader) {
        Set<T> result = new HashSet<>();
        ClassLoader cl = classLoader;
        while (cl != null) {
            T item = getItemNoParent(cl, true);
            result.add(item);
            cl = cl.getParent();
        }
        return result;
    }

    /**
     * Get all items valid for the parent class loader of the current thread context class loader, or - if null - the
     * parent of the classloader that loaded this class. This allows
     * to build a delta list of instances only visible on the target classloader given.
     *
     * @return the items found, never null.
     */
    public Set<T> getParentItems() {
        return getParentItems(getDefaultClassLoader());
    }

    /**
     * Get all items found for the parent of the given classloader and all its parent classloaders. This allows
     * to build a delta list of instances only visible on the target classloader given.
     *
     * @param classLoader the target top level classloader, not null.
     * @return the items found, never null.
     */
    public Set<T> getParentItems(ClassLoader classLoader) {
        Set<T> result = new HashSet<>();
        ClassLoader cl = classLoader.getParent();
        while (cl != null) {
            T item = getItemNoParent(cl, true);
            result.add(item);
            cl = cl.getParent();
        }
        return result;
    }

    /**
     * Return the item assigned to the current thread context class loader or - if null - the class that loaded
     * this class. If not yet loaded this method will NOT trigger a load.
     *
     * @return the item attached, or null.
     */
    public T getItemNoParent() {
        return getItemNoParent(getDefaultClassLoader(), false);
    }

    /**
     * Return the item assigned to the current thread context class loader or - if null - the class that loaded
     * this class.
     *
     * @param loadIfMissing Flag that allows to define if this method will trigger an item load, when no item is loaded
     *                      for the current class loader.
     * @return the item attached, or null.
     */
    public T getItemNoParent(boolean loadIfMissing) {
        return getItemNoParent(getDefaultClassLoader(), loadIfMissing);
    }

    /**
     * Return the item assigned to the current thread context class loader or - if null - the class that loaded
     * this class.
     *
     * @param classLoader   the target top level classloader, not null.
     * @param loadIfMissing Flag that allows to define if this method will trigger an item load, when no item is loaded
     *                      for the current class loader.
     * @return the item attached, or null. If {@code loadIfMissing} is set to true, the result is normally not to be
     * expected to be null.
     */
    public T getItemNoParent(ClassLoader classLoader, boolean loadIfMissing) {
        String clKey = getClassLoaderID(classLoader);
        T item = items.get(clKey);
        if (item == null) {
            if (loadIfMissing) {
                item = createItem(classLoader);
                this.items.put(clKey, item);
            }
        }
        return item;
    }


    /**
     * Utility method that either returns the current thread context classloader or
     * - if not available - the classloader that loaded this class.
     * @return the default classloader to be used, if no explicit classloader has been passed.
     */
    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = AbstractClassloaderAwareItemLoader.class.getClassLoader();
        }
        return cl;
    }


}
