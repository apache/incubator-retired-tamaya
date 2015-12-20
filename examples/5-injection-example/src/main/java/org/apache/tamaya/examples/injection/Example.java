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
package org.apache.tamaya.examples.injection;

import org.apache.tamaya.inject.api.Config;
import org.apache.tamaya.inject.api.ConfigDefaultSections;

/**
 * Simple example bean, mapped by default names mostly.
 */
@ConfigDefaultSections("example")
@SuppressWarnings("all")
public class Example {

    private String type;
    private String name;
    @Config(defaultValue = "No description available.")
    private String description;
    private int version;
    @Config("author")
    private String exampleAuthor;

    @Override
    public String toString() {
        return "Example Metadata:" +
                "type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", version=" + version +
                ", exampleAuthor='" + exampleAuthor + '\'';
    }
}
