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
//import org.apache.tamaya.ConfigurationProvider;
//import org.apache.tamaya.ui.services.MessageProvider;
//
//import java.util.Locale;
//import java.util.ResourceBundle;
//
///**
// * Component resolving messages for being shown in the UI, based on the ResourceBundle mechanisms.
// * The baseName used can optionally be configured by setting {@code tamaya.ui.baseName} either as system property,
// * environment property or Tamaya configuration. Be aware that the JDK resource bundle mechanism only reads
// * the first property file on the classpath (or a corresponding class file implementing ResourceBundle).
// */
//public class ResourceBundleMessageProvider implements MessageProvider{
//
//    private static final String BASENAME = evaluateBaseName();
//
//    /**
//     * The property name for configuring the resource bundle's base name either as
//     * system property, environment property or configuration entry.
//     */
//    private static final String TAMAYA_UI_BASE_NAME = "tamaya.ui.baseName";
//
//    /**
//     * Evaluates the base name to be used for creating the resource bundle used.
//     * @return
//     */
//    private static String evaluateBaseName() {
//        String baseName = System.getProperty(TAMAYA_UI_BASE_NAME);
//        if(baseName==null || baseName.isEmpty()){
//            baseName = System.getenv("tamaya.ui.baseName");
//        }
//        if(baseName==null || baseName.isEmpty()){
//            baseName = ConfigurationProvider.getConfiguration().get("tamaya.ui.baseName");
//        }
//        if(baseName==null || baseName.isEmpty()){
//            baseName = "ui/ui.lang/tamaya";
//        }
//        return baseName;
//    }
//
//    /**
//     * Private singleton constructor.
//     */
//    public ResourceBundleMessageProvider(){}
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
//            ResourceBundle bundle = ResourceBundle.getBundle(BASENAME, locale);
//            return bundle.getString(bundleID);
//        }
//        catch(Exception e){
//            return bundleID;
//        }
//    }
//
//}
