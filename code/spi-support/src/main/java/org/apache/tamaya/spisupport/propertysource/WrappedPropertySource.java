/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.spisupport.propertysource;

import org.apache.tamaya.spi.ChangeSupport;
import org.apache.tamaya.spi.PropertySource;
import org.apache.tamaya.spi.PropertyValue;
import org.apache.tamaya.spisupport.PropertySourceComparator;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Property source effectively managed by the configuration context, allowing resetting of ordinal and its
 * delegate (e.g. in case of refresh).
 */
class WrappedPropertySource implements PropertySource{

    private Integer ordinal;
    private PropertySource delegate;
    private long loaded = System.currentTimeMillis();

    private WrappedPropertySource(PropertySource delegate) {
        this(delegate, null);
    }

    private WrappedPropertySource(PropertySource delegate, Integer ordinal) {
        this.delegate = Objects.requireNonNull(delegate);
        this.ordinal = ordinal;
    }

    public static WrappedPropertySource of(PropertySource ps) {
        if(ps instanceof  WrappedPropertySource){
            return (WrappedPropertySource)ps;
        }
        return new WrappedPropertySource(ps);
    }

    public static WrappedPropertySource of(PropertySource ps, Integer ordinal) {
        if(ps instanceof  WrappedPropertySource){
            return new WrappedPropertySource(((WrappedPropertySource)ps).getDelegate(), ordinal);
        }
        return new WrappedPropertySource(ps, ordinal);
    }

    public int getOrdinal() {
        if(this.ordinal!=null){
            return this.ordinal;
        }
        return PropertySourceComparator.getOrdinal(delegate);
    }

    public void setOrdinal(Integer ordinal) {
        this.ordinal = ordinal;
    }

    public void setDelegate(PropertySource delegate) {
        this.delegate = Objects.requireNonNull(delegate);
        this.loaded = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public PropertyValue get(String key) {
        return delegate.get(key);
    }

    @Override
    public Map<String, PropertyValue> getProperties() {
        return delegate.getProperties();
    }

    @Override
    public boolean isScannable() {
        return delegate.isScannable();
    }

    @Override
    public ChangeSupport getChangeSupport() {
        return delegate.getChangeSupport();
    }

    @Override
    public String getVersion() {
        return delegate.getVersion();
    }

    @Override
    public void addChangeListener(BiConsumer<Set<String>, PropertySource> l) {
        delegate.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(BiConsumer<Set<String>, PropertySource> l) {
        delegate.removeChangeListener(l);
    }

    @Override
    public void removeAllChangeListeners() {
        delegate.removeAllChangeListeners();
    }

    public PropertySource getDelegate() {
        return delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WrappedPropertySource)) {
            return false;
        }

        WrappedPropertySource that = (WrappedPropertySource) o;

        return getDelegate().getName().equals(that.getDelegate().getName());
    }

    @Override
    public int hashCode() {
        return getDelegate().getName().hashCode();
    }

    @Override
    public String toString() {
        return "WrappedPropertySource{" +
                "name=" + getName() +
                ", ordinal=" + getOrdinal() +
                ", scannable=" + isScannable() +
                ", changeSupport=" + getChangeSupport() +
                ", loadedAt=" + loaded +
                ", delegate-class=" + delegate.getClass().getName() +
                '}';
    }
}
