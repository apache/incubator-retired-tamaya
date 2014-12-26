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
package org.apache.tamaya.samples.annotations;

import org.apache.tamaya.annotation.ConfiguredProperty;
import org.apache.tamaya.annotation.DefaultValue;

import java.math.BigDecimal;

/**
 * Created by Anatole on 08.09.2014.
 */
public interface ConfigTemplate {

    @ConfiguredProperty
    String testProperty();

    @ConfiguredProperty(keys = "Foo")
    @DefaultValue("The current \\${JAVA_HOME} env property is ${env:JAVA_HOME}.")
    String value1();

    // COMPUTERNAME is only under Windows available
    @ConfiguredProperty
    @DefaultValue("${env:COMPUTERNAME}")
    String computerName();

    @ConfiguredProperty(keys = "HOME")
    String homeDir();

    @ConfiguredProperty
    @DefaultValue("N/A")
    String runtimeVersion();

    @ConfiguredProperty
    @DefaultValue("${sys:java.version}")
    String javaVersion2();

    @ConfiguredProperty
    @DefaultValue("5")
    Integer int1();

    @ConfiguredProperty
    @DefaultValue("2233")
    int int2();

    @ConfiguredProperty
    boolean booleanT();

    @ConfiguredProperty(keys = "BD")
    BigDecimal bigNumber();
}
