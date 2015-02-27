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
package org.apache.tamaya.events.tests;

import org.apache.tamaya.events.folderobserver.ObservingPropertySourceProvider;
import org.apache.tamaya.format.formats.PropertiesFormat;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

/**
 * Test configuration property source provider that observes a directory and updated the config if necessary.
 */
public class TestObservingProvider extends ObservingPropertySourceProvider{

    public TestObservingProvider(){
        super(Paths.get("C:\\Users\\Anatole\\IdeaProjects\\incubator-tamaya\\modules\\events/src/test/data"),
                new PropertiesFormat());
    }
}
