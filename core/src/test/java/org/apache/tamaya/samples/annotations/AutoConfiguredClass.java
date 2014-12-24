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

import org.apache.tamaya.annotation.DefaultValue;
import org.apache.tamaya.annotation.NoConfig;
import org.apache.tamaya.annotation.ObservesConfigChange;

import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;

/**
 * Test example of a configured tyü that is using default config key resolution.
 */
public class AutoConfiguredClass {

    private String testProperty;

    @DefaultValue("The current \\${JAVA_HOME} env property is ${env:JAVA_HOME}.")
    String value1;

    @NoConfig
    private String value2;

    @DefaultValue("N/A")
    private String runtimeVersion;

    @DefaultValue("${java.version}")
    private String javaVersion2;

    @DefaultValue("5")
    private Integer int1;

    private int int2;

    @ObservesConfigChange
    public void changeListener1(PropertyChangeEvent configChange){
        // will be called
    }

    public String getTestProperty() {
        return testProperty;
    }

    public String getValue1() {
        return value1;
    }

    public String getValue2() {
        return value2;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public String getJavaVersion2() {
        return javaVersion2;
    }

    public Integer getInt1() {
        return int1;
    }

    public int getInt2() {
        return int2;
    }

    public String toString(){
        return super.toString() + ": testProperty="+testProperty+", value1="+value1+", value2="+value2
                +", int1="+int1+", int2="+int2
                +", runtimeVersion="+runtimeVersion+", javaVersion2="+javaVersion2;
    }

}
