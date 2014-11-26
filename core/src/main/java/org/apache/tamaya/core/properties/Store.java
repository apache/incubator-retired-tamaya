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
package org.apache.tamaya.core.properties;

import java.lang.ref.WeakReference;
import java.util.*;

/**
 * This store encapsulates an list of WeakReferences to items.
 * It cleans up the list from null references, when an item is removed, or an Iterator is created.
 * Created by Anatole on 10.04.2014.
 */
public class Store<T>implements Iterable<T> {

    private List<WeakReference<T>> weakItems = new ArrayList<>();

    private List<T> items = new ArrayList<>();

    private final Object LOCK = new Object();

    public void add(T item){
        Objects.requireNonNull(item);
        this.items.add(item);
    }

    public void addWeak(T item){
        Objects.requireNonNull(item);
        this.weakItems.add(new WeakReference<>(item));
    }

    public void remove(T item){
        Objects.requireNonNull(item);
        synchronized(LOCK){
            for(Iterator<WeakReference<T>> iter = weakItems.iterator(); iter.hasNext();iter.next()){
                WeakReference<T> wr = iter.next();
                T t = wr.get();
                if(t==null || t.equals(item)){
                    iter.remove();
                }
            }
            for(Iterator<T> iter = items.iterator(); iter.hasNext();iter.next()){
                T t = iter.next();
                if(t==null || t.equals(item)){
                    iter.remove();
                }
            }
        }
    }

    public List<T> toList(){
        List<T> newList = new ArrayList<>();
        synchronized(LOCK){
            Iterator<T> iter = items.iterator();
            while(iter.hasNext()){
                T t = iter.next();
                newList.add(t);
            }
            for (WeakReference<T> wr : weakItems) {
                T t = wr.get();
                if (t == null) {
                    iter.remove();
                } else {
                    newList.add(t);
                }
            }
        }
        return newList;
    }

    @Override
    public Iterator<T> iterator(){
        return toList().iterator();
    }

    @Override
    public String toString(){
        return "Store{" +
                "items=" + items +
                ", weakItems=" + weakItems +
                '}';
    }
}
