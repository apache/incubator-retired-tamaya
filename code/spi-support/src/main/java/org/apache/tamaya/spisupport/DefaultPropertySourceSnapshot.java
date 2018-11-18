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

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;

/**
 * PropertySource implementation that stores all current values of a given (possibly dynamic, contextual and non server
 * capable instance) and is fully serializable. Note that hereby only the scannable key/createValue pairs are considered.
 */
public class DefaultPropertySourceSnapshot implements PropertySource, Serializable {
    private static final long serialVersionUID = -6373137316556444171L;
    private static final int MAX_SYNCH_CHECKS = 10;
    private static final Logger LOG = Logger.getLogger(DefaultPropertySourceSnapshot.class.getName());

    /**
     * The PropertySource's name.
     */
    private String name;
    /**
     * The ordinal.
     */
    private int ordinal;
    /**
     * The properties read.
     */
    private Map<String, PropertyValue> properties = new HashMap<>();

    private Set<String> keys = new HashSet<>();

    private long frozenAt = System.currentTimeMillis();

    /**
     * Constructor.
     *
     * @param propertySource The base PropertySource.
     */
    public DefaultPropertySourceSnapshot(PropertySource propertySource) {
        this(propertySource, propertySource.getProperties().keySet());
    }

    /**
     * Constructor.
     *
     * @param propertySource The base PropertySource.
     * @param keys
     */
    public DefaultPropertySourceSnapshot(PropertySource propertySource, Iterable<String> keys) {
        for(String k:keys){
            this.keys.add(k);
        }
        if(this.keys.isEmpty()){
            this.keys.addAll(getProperties().keySet());
        }
        this.keys = Collections.unmodifiableSet(this.keys);
        this.ordinal = PropertySourceComparator.getOrdinal(propertySource);
        this.name = propertySource.getName();
        if(propertySource.getChangeSupport().equals(ChangeSupport.UNSUPPORTED) ||
            propertySource.getChangeSupport().equals(ChangeSupport.IMMUTABLE)){
            // simply get the keys and we are done. We cant do more...
            this.properties = initProperties(propertySource, false);
        }else{
            this.properties = initProperties(propertySource, true);
        }
    }

    private Map<String, PropertyValue> initProperties(PropertySource propertySource, boolean checkVersion) {
        Map<String, PropertyValue> properties = new HashMap<>();
        if(!checkVersion){
            // Simply collect values and we are done
            for(String key:keys) {
                PropertyValue val = propertySource.get(key);
                if(val != null) {
                    properties.put(key, val);
                }
            }
        }else {
            // Collect values, but ensure, the propert
            String version = propertySource.getVersion();
            String newVersion = null;
            int checksDone = 0;
            while (!Objects.equals(newVersion, version)) {
                for (String key : keys) {
                    PropertyValue val = propertySource.get(key);
                    if (val != null) {
                        properties.put(key, val);
                    }
                }
                newVersion = propertySource.getVersion();
                if (checksDone++ > MAX_SYNCH_CHECKS) {
                    LOG.info("Property Source is instable, will abort freeze, but inconsistent config may be possible: " + propertySource.getName());
                    break;
                }

            }
        }
        return Collections.unmodifiableMap(properties);
    }

    /**
     * Creates a new FrozenPropertySource instance based on a PropertySource and the target key set given. This method
     * uses all keys available in the property map.
     *
     * @param propertySource the property source to be frozen, not null.
     * @return the frozen property source.
     */
    public static DefaultPropertySourceSnapshot of(PropertySource propertySource) {
        Set<String> keySet = propertySource.getProperties().keySet();
        return DefaultPropertySourceSnapshot.of(propertySource, keySet);
    }

    /**
     * Creates a new FrozenPropertySource instance based on a PropertySource and the target key set given.
     *
     * @param propertySource the property source to be frozen, not null.
     * @param keys the keys to be evaluated for the snapshot. Only these keys will be contained in the resulting
     *             snapshot.
     * @return the frozen property source.
     */
    public static DefaultPropertySourceSnapshot of(PropertySource propertySource, Iterable<String> keys) {
        if (propertySource instanceof DefaultPropertySourceSnapshot) {
            DefaultPropertySourceSnapshot fps = (DefaultPropertySourceSnapshot) propertySource;
            if(fps.getKeys().equals(keys)){
                return fps;
            }
        }
        return new DefaultPropertySourceSnapshot(propertySource, keys);
    }

    public Set<String> getKeys() {
        return keys;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    /**
     * Get the creation timestamp of this instance.
     * @return the creation timestamp
     */
    public long getFrozenAt(){
        return frozenAt;
    }

    @Override
    public PropertyValue get(String key) {
        return this.properties.get(key);
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultPropertySourceSnapshot)) {
            return false;
        }
        DefaultPropertySourceSnapshot that = (DefaultPropertySourceSnapshot) o;
        return ordinal == that.ordinal && properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        int result = ordinal;
        result = 31 * result + properties.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FrozenPropertySource{" +
                "name=" + name +
                ", ordinal=" + ordinal +
                ", properties=" + properties +
                ", frozenAt=" + frozenAt +
                '}';
    }
}
