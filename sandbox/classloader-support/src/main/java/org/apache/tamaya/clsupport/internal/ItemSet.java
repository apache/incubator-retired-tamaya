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
package org.apache.tamaya.clsupport.internal;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

/**
 * Class that acts as a container for items loaded/evaialbe/bound to exactly one classloader.
 */
final class ItemSet<T> implements Iterable<T>{
    /** The managed items. */
    private Set<T> items = new HashSet<>();
    /** the corresponding classloader. */
    private WeakReference<ClassLoader> classLoaderRef;
    /** Timestamp in ms, when the last time the resource set was updated/checked for additional items. */
    private long lastUpdate;

    /**
     * Creates a new resource set.
     * @param cl the classloader, not null.
     * @param items the items to be added, not null.
     */
    public ItemSet(ClassLoader cl, Collection<T> items){
        this.classLoaderRef = new WeakReference<ClassLoader>(Objects.requireNonNull(cl));
        this.items.addAll(items);
    }

    /**
     * Removes all items.
     */
    public void clear(){
        Set<T> res = this.items;
        if(res!=null) {
            res.clear();
        }
    }

    /**
     * Adds the given items. If the resource set is not active this method return false and
     * no resource will be added.
     * @param resources the items to be added, not null.
     * @return true, if adding was succesful.
     */
    public boolean addResources(Collection<T> resources) {
        if(!isActive()){
            return false;
        }
        Set<T> res = this.items;
        if(res!=null) {
            res.addAll(resources);
            this.lastUpdate = System.currentTimeMillis();
        }
        return true;
    }

    public boolean contains(T resource) {
        return getItems().contains(resource);
    }

    public Set<T> getItems(){
        if(classLoaderRef==null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(this.items);
    }

    @Override
    public Iterator<T> iterator() {
        return getItems().iterator();
    }

    /**
     * Checks, if the referenced classloader was garbage collected.
     * @return true if the referenced classloader was garbage collected.
     */
    public boolean isActive(){
        return classLoaderRef==null;
    }

    /**
     * Gets the timestamp, when the resource was updated the last time.
     * @return the timestamp, when the resource was updated the last time.
     */
    public long getLastUpdate(){
        return lastUpdate;
    }

    /**
     * Updates the items, e.g. by adding new items found.
     * @param resources the updated items.
     */
    public void update(Collection<T> resources) {
        if(classLoaderRef!=null) {
            if (classLoaderRef.get()==null) {
                this.items = null;
            }
            else{
                if(this.items !=null){
                    Set<T> newRes = new HashSet<>(resources.size());
                    newRes.addAll(resources);
                    this.items = newRes;
                }
            }
            this.lastUpdate = System.currentTimeMillis();
        }
    }


    @Override
    public String toString() {
        return "ItemSet{" +
                "items=" + items +
                ", classLoaderRef=" + classLoaderRef +
                ", lastUpdate=" + lastUpdate +
                '}';
    }

}
