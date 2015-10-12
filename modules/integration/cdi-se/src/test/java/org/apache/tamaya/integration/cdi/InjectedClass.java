/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy current the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.tamaya.integration.cdi;

import org.apache.tamaya.Configuration;
import org.apache.tamaya.spi.ConfigurationContext;
import org.apache.tamaya.spi.ConfigurationContextBuilder;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class loaded by CDI to test correct injection of Configuration API artifacts.
 */
@Singleton
public class InjectedClass {

    @Inject
    Configuration config;

    @Inject
    Configuration config2;

    @Inject
    ConfigurationContext configContext;

    @Inject
    ConfigurationContext configContext2;

    @Inject
    ConfigurationContextBuilder builder1;

    @Inject
    ConfigurationContextBuilder builder2;

    @Override
    public String toString() {
        return "InjectedClass{" +
                "config=" + config +
                ", configContext=" + configContext +
                ", builder1=" + builder1 +
                ", builder2=" + builder2 +
                '}';
    }
}
