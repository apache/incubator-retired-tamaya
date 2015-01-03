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
//package org.apache.tamaya.core.config;
//
//import org.apache.tamaya.Configuration;
//import org.junit.Test;
//
//import java.beans.PropertyChangeEvent;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertFalse;
//
///**
// * Simple testdata for a mutable Configuration instance.
// */
//public class MutableConfigTest {
//
//    @Test
//    public void accessMutableConfig() {
//        Configuration config = Configuration.current("mutableTestConfig");
//        ConfigChangeSet changeSet = ConfigChangeSetBuilder.of(config).put("testCase", "accessMutableConfig")
//                .put("execTime", System.currentTimeMillis()).put("execution", "once").build();
//        List<PropertyChangeEvent> changes = new ArrayList<>();
//        Configuration.addChangeListener(change -> {
//            if (change.getPropertySource() == config) {
//                changes.addAll(change.getEvents());
//            }
//        });
//        config.applyChanges(changeSet);
//        assertFalse(changes.isEmpty());
//        System.out.println(changes);
//    }
//}
