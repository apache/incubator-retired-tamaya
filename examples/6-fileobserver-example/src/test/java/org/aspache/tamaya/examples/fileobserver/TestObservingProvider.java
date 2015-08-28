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
package org.aspache.tamaya.examples.fileobserver;

import org.apache.tamaya.events.folderobserver.ObservingPropertySourceProvider;
import org.apache.tamaya.format.formats.PropertiesFormat;

import java.nio.file.Path;

/**
 * Test configuration property source provider that observes a directory and updated the config if necessary.
 */
public class TestObservingProvider extends ObservingPropertySourceProvider{

    public static Path propertyLocation;

    public TestObservingProvider(){
        super(propertyLocation, new PropertiesFormat());
    }
}
