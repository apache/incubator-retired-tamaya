///*
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// */
//package org.apache.tamaya.core.internal.converters;
//
//import org.apache.tamaya.*;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.function.Supplier;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * Supplier implementation for a typed configuration entry.
// */
//final class EntrySupplier<T> implements Supplier<T> {
//
//    private static final Logger LOG = Logger.getLogger(EntrySupplier.class.getName());
//
//    private Supplier<Configuration> configSupplier;
//    private List<String> keys = new ArrayList<>();
//    private String defaultValue;
//    private TypeLiteral valueType;
//
//    private EntrySupplier(Supplier<Configuration> configSupplier,
//             List<String> keys, String defaultValue, TypeLiteral valueType){
//        this.configSupplier = Objects.requireNonNull(configSupplier);
//        this.keys.addAll(Objects.requireNonNull(keys));
//        this.defaultValue = defaultValue;
//        this.valueType = Objects.requireNonNull(valueType);
//    }
//
//    public Supplier<Configuration> getConfigSupplier() {
//        return configSupplier;
//    }
//
//    public List<String> getKeys() {
//        return keys;
//    }
//
//    public String getDefaultValue() {
//        return defaultValue;
//    }
//
//    public TypeLiteral getValueType() {
//        return valueType;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        return getClass().equals(o.getClass());
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }
//
//    @Override
//    public T get() {
//        T result = null;
//        for (String key : keys) {
//            try {
//                result = (T)configSupplier.get()
//                        .getOrDefault(key, valueType, null);
//                if (result != null) {
//                    return result;
//                }
//            } catch (Exception e) {
//                LOG.log(Level.FINE, "Cannot evaluate key '" + key + "' of type " + valueType, e);
//            }
//        }
//        throw new ConfigException("Could not evaluate any config for of type " + valueType +
//                " for keys: " + keys);
//    }
//
//
//}
