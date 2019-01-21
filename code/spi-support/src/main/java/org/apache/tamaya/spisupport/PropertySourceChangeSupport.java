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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Simple support class for helping with change management on property sources.
 */
public final class PropertySourceChangeSupport {

    private static final Logger LOG = Logger.getLogger(PropertySourceChangeSupport.class.getName());

    private ChangeSupport changeSupport;
    private PropertySource propertySource;
    private AtomicLong version = new AtomicLong();
    private List<BiConsumer<Set<String>, PropertySource>> listeners  = new ArrayList<>();
    private int oldHash = 0;
    private Map<String, PropertyValue> valueMap;
    private long timestamp;
    private ScheduledFuture scheduleTask;

    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    /**
     * Create a new property change support instance.
     * @param changeSupport the support type, not null.
     * @param propertySource the property source to pass to listeners, not null.
     */
    public PropertySourceChangeSupport(ChangeSupport changeSupport,
                                       PropertySource propertySource){
        this.changeSupport = Objects.requireNonNull(changeSupport);
        this.propertySource = Objects.requireNonNull(propertySource);
    }

    public ChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public String getVersion() {
        return version.get() + ": timestamp="+timestamp;
    }

    public void addChangeListener(BiConsumer<Set<String>, PropertySource> l){
        switch(changeSupport){
            case SUPPORTED:
                if(!listeners.contains(l)){
                    listeners.add(l);
                }
                break;
            case UNSUPPORTED:
            case IMMUTABLE:
                default:
                break;
        }
    }

    public void removeChangeListener(BiConsumer<Set<String>, PropertySource> l){
        if(changeSupport==ChangeSupport.SUPPORTED) {
            listeners.remove(l);
        }
    }

    public void removeAllChangeListeners(){
        if(changeSupport==ChangeSupport.SUPPORTED) {
            listeners.clear();
        }
    }

    public long load(Map<String, PropertyValue> properties){
        Objects.requireNonNull(properties);
        if(changeSupport==ChangeSupport.SUPPORTED) {
            Set<String> changedKeys = calculateChangedKeys(this.valueMap, properties);
            if(!changedKeys.isEmpty()) {
                this.valueMap = properties;
                version.incrementAndGet();
                fireListeners(changedKeys);
            }
        } else {
            if(!properties.equals(this.valueMap)){
                this.valueMap = properties;
                version.incrementAndGet();
            }
        }
        return version.get();
    }

    private Set<String> calculateChangedKeys(Map<String, PropertyValue> valueMap, Map<String, PropertyValue> newValues) {
        Set<String> result = new HashSet<>();
        if(this.valueMap!=null) {
            for (Map.Entry<String, PropertyValue> en : valueMap.entrySet()) {
                if (!newValues.containsKey(en.getKey())) {
                    result.add(en.getKey()); // removed
                }
            }
        }
        for(Map.Entry<String, PropertyValue> en:newValues.entrySet()){
            if(valueMap != null){
                if(!valueMap.containsKey(en.getKey())) {
                    result.add(en.getKey()); // added
                }
                if(!Objects.equals(valueMap.get(en.getKey()), en.getValue())) {
                    result.add(en.getKey()); // changed
                }
            }else{
                result.add(en.getKey()); // added
            }
        }
        return result;
    }

    private void fireListeners(Set<String> changedKeys) {
        for(BiConsumer<Set<String>, PropertySource> l:this.listeners){
            try{
                l.accept(changedKeys, propertySource);
            }catch(Exception e){
                LOG.log(Level.WARNING, "Failed to load listener on property source change: " + l, e);
            }
        }
    }

    public PropertyValue getValue(String key){
        if(valueMap==null){
            return null;
        }
        return valueMap.get(key);
    }

    public Map<String, PropertyValue> getProperties(){
        if(valueMap==null){
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(valueMap);
    }

    public void scheduleChangeMonitor(Supplier<Map<String, PropertyValue>> propertySupplier, long duration, TimeUnit timeUnit){
        if(changeSupport==ChangeSupport.SUPPORTED) {
            Objects.requireNonNull(propertySupplier);
            scheduleTask = executorService.schedule(() -> {
                load(propertySupplier.get());
            }, duration, timeUnit);
        }
    }

    public void cancelSchedule(){
        if(changeSupport==ChangeSupport.SUPPORTED && scheduleTask!=null){
            scheduleTask.cancel(false);
        }
    }

    private int hashCode(Map<String, PropertyValue> valueMap) {
        int result = 0;
        for(Map.Entry<String,PropertyValue> en:valueMap.entrySet()) {
            result = 31 * result + en.getKey().hashCode();
            String value = en.getValue().getValue();
            result = 31 * result + (value!=null?value.hashCode():0);
        }
        return result;
    }
}
