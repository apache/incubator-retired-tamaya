/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.tamaya.spisupport.propertysource;

import java.io.StringReader;
import java.io.StringWriter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for PropertySource for reading main arguments as configuration.
 */
public class CLIPropertySourceTest {

    @Test
    public void setCLIProps() throws Exception {
        StringWriter stringBufferWriter = new StringWriter();
        System.getProperties().store(stringBufferWriter, null);
        String before = stringBufferWriter.toString();

        try {
            System.clearProperty("main.args");
            
            CLIPropertySource ps = new CLIPropertySource();
            assertThat(ps.getProperties().isEmpty()).isTrue();
            
            ps = new CLIPropertySource(26);
            assertThat(ps.getProperties().isEmpty()).isTrue();
            assertThat(ps.getOrdinal()).isEqualTo(26);
            
            ps = new CLIPropertySource("-a", "b");
            assertThat(ps.getProperties().isEmpty()).isFalse();
            assertThat("b").isEqualTo(ps.getProperties().get("a").getValue());
            assertThat(ps.toStringValues().contains("args=[-a, b]")).isTrue();
            
            ps = new CLIPropertySource(16, "-c", "d");
            assertThat(ps.getProperties().isEmpty()).isFalse();
            assertThat("d").isEqualTo(ps.getProperties().get("c").getValue());
            assertThat(ps.getOrdinal()).isEqualTo(16);
            
            CLIPropertySource.initMainArgs("-e", "f");
            assertThat(ps.getProperties().isEmpty()).isFalse();
            assertThat("f").isEqualTo(ps.getProperties().get("e").getValue());
            
            CLIPropertySource.initMainArgs("--g");
            assertThat(ps.getProperties().isEmpty()).isFalse();
            assertThat("g").isEqualTo(ps.getProperties().get("g").getValue());
            
            CLIPropertySource.initMainArgs("sss");
            assertThat(ps.getProperties().isEmpty()).isFalse();
            assertThat("sss").isEqualTo(ps.getProperties().get("sss").getValue());
            
            CLIPropertySource.initMainArgs("-a", "b", "--c", "sss", "--val=vvv");
            assertThat(ps.getProperties().isEmpty()).isFalse();
            assertThat("b").isEqualTo(ps.getProperties().get("a").getValue());
            assertThat("c").isEqualTo(ps.getProperties().get("c").getValue());
            assertThat("sss").isEqualTo(ps.getProperties().get("sss").getValue());
            
            System.setProperty("main.args", "-a b\t--c sss  ");
            ps = new CLIPropertySource();
            assertThat(ps.getProperties().isEmpty()).isFalse();
            System.clearProperty("main.args");
            assertThat("b").isEqualTo(ps.getProperties().get("a").getValue());
            assertThat("c").isEqualTo(ps.getProperties().get("c").getValue());
            assertThat("sss").isEqualTo(ps.getProperties().get("sss").getValue());
            
        } finally {
            System.getProperties().clear();
            System.getProperties().load(new StringReader(before));
        }
    }
}
