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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

/**
 *
 * @author William.Lieurance 2018-02-11
 */
public class ReflectionUtilTest<E> {
    
    private final List<E> reflectable = new ArrayList<>();
    private final Multi<Integer> multi = new Multi<>();
    
    /**
     * Test of getParametrizedType method, of class ReflectionUtil.
     * @throws java.lang.Exception
     */
    @Test
    public void testGetParametrizedType() throws Exception {
        Field stringListField = this.getClass().getDeclaredField("reflectable");
        ParameterizedType genericListType = (ParameterizedType) stringListField.getGenericType();
        
        assertThat(ReflectionUtil.getParametrizedType(reflectable.getClass()).toString()).isEqualTo(genericListType.toString());
        assertThat(ReflectionUtil.getParametrizedType(multi.getClass()).getRawType().getTypeName()).isEqualTo(First.class.getName());
        assertThat(ReflectionUtil.getParametrizedType(Object.class)).isNull();
    }
    
    private interface First<T> {}
    private interface Second<T> {}
    private class Multi<T> implements First<T>, Second<T> {};
    
}
