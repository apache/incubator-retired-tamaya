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
package org.apache.tamaya.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Metrics container containing access statistics for a given configuration key.
 */
public final class Usage {
    private static final Logger LOG = Logger.getLogger(Usage.class.getName());
    private static final String[] EMPTY_TRACE = new String[0];
    /**
     * the config section.
     */
    private final String key;

    /**
     * Maps with usage references, key is the fully qualified package name.
     */
    private final Map<String,AccessDetail> accessDetails = new ConcurrentHashMap<>();
    /**
     * The maximal length of the stacktrace stored.
     */
    private static int maxTrace = 10;

    /**
     * Creates a usage metric container for a given key.
     * @param key the parameter (fully qualified).
     */
    public Usage(String key) {
        this.key = Objects.requireNonNull(key);
    }

    /**
     * Get the maximal length of the stack traces recorded, default is 10.
     * @return the maximal length of the stack traces recorded
     */
    public static int getMaxTraceLength(){
        return Usage.maxTrace;
    }

    /**
     * Sets the maximal length of the stacktraces stored when tracking configuration
     * usage. Setting it to a negative value, disabled stacktrace logging.
     * @param maxTrace the maximal recorded stack length.
     */
    public static void setMaxTraceLength(int maxTrace){
        Usage.maxTrace =maxTrace;
    }

    /**
     * Get the configModel section.
     *
     * @return the section, never null.
     */
    public String getKey() {
        return key;
    }

    /**
     * Clears all collected usage metrics for this key.
     */
    public void clearMetrics(){
        this.accessDetails.clear();
    }

    /**
     * Get the detail message.
     *
     * @return the detail message, or null.
     */
    public int getReferenceCount() {
        return accessDetails.size();
    }

    /**
     * Get the detail message.
     *
     * @return the detail message, or null.
     */
    public int getUsageCount() {
        int count = 0;
        for(AccessDetail ref: accessDetails.values()){
            count += ref.getAccessCount();
        }
        return count;
    }

    /**
     * Access a usage reference for a given class.
     * @param type class to get usage references for, not null.
     * @return the usage ref, if present, or null.
     */
    public Collection<AccessDetail> getAccessDetails(Class type){
        return getAccessDetails(type.getName() +"\\..*");
    }

    /**
     * Access a usage reference for a given package.
     * @param pack package to get usage references for, not null.
     * @return the usage ref, if present, or null.
     */
    public Collection<AccessDetail> getAccessDetails(Package pack){
        return getAccessDetails(pack.getName() +"\\..*");
    }

    /**
     * Find usages of this key for the given expression (regex). Hereby the expression is
     * matched with the tracked reference identifier, which has the form
     * {@code f.q.n.ClassName#methodName(line: 123)}.
     * @param lookupExpression the target lookup expression, not null.
     * @return the matching references, not null.
     */
    public Collection<AccessDetail> getAccessDetails(String lookupExpression){
        List<AccessDetail> result = new ArrayList<>();
        for(AccessDetail ref:this.accessDetails.values()){
            if(ref.getAccessPoint().matches(lookupExpression)){
                result.add(ref);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Usage Count: " + key + " -> " + getUsageCount() + '\n';
    }

    /**
     * Get the access details (stacktrace etc) for this reference.
     * @return return the access details, not null.
     */
    public Collection<AccessDetail> getAccessDetails(){
        return Collections.unmodifiableCollection(accessDetails.values());
    }

    /**
     * Evaluates the current access point from the current stacktrace and adds an according
     * usage reference object (or updates any existing one) for the given key. The
     * stacktrace is shortened to a maximal size of 20 items.
     * @param value the value returned, not null.
     */
    public void trackUsage(String value){
        trackUsage(value, maxTrace);
    }

    /**
     * Evaluates the current access point from the current stacktrace and adds an according
     * usage reference object (or updates any existing one) for the given key.
     * @param value the value returned, not null.
     * @param maxTraceLength the maximal length of the stored stacktrace.
     */
    public void trackUsage(String value, int maxTraceLength){
        String accessPoint = null;
        if(maxTraceLength>0) {
            Exception e = new Exception();
            List<String> trace = new ArrayList<>();
            stack:
            for (StackTraceElement ste : e.getStackTrace()) {
                for (String ignored : ConfigUsageStats.getIgnoredUsagePackages()) {
                    if (ste.getClassName().startsWith(ignored)) {
                        continue stack;
                    }
                }
                String ref = ste.getClassName() + '#' + ste.getMethodName() + "(line:" + ste.getLineNumber() + ')';
                trace.add(ref);
                if (accessPoint == null) {
                    accessPoint = ref;
                }
                if (trace.size() >= maxTraceLength) {
                    break;
                }
            }
            if (accessPoint == null) {
                // all ignored, take first one, with different package
                accessPoint = "<unknown/filtered/internal>";
            }
            AccessDetail details = getAccessDetails(accessPoint, trace.toArray(new String[trace.size()]));
            details.trackAccess(value);
        }else{
            accessPoint = "<disabled>";
            AccessDetail details = getAccessDetails(accessPoint, EMPTY_TRACE);
            details.trackAccess(value);
        }
    }

    private AccessDetail getAccessDetails(String accessPoint, String[] trace) {
        AccessDetail details = accessDetails.get(accessPoint);
        if(details==null){
            details = new AccessDetail(key, accessPoint, trace);
            accessDetails.put(accessPoint, details);
        }
        return details;
    }

    /**
     * Class modelling the access details tracked per detailed item, e.g. per class in the owning package.
     */
    public static final class AccessDetail {
        private String key;
        private AtomicLong accessCount = new AtomicLong();
        private long lastAccessTS;
        private long firstAccessTS;
        private String[] stackTrace;
        private String accessPoint;
        private Map<Long, String> trackedValues;

        public AccessDetail(String key, String accessPoint, String[] stackTrace){
            this.key = Objects.requireNonNull(key);
            this.accessPoint = Objects.requireNonNull(accessPoint);
            this.stackTrace = stackTrace.clone();
        }

        public void clearStats(){
            lastAccessTS = 0;
            firstAccessTS = 0;
            accessCount.set(0);
        }

        public long trackAccess(String value){
            long count = accessCount.incrementAndGet();
            lastAccessTS = System.currentTimeMillis();
            if(firstAccessTS==0){
                firstAccessTS = lastAccessTS;
            }
            if(value!=null){
                synchronized (this) {
                    if(trackedValues==null){
                        trackedValues = new HashMap<>();
                    }
                    trackedValues.put(lastAccessTS, value);
                }
            }
            return count;
        }

        public String getKey(){
            return key;
        }

        public long getAccessCount() {
            return accessCount.get();
        }

        public String getAccessPoint() {
            return accessPoint;
        }

        public long getFirstAccessTS() {
            return firstAccessTS;
        }

        public long getLastAccessTS() {
            return lastAccessTS;
        }

        public String[] getStackTrace() {
            return stackTrace.clone();
        }

        public Map<Long, String> getTrackedValues(){
            synchronized (this) {
                if (trackedValues == null) {
                    return Collections.emptyMap();
                } else {
                    return new HashMap<>(trackedValues);
                }
            }
        }

        @Override
        public String toString() {
            return "AccessDetails{" +
                    "key=" + key +
                    ", accessCount=" + accessCount +
                    ", lastAccessTS=" + lastAccessTS +
                    ", firstAccessTS=" + firstAccessTS +
                    ", stackTrace=" + Arrays.toString(stackTrace) +
                    ", accessPoint='" + accessPoint + '\'' +
                    ", trackedValues=" + trackedValues +
                    '}';
        }
    }

}
