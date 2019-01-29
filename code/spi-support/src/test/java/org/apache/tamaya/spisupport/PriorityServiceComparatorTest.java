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
package org.apache.tamaya.spisupport;

import org.apache.tamaya.spisupport.propertysource.SystemPropertySource;
import org.junit.Test;

import javax.annotation.Priority;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by atsticks on 12.09.16.
 */
public class PriorityServiceComparatorTest {

    @Test
    public void compare() throws Exception {
        assertThat(PriorityServiceComparator.getInstance().compare("a", "b")==0).isTrue();
        assertThat(PriorityServiceComparator.getInstance().compare(getClass(), getClass())==0).isTrue();
        assertThat(PriorityServiceComparator.getInstance().compare(new A(), new SystemPropertySource())==-1).isTrue();
        assertThat(PriorityServiceComparator.getInstance().compare(new SystemPropertySource(), new A())==1).isTrue();
    }

    @Priority(100)
    private static final class A{}

}
