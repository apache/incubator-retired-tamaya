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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spi.PropertySource;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author William.Lieurance 2018.02.18
 */
public class DefaultConfigurationContextTest {
    
    @Test
    public void testEqualsAndHashAndToStringValues() {
        PropertySource sharedSource = new MockedPropertySource();
        DefaultConfigurationContext ctx1 = (DefaultConfigurationContext) new DefaultConfigurationContextBuilder().build();
        ctx1.addPropertySources(sharedSource);
        DefaultConfigurationContext ctx2 = (DefaultConfigurationContext) new DefaultConfigurationContextBuilder().build();
        ctx2.addPropertySources(sharedSource);
        DefaultConfigurationContext ctx3 = (DefaultConfigurationContext) new DefaultConfigurationContextBuilder().build();
        ctx3.addPropertySources(new MockedPropertySource());

        assertThat(ctx1).isEqualTo(ctx1);
        assertThat(ctx1).isNotEqualTo(null);
        assertThat("aString").isNotEqualTo(ctx1);
        assertThat(ctx2).isEqualTo(ctx1);
        assertThat(ctx1).isNotEqualTo(ctx3);
        assertThat(ctx2.hashCode()).isEqualTo(ctx1.hashCode());
        assertThat(ctx1.hashCode()).isNotEqualTo(ctx3.hashCode());
        String spaces = new String(new char[70 - sharedSource.getName().length()]).replace("\0", " ");
        System.out.println(ctx1.toString());
        assertThat(ctx1.toString().contains(sharedSource.getName() + spaces)).isTrue();
    }
}
