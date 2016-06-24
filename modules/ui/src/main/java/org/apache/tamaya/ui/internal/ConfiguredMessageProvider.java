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
//package org.apache.tamaya.ui.internal;
//
//import org.apache.tamaya.ui.services.MessageProvider;
//
//import java.util.Locale;
//import java.util.ResourceBundle;
//
///**
// * Component resolving messages for being shown in the UI, based on the ResourceBundle mechanisms.
// */
//public class ConfiguredMessageProvider implements MessageProvider{
//
//    /**
//     * Private singleton constructor.
//     */
//    public ConfiguredMessageProvider(){}
//
//    /**
//     * Get a message using the defaul locale.
//     * @param bundleID the message bundle key, not null.
//     * @return the resolved message, or the bundle ID, never null.
//     */
//    public String getMessage(String bundleID){
//        return getMessage(bundleID, Locale.getDefault());
//    }
//
//    /**
//     * Get a message.
//     * @param bundleID the message bundle key, not null.
//     * @param locale the target locale, or null, for the default locale.
//     * @return the resolved message, or the bundle ID, never null.
//     */
//    public String getMessage(String bundleID, Locale locale){
//        try{
//            ResourceBundle bundle = ResourceBundle.getBundle("ui/ui.lang/tamaya", locale);
//            return bundle.getString(bundleID);
//        }
//        catch(Exception e){
//            return bundleID;
//        }
//    }
//
//}
